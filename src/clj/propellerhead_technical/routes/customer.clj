(ns propellerhead-technical.routes.customer
  (:require [compojure.core :refer [defroutes GET POST]]
            [propellerhead-technical.layout :as layout]
            [propellerhead-technical.util :as util]
            [propellerhead-technical.db.core :as db]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn customer-page [customer-id]
  (let [id (util/parse-int customer-id)
        anti-forgery-field (anti-forgery-field)]
    (layout/render {} "customer.html"
                   {:customer           (db/get-customer {:customer-id id})
                    :anti-forgery-token anti-forgery-field})))

(defn update-customer! [form]
  ;todo: validate
  (clojure.pprint/pprint form)
  (let [id (util/parse-int (:customer-id form))]
    (db/update-customer-status! {:status      (:status-radios form)
                                 :customer-id id})))

(defroutes customer-routes
           (GET "/customer" _ (resp/redirect "/"))          ;TODO: should be a 404?
           (GET "/customer/:customer-id{[0-9]+}/"
                [customer-id] (customer-page customer-id))
           (POST "/customer/:customer-id{[0-9]+}/"
                 [customer-id newNote toggleNote statusRadios]
             (do (update-customer! {:customer-id customer-id
                                    :new-note    newNote
                                    :toggle-note toggleNote
                                    :status-radios statusRadios})
                 (resp/redirect (str "/customer/" customer-id "/")))))