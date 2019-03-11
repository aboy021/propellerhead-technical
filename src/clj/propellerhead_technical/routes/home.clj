(ns propellerhead-technical.routes.home
  (:require [propellerhead-technical.layout :as layout]
            [propellerhead-technical.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [propellerhead-technical.util :as util]
            [clojure.string :as s]))

(def page-size 12)

(defn navigation
  ([{num-customers :num-customers
     page-num      :page-num}]
   (navigation page-num num-customers))
  ([page-num num-customers]
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
                  :next     (inc page-num)}))))

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

(defn get-state
  "Parses the current state from the request"
  [request]
  {:page-num     (request->page-num request)
   :search       (-> request
                     (get-in [:query-params "search"])
                     (or ""))
   :status       (-> request
                     (request->status)
                     (or ""))
   :descending?  (-> request
                     (get-in [:query-params "order"])
                     (not= "ascending"))
   :query-params (:query-params request)})

(defn read-docs
  "Reads the docs.md file from resources"
  []
  (-> "docs/docs.md"
      io/resource
      slurp))

(defn sort-customers
  "Sorts a list of customers by appearances"
  [customers descending?]
  (let [comparer (if descending?
                   #(compare %2 %1)
                   #(compare %1 %2))]
    (->> customers
         (sort-by :appearances comparer))))

(defn paginate
  "Selects a page of customers from a seq"
  [customers page-num page-size]
  (let [offset (* (dec page-num)
                  page-size)]
    (->> customers
         (drop offset)
         (take page-size)
         (vec))))

(defn get-customers
  "Retrieves customers from the db, sorts, and paginates them"
  [{descending? :descending?
    page-num    :page-num
    search      :search
    status      :status}]
  (-> (db/customer-search {:search search
                           :status status})
      (sort-customers descending?)
      (paginate page-num page-size)))

(defn get-num-customers
  "Counts the customers in this query"
  [{search :search
    status :status}]
  (-> {:search search
       :status status}
      (db/customer-search-count)
      (:freq)))

(defn update-querystring-for-nav
  [{query-params :query-params
    nav          :nav} nav-key]
  (-> query-params
      (assoc "page" (get nav nav-key))
      (ring.util.codec/form-encode)))

(defn add-customers [state] (assoc state :customers (get-customers state)))
(defn add-docs [m] (assoc m :docs (read-docs)))
(defn add-num-customers [state] (assoc state :num-customers (get-num-customers state)))
(defn add-nav [state] (assoc state :nav (navigation state)))

(defn add-prev-querystring [state]
  (assoc state :previous-query-string
               (update-querystring-for-nav state :previous)))

(defn add-next-querystring [state]
  (assoc state :next-query-string
               (update-querystring-for-nav state :next)))

(defn request->home-model
  [request]
  (let [state (get-state request)]
    (-> state
        (add-docs)
        (add-customers)
        (add-num-customers)
        (add-nav)
        (add-prev-querystring)
        (add-next-querystring))))

(defn home-page
  "Interprets the request and renders the home page"
  [request]
  (let [home-model (request->home-model request)]
    (layout/render request "home.html" home-model)))

(defn about-page [request]
  (layout/render request "about.html"))

(defroutes home-routes
           (GET "/" request (home-page request))
           (GET "/about" request (about-page request)))







#_(pprint (dissoc (zipmap [:things-only-in-a :things-only-in-b :things-in-both]
                          (clojure.data/diff render-params-old home-model)) :things-in-both))

#_(defn render-params-old [request]
    (let [page-num (request->page-num request)
          search (-> request
                     (get-in [:query-params "search"])
                     (or ""))
          status (or (request->status request) "")
          descending? (-> request
                          (get-in [:query-params "order"])
                          (not= "ascending"))
          query-params (:query-params request)


          query {:search search
                 :status status}
          customers (db/customer-search query)
          num-customers (:freq (db/customer-search-count query))
          nav (navigation page-num num-customers)
          offset (* (dec page-num) page-size)
          previous-query-string (-> query-params
                                    (assoc "page" (:previous nav))
                                    (ring.util.codec/form-encode))
          next-query-string (-> query-params
                                (assoc "page" (:next nav))
                                (ring.util.codec/form-encode))
          sorted-customers (->> customers
                                (sort-by :appearances)
                                ((fn [col] (if descending? (reverse col) col))))
          paged-customers (->> sorted-customers
                               (drop offset)
                               (take page-size)
                               (vec))
          docs (read-docs)
          render-params {:docs                  docs
                         :customers             paged-customers
                         :nav                   nav
                         :next-query-string     next-query-string
                         :previous-query-string previous-query-string
                         :search                search
                         :status                status
                         :descending?           descending?}]
      render-params))

