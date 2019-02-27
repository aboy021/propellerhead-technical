(ns user
  (:require [propellerhead-technical.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [propellerhead-technical.core :refer [start-app]]
            [propellerhead-technical.db.core]
            [conman.core :as conman]
            [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'propellerhead-technical.core/repl-server))

(defn stop []
  (mount/stop-except #'propellerhead-technical.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn restart-db []
  (mount/stop #'propellerhead-technical.db.core/*db*)
  (mount/start #'propellerhead-technical.db.core/*db*)
  (binding [*ns* 'propellerhead-technical.db.core]
    (conman/bind-connection propellerhead-technical.db.core/*db* "sql/queries.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))


