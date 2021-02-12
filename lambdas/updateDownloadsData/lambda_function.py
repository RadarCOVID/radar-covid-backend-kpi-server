import json
import boto3
import csv
import sys
import logging
import psycopg2
from urllib import parse
from os import environ

headerIosFile = ['Fecha', 'Unidades de app']
headerAndroidFile = ['Date', 'Package Name', 'Daily Device Installs', 'Daily Device Uninstalls',
                     'Daily Device Upgrades', 'Total User Installs', 'Daily User Installs', 'Daily User Uninstalls',
                     'Active Device Installs', 'Install events', 'Update events', 'Uninstall events']
columnAndroid = 'nm_downloads_android'
columnIos = 'nm_downloads_ios'
charsetIos = 'utf-8'
charsetAndroid = 'utf-16'
datePatternIos = 'DD/MM/YY'
datePatternAndroid = 'YYYY-MM-DD'

db_schema = environ.get('DB_SCHEMA', 'kpi')
db_host = environ.get('DB_HOST', 'localhost')
db_port = environ.get('DB_PORT', '5432')
db_user = environ.get('DB_USER', 'radarcovid')
db_pass = environ.get('DB_PASS', 'radarcovid')
db_database = environ.get('DB_NAME', 'RADARCOVID')

iosFile = environ.get('IOS_FILE', 'radar_covid-unidades_de_app')
androidFile = environ.get('ANDROID_FILE', 'stats_installs_installs_es.gob.radarcovid')

logger = logging.getLogger()
logger.setLevel(environ.get('PYTHON_LOGLEVEL', 'INFO'))

def lambda_handler(event, context):
    conn = None
    try:
        logger.info('Starting process...')
        s3_resource = boto3.resource('s3')
        conn = connect_db()
        logger.debug(event)
        for item in event['Records']:
            fileName = parse.unquote_plus(item['s3']['object']['key'])
            bucket = item['s3']['bucket']['name']
            if iosFile in fileName:
                dataToInsertIos = download_csv(s3_resource, fileName, headerIosFile, charsetIos, bucket)
                update_db(conn, dataToInsertIos, columnIos, headerIosFile.index('Fecha'),
                          headerIosFile.index('Unidades de app'), datePatternIos)
                logger.info('Updated iOS data')
            elif androidFile in fileName:
                dataToInsertAndroid = download_csv(s3_resource, fileName, headerAndroidFile, charsetAndroid, bucket)
                update_db(conn, dataToInsertAndroid, columnAndroid, headerAndroidFile.index('Date'),
                          headerAndroidFile.index('Daily User Installs'), datePatternAndroid)
                logger.info('Updated Android data')

        logger.info('Import Finished.')
    except Exception as e:
        raise e
    finally:
        disconnect_db(conn)


def download_csv(s3_resource, file, header, charset, bucket):
    logger.info('About to read: ' + file)
    returnList = []
    s3_object = s3_resource.Object(bucket, file)
    data = s3_object.get()['Body'].read().decode(charset).splitlines()
    lines = csv.reader(data)
    start = 0
    for line in lines:
        logger.debug(line)
        if (start == 1):
            returnList.append(line)
        elif (line == header):
            start = 1

    logger.info('Loaded {} lines'.format(len(returnList)))
    return returnList


def update_db(conn, data, column, dateIndex, downloadsIndex, datePattern):
    with conn.cursor() as cur:
        for reg in data:
            query = "UPDATE " + db_schema + ".statistics_radar" \
                    " SET " + column + " = " + reg[downloadsIndex] + \
                    " WHERE fc_statistics_date = TO_TIMESTAMP('" + reg[dateIndex] + "', '" + datePattern + "')"
            logger.debug(query)
            cur.execute(query)
        conn.commit()


def connect_db():
    try:
        conn = psycopg2.connect(dbname=db_database, host=db_host, port=db_port, user=db_user, password=db_pass)
        logger.info("SUCCESS: Connection to RDS PostgreSQL instance succeeded")
    except Exception as e:
        logger.error("ERROR: Unexpected error: Could not connect to PostgreSQL instance.")
        logger.error(e)
        sys.exit()
    return conn


def disconnect_db(conn):
    try:
        if (conn != None):
            conn.close()
    except Exception as e:
        raise e
