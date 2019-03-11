(ns propellerhead-technical.routes.customer
  (:require [compojure.core :refer [defroutes GET POST]]
            [propellerhead-technical.layout :as layout]
            [propellerhead-technical.util :as util]
            [propellerhead-technical.db.core :as db]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn not-empty? [x] ((complement empty?) x))
(defn not-zero? [x] ((complement zero?) x))

(defn customer-page
  "Renders the customer details for the specified customer"
  [customer-id]
  (let [id (util/parse-int customer-id)
        customer (db/get-customer {:customer-id id})
        notes (db/customer-notes {:customer-id id})]
    (if customer
      (layout/render {} "customer.html"
                     {:customer           customer
                      :notes              notes
                      :anti-forgery-token (anti-forgery-field)})
      (resp/redirect "error.html"))))

(defn update-customer!
  "Updates the customer and creates any new notes"
  [form]
  ;todo: validate
  ;(clojure.pprint/pprint form)
  (let [id (util/parse-int (:customer-id form))
        toggle-note (util/parse-int (:toggle-note form))
        new-note (:new-note form)]
    (do
      ;(clojure.pprint/pprint toggle-note)
      (db/update-customer-status! {:status      (:status-radios form)
                                   :customer-id id})
      (when (not-empty? new-note)
        (db/create-note! {:customer-id id
                          :body        new-note}))
      (when (not-zero? toggle-note)
        (db/delete-note! {:note-id toggle-note})))))

(defroutes
  customer-routes
  (GET "/customer" _ (resp/redirect "/"))                   ;TODO: should be a 404?
  (GET "/customer/:customer-id{[0-9]+}/"
       [customer-id] (customer-page customer-id))
  (POST "/customer/:customer-id{[0-9]+}/"
        [customer-id newNote toggleNote statusRadios]
    (do (update-customer! {:customer-id   customer-id
                           :new-note      newNote
                           :toggle-note   toggleNote
                           :status-radios statusRadios})
        (resp/redirect (str "/customer/" customer-id "/")))))