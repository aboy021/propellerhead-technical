(ns propellerhead-technical.routes.home
  (:require [propellerhead-technical.layout :as layout]
            [propellerhead-technical.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [propellerhead-technical.util :as util]))

(defn home-page [request]
  (let [page (-> request
                 (get-in [:query-params "page"])
                 (util/parse-int)
                 (util/abs))
        page-size 30
        to-drop (-> page
                    (dec)
                    (* page-size))]
    (layout/render request "home.html"
                   {:docs      (-> "docs/docs.md" io/resource slurp)
                    :customers (->> (db/all-customers)
                                    (drop to-drop)
                                    (take page-size))})))

(defn about-page [request]
  (layout/render request "about.html"))

(defroutes home-routes
           (GET "/" request (home-page request))
           (GET "/about" request (about-page request)))

