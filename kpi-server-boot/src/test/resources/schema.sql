DROP SCHEMA IF EXISTS KPI CASCADE;
CREATE SCHEMA KPI;

CREATE SEQUENCE KPI.SQ_NM_ID_KPI_TYPE;
CREATE SEQUENCE KPI.SQ_NM_ID_KPI;

CREATE TABLE KPI.KPI_TYPES (
    NM_ID_KPI_TYPE  BIGINT DEFAULT nextval('KPI.SQ_NM_ID_KPI_TYPE'),
    DE_KPI_NAME     CHAR VARYING(100),
    IN_ENABLED      BOOLEAN DEFAULT FALSE,
    CONSTRAINT PK_KPI_TYPE
        PRIMARY KEY (NM_ID_KPI_TYPE)
);

CREATE TABLE KPI.KPIS (
    NM_ID_KPI           INTEGER DEFAULT nextval('KPI.SQ_NM_ID_KPI'),
    NM_ID_KPI_TYPE      INTEGER,
    FC_CREATION_DATE    TIMESTAMP DEFAULT current_timestamp,
    NM_VALUE            INTEGER,
    NM_SO_TYPE          INTEGER,
    FC_VALUE            TIMESTAMP,
    CONSTRAINT PK_KPI
        PRIMARY KEY (NM_ID_KPI),
    CONSTRAINT FK_KPI_TYPE
        FOREIGN KEY (NM_ID_KPI_TYPE)
            REFERENCES KPI.KPI_TYPES (NM_ID_KPI_TYPE)
);

ALTER SEQUENCE KPI.SQ_NM_ID_KPI_TYPE
    OWNED BY KPI.KPI_TYPES.NM_ID_KPI_TYPE;

ALTER SEQUENCE KPI.SQ_NM_ID_KPI
    OWNED BY KPI.KPIS.NM_ID_KPI;
