(ns data-fill
  (:require [clojure.java.jdbc :as jdbc]
            [db :refer [db-spec create-database]]
            [honeysql.core :as sql]

            [gen-algo :refer [drop-states]]))

(defn fill-kings []
  (drop-states)
  (jdbc/insert-multi! db-spec :kings (map gen-algo/generate-king (range 1000))))

(defn fill-nobles []
  (drop-states)
  (jdbc/insert-multi! db-spec :barons (map gen-algo/generate-noble (range 1000)))
  (drop-states)
  (jdbc/insert-multi! db-spec :dukes (map gen-algo/generate-noble (range 1000)))
  (drop-states)
  (jdbc/insert-multi! db-spec :earls (map gen-algo/generate-noble (range 1000))))

(defn fill-knights []
  (drop-states)
  (jdbc/insert-multi! db-spec :knights (map gen-algo/generate-knight (range 1000))))

(defn fill-peasants []
  (drop-states)
  (jdbc/insert-multi! db-spec :peasants (map gen-algo/generate-peasant (range 3000))))

(comment
  (do
    (create-database)
    (fill-kings)
    (fill-nobles)
    (fill-knights)
    (fill-peasants))
  ;
  (letfn [(get-table [table]
            (jdbc/query
             db-spec
             (sql/format {:select [:*]
                          :from [table]})))]
    (take 100 (get-table :peasants)))
  ;
  )