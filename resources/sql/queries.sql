---------------
-- Customers --
---------------

-- :name create-customer! :! :n
-- :doc creates a new customer record
INSERT INTO customer
(customer_id
 ,name
 ,description
 ,appearances
 ,detail_url
 ,thumbnail_path
 ,thumbnail_extension
 ,date_created
 ,status)
VALUES (:customer-id
         ,:name
         ,:description
         ,:appearances
         ,:detail-url
         ,:thumbnail-path
         ,:thumbnail-extension
         ,:date-created
         ,:status)

-- :name create-customers! :! :n
-- :doc Insert multiple characters with :tuple* parameter type
insert into customer
(customer_id
 ,name
 ,description
 ,appearances
 ,detail_url
 ,thumbnail_path
 ,thumbnail_extension
 ,date_created
 ,status)
values :tuple*:customers

-- :name get-customer :? :1
-- :doc retrieves a customer record given the id
SELECT *
FROM customer
WHERE customer_id = :customer-id

-- :name update-customer-status! :! :n
-- :doc updates an existing customer record's status
UPDATE customer
SET status = :status
    ,date_modified = CURRENT_TIMESTAMP
WHERE customer_id = :customer-id

-- :name customer-search :query :*
-- :doc Retrieves customers based on a search string
SELECT *
FROM customer
WHERE (:search is null
    or :search = ''
    or (name like '%' || :search || '%'
        or description like '%' || :search || '%'))
    and (:status is null
         or :status = ''
         or status = :status)


-- :name customer-search-count :query :1
-- :doc Retrieves customers based on a search string
SELECT count(*) as freq
FROM customer
WHERE (:search is null
    or :search = ''
    or (name like '%' || :search || '%'
        or description like '%' || :search || '%'))
    and (:status is null
         or :status = ''
         or status = :status)

-- :name all-customers :query :*
-- :doc Retrieves all customers.
--      Generally a bad idea but there's little data here
SELECT *
,(CASE :ordering
             WHEN 'desc' THEN (appearances * -1)
             else appearances
             END) as ordering
FROM customer
ORDER BY ordering asc;

-----------
-- Notes --
-----------

-- :name create-note! :! :n
-- :doc creates a new note for the specified customer
insert into note
( body
 ,customer_id
 ,date_created
 ,date_deleted)
 values
 ( :body
  ,:customer-id
  ,CURRENT_TIMESTAMP
  ,null);


-- :name delete-note! :! :n
-- :doc marks a note as deleted
UPDATE note
SET date_deleted = CURRENT_TIMESTAMP
WHERE note_id = :note-id


-- :name customer-notes :query :*
-- :doc Get's all of the notes for the specified customer that are not deleted
SELECT *
FROM note
where customer_id = :customer-id
ORDER BY date_created desc;