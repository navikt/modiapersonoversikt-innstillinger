CREATE TABLE innstillinger(
    ident   VARCHAR     NOT NULL,
    navn    VARCHAR     NOT NULL,
    verdi   VARCHAR     NOT NULL,
    PRIMARY KEY (ident, navn)
);

CREATE TABLE sist_oppdatert(
    ident       VARCHAR         PRIMARY KEY     NOT NULL,
    tidspunkt   TIMESTAMP       DEFAULT NOW()   NOT NULL
)
