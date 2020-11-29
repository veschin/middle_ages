(ns data-fill
  (:require [clojure.java.jdbc :as jdbc]
            [db :refer [db-spec]]
            [honeysql.core :as sql]
            [gen-algo :refer [generate-king]]))

(defn fill-kings []
  (jdbc/insert-multi! db-spec :kings (map generate-king (range 1000))))

(sort (let [kings (jdbc/query
                   db-spec
                   (sql/format {:select [:*]
                                :from [:kings]}))]
        (group-by :age kings)))
;  (jdbc/insert! db-spec :kings {:name "123"
;                               :sex 1
;                               :age 30
;                               :birth_date 930
;                               :death_date 960
;                               :heir_id 2
;                               :years_at_the_throne 20})