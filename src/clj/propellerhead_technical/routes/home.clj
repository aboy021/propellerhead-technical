(ns propellerhead-technical.routes.home
  (:require [propellerhead-technical.layout :as layout]
            [propellerhead-technical.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defn home-page [request]
  (layout/render request "home.html"
                 {:docs      (-> "docs/docs.md" io/resource slurp)
                  :customers (take 30 (db/all-customers))}))

(defn about-page [request]
  (layout/render request "about.html"))

(defroutes home-routes
  (GET "/" request (home-page request))
  (GET "/about" request (about-page request)))

