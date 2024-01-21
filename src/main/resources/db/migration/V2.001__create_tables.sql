DROP TABLE IF EXISTS item;
CREATE TABLE item
(
    id                   SERIAL PRIMARY KEY,
    externalId           VARCHAR(512) NOT NULL,
    name                 VARCHAR(255) NOT NULL,
    store                VARCHAR(128) NOT NULL,
    url                  VARCHAR(255) NOT NULL,
    imageUrl             VARCHAR(255) NOT NULL,
    desiredPrice         FLOAT        NOT NULL,
    latestPrice          FLOAT,
    latestPriceTimestamp TIMESTAMP,
    trackingEnabled      BIT          NOT NULL,
    UNIQUE KEY unique_index (externalId, store)
);

DROP TABLE IF EXISTS item_price_history;
CREATE TABLE item_price_history
(
    id        SERIAL PRIMARY KEY,
    itemId    VARCHAR(512) NOT NULL,
    store     VARCHAR(128) NOT NULL,
    price     FLOAT        NOT NULL,
    timestamp TIMESTAMP    NOT NULL
);

DROP TABLE IF EXISTS email_history;
CREATE TABLE email_history
(
    id                 SERIAL PRIMARY KEY,
    itemId             BIGINT UNSIGNED NOT NULL,
    store              VARCHAR(128)    NOT NULL,
    price              FLOAT           NOT NULL,
    emailSentTimestamp TIMESTAMP       NOT NULL
)

