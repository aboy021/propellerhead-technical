CREATE TABLE note
(note_id int auto_increment PRIMARY KEY,
 body VARCHAR_IGNORECASE(1024),
 customer_id int,
 date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 date_deleted TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 foreign key (customer_id) references customer(customer_id));
