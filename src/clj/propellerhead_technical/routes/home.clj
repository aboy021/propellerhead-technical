(ns propellerhead-technical.routes.home
  (:require [propellerhead-technical.layout :as layout]
            [propellerhead-technical.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [propellerhead-technical.util :as util]
            [clojure.string :as s]))

(def page-size 12)

(defn navigation [page-num num-customers]
  (let [at-start (<= page-num 1)
        at-end (>= page-num (/ num-customers page-size))]

    (cond (and at-start at-end) {:previous -1
                                 :current  1
                                 :next     -1}
          at-start {:previous -1
                    :current  page-num
                    :next     2}
          at-end {:previous (dec page-num)
                  :current  page-num
                  :next     -1}

          :else {:previous (dec page-num)
                 :current  page-num
                 :next     (inc page-num)})))

(defn request->page-num [request]
  (-> request
      (get-in [:query-params "page"])
      (or "1")
      (util/parse-int)
      (util/abs)))

(def valid-statuses #{"prospective"
                      "current"
                      "non-active"})

(defn request->status [request]
  (-> request
      (get-in [:query-params "status"])
      (or "")
      (s/lower-case)
      (valid-statuses)))

(defn home-page [request]
  (let [page-num (request->page-num request)
        search (get-in request [:query-params "search"])
        status (request->status request)
        query {:search search
               :status status
               :limit  page-size
               :offset (* (dec page-num) page-size)}
        customers (db/customer-search query)
        num-customers (:freq (db/customer-search-count query))
        nav (navigation page-num num-customers)]
    (clojure.pprint/pprint (:query-params request))
    (clojure.pprint/pprint query)
    (layout/render request "home.html"
                   {:docs                  (-> "docs/docs.md" io/resource slurp)
                    :customers             customers
                    :nav                   nav
                    :next-query-string     (-> request
                                               (:query-params)
                                               (assoc :page (:next nav))
                                               (ring.util.codec/form-encode))

                    :previous-query-string (-> request
                                               (:query-params)
                                               (assoc :page (:next nav))
                                               (ring.util.codec/form-encode))
                    :search                search
                    :status                (or status "")})))

(defn about-page [request]
  (layout/render request "about.html"))

(defroutes home-routes
           (GET "/" request (home-page request))
           (GET "/about" request (about-page request)))

