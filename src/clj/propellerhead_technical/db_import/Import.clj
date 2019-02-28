(ns propellerhead-technical.db-import.Import
  "Functions for importing marvel characters into the database"
  (:require [propellerhead-technical.config :refer [env]]
            [propellerhead-technical.db.core :refer [*db*] :as db]
            [propellerhead-technical.db-import.prepare :as prepare]
            [mount.core :as mount]
            [luminus-migrations.core :as migrations]
            [clojure.java.jdbc :as jdbc]))

(defn start-env []
  (mount/start
    #'propellerhead-technical.config/env
    #'propellerhead-technical.db.core/*db*)
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn do-import

  []
  (start-env)
  (map db/create-customer!
       (prepare/notable-characters)))


