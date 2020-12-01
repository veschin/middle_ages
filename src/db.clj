(ns db
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.java.shell :refer [sh]]))

(def db-spec
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"})

(def base-fields
  [[:id :integer :primary :key :autoincrement]
   [:sex :bool]
   [:name :text]
   [:age :integer]
   [:birth_date :integer]
   [:death_date :integer]])

(def noble-fields
  [[:relative_id :integer]
   [:relative_estate :text]
   [:kings_id :integer]])

(def kings-table
  [:kings
   (into base-fields
         [[:years_at_the_throne :integer]
          [:heir_id :integer]])])

(def earls-table
  [:earls
   (into base-fields
         noble-fields)])

(def dukes-table
  [:dukes
   (into base-fields
         noble-fields)])

(def barons-table
  [:barons
   (into base-fields
         noble-fields)])

(def knights-table
  [:knights
   (into base-fields
         noble-fields)])

(def peasants-table
  [:peasants
   [[:id :integer :primary :key :autoincrement]
    [:sex :bool]
    [:name :text]
    [:age :integer]
    [:kings_id :integer]]])

(def delete-database #(sh "rm" "db/database.db"))

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
    (doseq [[table fields] tables]
      (create-tables table fields))))

(comment
  "GET TABLES"
  (:out (sh "sqlite3" "db/database.db" ".tables"))

  "GET SCHEMA"
  (:out (sh "sqlite3" "db/database.db" ".schema"))


  (:out (sh "sqlite3" "db/database.db" "SELECT * FROM dukes"))
  ;
  )
