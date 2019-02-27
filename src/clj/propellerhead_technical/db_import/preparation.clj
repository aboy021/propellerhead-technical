(ns propellerhead-technical.db-import.preparation
  "Functions for reading Marvel character edn files and preparing the data
  for insertion into the database"
  (:require [clojure.edn :as edn])
  (:import (java.io File)))


(def character-keys
  {:description "Description of character. Required"
   :urls        "Links to the detail, wiki, and comiclink. Use Wiki"
   :series      "The comics series they appear in. No."
   :name        "Their name. Required"
   :events      "Events they're in. No."
   :stories     "Stories they're in. No"
   :modified    "Date last modified. No."
   :thumbnail   "Path and extension of thumbnail. Path required. extension required."
   :id          "Marvel Id. Required."
   :resourceURI "Api link. Not required"
   :comics      "Comics they appear in, take :available."})

(defn list-files
  "Lists the files in the characters directory"
  []
  (->> "characters/"
       (new File)
       (.listFiles)
       (map str)
       (sort)
       (vec)))

(defn read-file
  "Reads and deserializes a characters file"
  [f]
  (edn/read-string (slurp f)))

(defn get-characters [file-path]
  (->> (read-file file-path)
       :data
       :results))

(defn detail-url [c]
  (->> c
       :urls
       (filter #(= "detail" (:type %)))
       (map :url)
       (first)))

(defn transform-character [c]
  (->> c
       ((juxt :name
              :description
              :id
              #(get-in % [:comics :available])
              detail-url
              #(get-in % [:thumbnail :path])
              #(get-in % [:thumbnail :extension])
              ))
       (zipmap [:name
                :description
                :id
                :appearances
                :detail-url
                :thumbnail-path
                :thumbnail-extension]
               )))

(defn all-characters []
  (->> (list-files)
       (mapcat get-characters)
       (map transform-character)))

(defn notable-characters []
  (->> (all-characters)
       (filter #(and (< 3 (:appearances %))
                     (not-empty (:description %))))
       (sort-by :appearances)))
