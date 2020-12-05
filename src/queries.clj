(ns queries
  (:require [clojure.java.jdbc :as jdbc]
            [db :refer [db-spec]]
            [honeysql.core :as sql]))

(defn query*
  [map]
  (jdbc/query db-spec (sql/format map)))

(defn get-king-with-heir
  [king-id]
  (let [king (first (query* {:select [:k.*]
                             :from [[:kings :k]]
                             :where [:= :k.id king-id]}))
        heir (first (query* {:select [:k.*]
                             :from [[:kings :k]]
                             :where [:= :k.id (:heir_id king)]}))]
    [king heir]))

(defn get-noble-and-relative
  [noble-table noble-id]
  (let [noble (query* {:select [:n.*]
                       :from [[noble-table :n]]
                       :where [:= :n.id noble-id]})
        estate (-> noble
                   first
                   :relative_estate
                   (subs 1)
                   keyword)
        relative (query* {:select [:r.*]
                          :from [[noble-table :n]]
                          :join [[estate :r] [:= :r.id :n.relative_id]]
                          :where [:= :n.id noble-id]})]
    [noble relative]))

(defn get-peasant-and-kings
  [peasant-id]
  (let [peasant (first (query* {:select [:p.*]
                                :from [[:peasants :p]]
                                :where [:= :p.id peasant-id]}))
        king-query #(query* {:select [:k.*]
                             :from [[:kings :k]]
                             :where [:= :k.id %]})
        kings (if (some? (:kings_id peasant))
                (flatten (map (comp king-query :id) (read-string (:kings_id peasant))))
                "Have no kings in his/her life...")]
    [peasant kings]))
