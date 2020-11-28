(ns db
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.java.shell :refer [sh]]))

(def db-spec
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"})

(def base-fields
  [[:id :integer :primary :key :autoincrement]
   [:name :text]
   [:age :integer]
   [:birth_date :date]
   [:death_date :date]])

(def kings-table
  [:kings
   (into base-fields
         [[:heir_id :integer]
          [:years_at_the_throne :integer]])])

(def earls-table
  [:earls
   (into base-fields
         [[:relative_id :integer]
          [:king_id :integer]])])

(def dukes-table
  [:dukes
   (into base-fields
         [[:relative_id :integer]
          [:king_id :integer]])])

(def barons-table
  [:barons
   (into base-fields
         [[:relative_id :integer]
          [:king_id :integer]])])

(def knights-table
  [:knights
   (into base-fields
         [[:relative_id :integer]
          [:king_id :integer]])])

(def peasants-table
  [:peasants
   [[:id :integer :primary :key :autoincrement]
    [:name :text]
    [:age :integer]
    [:king_id :integer]]])

(defn delete-database []
  (sh "rm" "db/database.db"))

(defn create-tables
  [table fields]
  (try
    (jdbc/db-do-commands
     db-spec
     (jdbc/create-table-ddl  table fields))
    (catch Exception e
      (str "We got an error -> " e "; "))))

(defn create-database []
  (let [tables [kings-table earls-table dukes-table
                barons-table knights-table peasants-table]]
    (delete-database)
    (map (fn [[table fields]] (create-tables table fields)) tables)))

(comment
  "GET TABLES"
  (:out (sh "sqlite3" "db/database.db" ".tables"))

  "GET SCHEMA"
  (:out (sh "sqlite3" "db/database.db" ".schema"))

  ;
  )