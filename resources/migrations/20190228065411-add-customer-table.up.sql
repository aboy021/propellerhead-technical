CREATE TABLE customer
(customer_id int PRIMARY KEY,
 name VARCHAR(64),
 description VARCHAR(1000),
 appearances int,
 detail_url VARCHAR(256),
 thumbnail_path VARCHAR(64),
 thumbnail_extension VARCHAR(3),
 date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 status VARCHAR(16));
