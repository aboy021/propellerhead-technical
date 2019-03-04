(ns propellerhead-technical.test.db.core
  (:require [propellerhead-technical.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [java-time :as jt]
            [propellerhead-technical.config :refer [env]]
            [mount.core :as mount]))

(def spiderman
  {:description         "ABC123 .Bitten by a radioactive spider, high school student Peter Parker gained the speed, strength and powers of a spider. Adopting the name Spider-Man, Peter hoped to start a career using his new abilities. Taught that with great power comes great responsibility, Spidey has vowed to use his powers to help people.",
   :date-created        (-> "2019-02-06T18:06:19-0500"
                            (subs 0 19)
                            (jt/local-date-time)),
   :appearances         3500,
   :name                "Spider-Man",
   :thumbnail-path      "http://i.annihil.us/u/prod/marvel/i/mg/3/50/526548a343e4b",
   :thumbnail-extension "jpg",
   :status              "current",
   :customer-id         0,
   :detail-url          "http://marvel.com/comics/characters/1009610/spider-man?utm_campaign=apiRef&utm_source=7e8580151d73b9fa709851f6dd87ba51"})

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'propellerhead-technical.config/env
      #'propellerhead-technical.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-customer
  (jdbc/with-db-transaction
    [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-customer! t-conn spiderman)))
    (is (= spiderman
           (-> (db/get-customer t-conn {:customer-id 0})
               (dissoc :date-modified))))))


(deftest test-customer-search
  (jdbc/with-db-transaction
    [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-customer! t-conn spiderman)))
    (is (= spiderman
           (-> (db/customer-search t-conn {:search "ABC123"
                                           :status nil
                                           ;:ordering "desc"
                                           ;:offset 0
                                           ;:limit 12
                                           })
               (first)
               (dissoc :date-modified))))))


(deftest test-create-note
  (jdbc/with-db-transaction
    [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-customer! t-conn spiderman)))
    (let [note {:customer-id 0
                :body        "This is a test note"}]
      (is (= 1 (db/create-note! t-conn note)))
      (is (= note
             (-> (db/customer-notes t-conn note)
                 (first)
                 (select-keys [:customer-id :body])))))))

(deftest test-delete-note
  (jdbc/with-db-transaction
    [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-customer! t-conn spiderman)))
    (let [note {:customer-id 0
                :body        "This is a test note"}]
      (is (= 1 (db/create-note! t-conn note)))
      (let [db-note (first (db/customer-notes t-conn note))]
        ;(clojure.pprint/pprint db-note)
        (is (= 1 (db/delete-note! t-conn (select-keys db-note [:note-id]))))))))
