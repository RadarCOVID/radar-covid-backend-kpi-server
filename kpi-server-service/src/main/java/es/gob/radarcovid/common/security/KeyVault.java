/*
 * Copyright (c) 2020 Secretaría de Estado de Digitalización e Inteligencia Artificial
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package es.gob.radarcovid.common.security;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class KeyVault {

    private final HashMap<String, KeyPair> pairVault = new HashMap<>();

    private final static List<Method> externalPublicProviders = new ArrayList<>();
    private final static List<Method> externalPrivateProviders = new ArrayList<>();

    public static String getBase64Key(String key) {
        return new String(Base64.getDecoder().decode(key));
    }

    public static String loadKey(String key) throws IOException {
        String keyLoaded = key;
        InputStream in = null;
        if (key.startsWith("classpath:/")) {
            in = new ClassPathResource(key.substring(11)).getInputStream();
            keyLoaded = readAsStringFromInputStreamAndClose(in);
        } else if (key.startsWith("file:/")) {
            in = new FileInputStream(key);
            keyLoaded = readAsStringFromInputStreamAndClose(in);
        }
        return getBase64Key(keyLoaded);
    }

    private static String readAsStringFromInputStreamAndClose(InputStream inputStream) throws IOException {
        String result = "";
        try {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			bufferedReader.lines().forEach(stringBuilder::append);
            result = stringBuilder.toString();
        } finally {
            inputStream.close();
        }
        return result;
    }

    public static boolean registerNewPublicEncodingProvider(Class<?> clazz, String functionName) {
        try {
            var method = clazz.getDeclaredMethod(functionName, String.class, String.class);
            if (!Modifier.isStatic(method.getModifiers()))
                return false;
            if (!method.getReturnType().isAssignableFrom(PublicKey.class))
                return false;
            externalPublicProviders.add(method);
            return true;
        } catch (NoSuchMethodException | SecurityException e) {
            log.error("Exception registering new public encoding provider", e);
            return false;
        }
    }

    public static boolean registerNewPrivateEncodingProvider(Class<?> clazz, String functionName) {
        try {
            var method = clazz.getDeclaredMethod(functionName, String.class, String.class);
            if (!Modifier.isStatic(method.getModifiers()))
                return false;
            if (!method.getReturnType().isAssignableFrom(PrivateKey.class))
                return false;

            externalPrivateProviders.add(method);
            return true;
        } catch (NoSuchMethodException | SecurityException e) {
            log.error("Exception registering new private encoding provider", e);
            return false;
        }
    }

    public static void registerDefaultProviders() {
        // private providers
        registerNewPrivateEncodingProvider(KeyVault.class, "loadPrivateKeyFromJavaEncoding");
        registerNewPrivateEncodingProvider(KeyVault.class, "loadPrivateKeyFromPem");

        // public providers
        registerNewPublicEncodingProvider(KeyVault.class, "loadPublicKeyFromJavaEncoding");
        registerNewPublicEncodingProvider(KeyVault.class, "loadPublicKeyFromPem");
        registerNewPublicEncodingProvider(KeyVault.class, "loadPublicKeyFromX509Certificate");
    }

    public KeyVault(KeyVaultEntry... entries)
            throws PrivateKeyNoSuitableEncodingFoundException, PublicKeyNoSuitableEncodingFoundException {
        registerDefaultProviders();

        for (KeyVaultEntry entry : entries) {
            var kp = loadKeyPairFromString(entry);
            this.pairVault.put(entry.pairKey, kp);
        }
    }

    public KeyVault(KeyVaultKeyPair... pairs) {
        registerDefaultProviders();
        for (var pair : pairs) {
            this.pairVault.put(pair.pairKey, pair.keyPair);
        }
    }

    public KeyPair get(String key) {
        return this.pairVault.get(key);
    }

    public void add(KeyVaultEntry entry)
            throws PrivateKeyNoSuitableEncodingFoundException, PublicKeyNoSuitableEncodingFoundException {
        var kp = loadKeyPairFromString(entry);
        if (kp != null) {
            this.pairVault.put(entry.pairKey, kp);
        }
    }

    public void add(KeyVaultKeyPair pair) {
        this.pairVault.put(pair.pairKey, pair.keyPair);
    }

    private KeyPair loadKeyPairFromString(KeyVaultEntry entry)
            throws PrivateKeyNoSuitableEncodingFoundException, PublicKeyNoSuitableEncodingFoundException {
        PrivateKey privateKey = loadPrivateKey(entry.privatePart, entry.algorithm);
        PublicKey publicKey = loadPublicKey(entry.publicPart, entry.algorithm);

        return new KeyPair(publicKey, privateKey);
    }

    public static PrivateKey loadPrivateKey(String privatePart, String algorithm)
            throws PrivateKeyNoSuitableEncodingFoundException {
        PrivateKey key = null;
        for (var provider : externalPrivateProviders) {
            try {
                key = (PrivateKey) provider.invoke(null, privatePart, algorithm);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.warn("externalPrivateProvider failed with reflection error");
            }
            if (key != null) {
                return key;
            }
        }
        throw new PrivateKeyNoSuitableEncodingFoundException();
    }

    public static PrivateKey loadPrivateKeyFromJavaEncoding(String privatePart, String algorithm) {
        try {
            return KeyFactory.getInstance(algorithm)
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privatePart)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Exception loading private key from java encoding", e);
            return null;
        }
    }

    public static PrivateKey loadPrivateKeyFromPem(String privatePart, String algorithm) {
        PemReader readerPem = null;
        PrivateKey result = null;
        try {
            var reader = new StringReader(privatePart);
            readerPem = new PemReader(reader);
            var obj = readerPem.readPemObject();
            result = KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(obj.getContent()));
        } catch (NullPointerException e) {
            log.error("Exception loading private key from PEM", e);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            log.error("Exception loading private key from PEM", e);
        } finally {
            try {
                readerPem.close();
            } catch (IOException e) {
                log.error("Exception closing PEM reader: {}", e.getMessage(), e);
            }
        }
        return result;
    }

    public static PublicKey loadPublicKey(String publicPart, String algorithm)
            throws PublicKeyNoSuitableEncodingFoundException {
        PublicKey key = null;
        for (var provider : externalPublicProviders) {
            try {
                key = (PublicKey) provider.invoke(null, publicPart, algorithm);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                log.warn("externalPublicProvider failed with reflection error");
            }
            if (key != null) {
                return key;
            }
        }
        throw new PublicKeyNoSuitableEncodingFoundException();
    }

    public static PublicKey loadPublicKeyFromJavaEncoding(String publicPart, String algorithm) {
        try {
            return KeyFactory.getInstance(algorithm)
                    .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicPart)));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            log.error("Exception loading public key from java encoding", e);
            return null;
        }
    }

    public static PublicKey loadPublicKeyFromPem(String publicPart, String algorithm) {
        PemReader readerPem = null;
        PublicKey result = null;
        try {
            var reader = new StringReader(publicPart);
            readerPem = new PemReader(reader);
            var obj = readerPem.readPemObject();
            readerPem.close();
            result = KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(obj.getContent()));
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            log.error("Exception loading public key from PEM", e);
        } finally {
            if (readerPem != null) {
                try {
                    readerPem.close();
                } catch (IOException e) {
                    log.error("Exception closing PEM reader: {}", e.getMessage(), e);
                }
            }
        }
        return result;
    }

    public static PublicKey loadPublicKeyFromX509Certificate(String publicPart, String algorithm) {
        try {
            return CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(publicPart.getBytes())).getPublicKey();
        } catch (CertificateException e) {
            log.error("Exception loading public key from X509 certificate", e);
            return null;
        }
    }

    public static class KeyVaultEntry {
        private final String pairKey;
        private final String algorithm;
        private final String privatePart;
        private final String publicPart;

        public KeyVaultEntry(String pairKey, String privatePart, String publicPart, String algorithm) {
            this.pairKey = pairKey;
            this.privatePart = privatePart;
            this.publicPart = publicPart;
            this.algorithm = algorithm;
        }
    }

    public static class KeyVaultKeyPair {
        private final String pairKey;
        private final KeyPair keyPair;

        public KeyVaultKeyPair(String pairKey, KeyPair keyPair) {
            this.pairKey = pairKey;
            this.keyPair = keyPair;
        }
    }

    public static class PrivateKeyNoSuitableEncodingFoundException extends Exception {

        private static final long serialVersionUID = 1623575762871663224L;
    }

    public static class PublicKeyNoSuitableEncodingFoundException extends Exception {

        private static final long serialVersionUID = -1286647270505904967L;
    }

}
