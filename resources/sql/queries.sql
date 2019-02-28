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
VALUES (  :customer-id
         ,:name
         ,:description
         ,:appearances
         ,:detail-url
         ,:thumbnail-path
         ,:thumbnail-extension
         ,:date-created
         ,:status)

-- :name get-customer :? :1
-- :doc retrieves a customer record given the id
SELECT *
FROM customer
WHERE customer_id = :customer-id

-- :name update-customer-status! :! :n
-- :doc updates an existing customer record's status
UPDATE customer
SET status = :status
    ,date_modified CURRENT_TIMESTAMP
WHERE customer_id = :customer-id

-- :name customer-search :query :*
-- :doc Retrieves customers based on a search string
SELECT *
FROM customer
WHERE name like :query
      or description like :query
      or status like :query