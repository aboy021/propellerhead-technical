(ns propellerhead-technical.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [propellerhead-technical.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[propellerhead-technical started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[propellerhead-technical has shut down successfully]=-"))
   :middleware wrap-dev})
