DROP TABLE IF EXISTS item_metadata;
CREATE TABLE item_metadata
(
    itemId SERIAL PRIMARY KEY,
    color  VARCHAR(255),
    FOREIGN KEY (itemId) REFERENCES item (id)
);

