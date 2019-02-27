(ns propellerhead-technical.db-import.marvel-download
  "One off code for downloading marvel characters"
  (:require [clj-http.client :as client]
            [clojure.edn :as edn])
  (:import (java.security MessageDigest)
           (java.time LocalDateTime)
           (java.io File)))

(defn config
  "Retrieve the dev-config file which contains the Marvel keys"
  []
  (edn/read-string (slurp "dev-config.edn")))

(defn md5
  [^String s]
  (->> s
       .getBytes
       (.digest (MessageDigest/getInstance "MD5"))
       (BigInteger. 1)
       (format "%032x")))

(defn get-characters
  "Retrieve 100 characters from the Marvel database, starting at `offset`"
  [offset]
  (let [ts (str (LocalDateTime/now))
        {private-key :marvel/private-key
         public-key  :marvel/public-key} (config)
        hash (md5 (str ts private-key public-key))]
    (:body
      (client/get "https://gateway.marvel.com:443/v1/public/characters"
                  {:accept       :json
                   :as           :json
                   :query-params {"ts"      ts
                                  "orderBy" "name"
                                  "limit"   "100"
                                  "offset"  (str offset)
                                  "apikey"  public-key
                                  "hash"    hash}}))))

(defn file-name [offset]
  (str "characters/" offset ".edn"))

(defn write-file
  "Write an edn file of character data named after the offset"
  [data offset]
  (let [f (file-name offset)]
    (spit f (pr-str data))))

(defn num-characters [data]
  (get-in data [:data :count]))

(defn file-exists? [offset]
  (. (new File (file-name offset)) isFile))

(defn save-characters
  "Retrieves 100 characters and saves them as an edn file"
  [offset]
  (if (file-exists? offset)
    100
    (let [data (get-characters offset)]
      (write-file data offset)
      (num-characters data))))

(defn ensure-output-directory
  []
  (let [directory (new File "characters/")]
    (when-not (.exists directory)
      (.mkdir directory))))

(defn download-character-data
  "Retrieves characters in 100 character blocks until no more are found"
  []
  (do
    (ensure-output-directory)
    (loop [offset 100
           affected 100]
      (println affected "records returned")
      (println "retrieving offset " offset)
      (if (zero? affected)
        (print "done")
        (recur (+ offset 100) (save-characters offset))))))

; (:count (:data (:body t)))
;(first (:results (:data (:body t))))