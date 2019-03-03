(ns propellerhead-technical.routes.home
  (:require [propellerhead-technical.layout :as layout]
            [propellerhead-technical.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [propellerhead-technical.util :as util]))

(defn navigation [page-num num-pages]
  (let [at-start (<= page-num 1)
        at-end (>= page-num num-pages)]
    (cond at-start {:previous -1
                    :current  page-num
                    :next     2}
          at-end {:previous (dec num-pages)
                  :current  page-num
                  :next     -1}
          :else {:previous (dec page-num)
                 :current  page-num
                 :next     (inc page-num)})))

(defn home-page [request]
  (let [page-index (-> request
                       (get-in [:query-params "page"])
                       (util/parse-int)
                       (util/abs))
        page-size 12
        to-drop (-> page-index
                    (dec)
                    (* page-size))
        customers (db/all-customers)
        num-pages (-> customers
                      (count)
                      (/ page-size)
                      (int))
        page-num page-index
        nav (navigation page-num num-pages)]
    (clojure.pprint/pprint request)
    (layout/render request "home.html"
                   {:docs      (-> "docs/docs.md" io/resource slurp)
                    :customers (->> customers
                                    (drop to-drop)
                                    (take page-size))
                    :nav       (navigation page-num num-pages)})))

(defn about-page [request]
  (layout/render request "about.html"))

(defroutes home-routes
           (GET "/" request (home-page request))
           (GET "/about" request (about-page request)))

