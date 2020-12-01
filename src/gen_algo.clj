(ns gen-algo
  (:require [clojure.java.jdbc :as jdbc]
            [db :refer [db-spec]]
            [clojure.string :refer [capitalize]]
            [honeysql.core :as sql]))


(def names-config (read-string (slurp "src/names_config.edn")))

(defn drop-states []
  (def base-state (atom {:year 0
                         :id-counter 0}))
  (def names-state (atom {})))

(defn generate-king [_]
  (let [{year :year} @base-state
        sex (rand-nth [:women :men])
        age (rand-int 110)
        base-chance (rand-nth [false false true false])
        old? (> age 55)
        title (when (> age 15)
                (capitalize (rand-nth ((case sex
                                         :women :women-titles
                                         :men :men-titles) names-config))))
        name (rand-nth (sex names-config))

        count (get (if (get @names-state name)
                     (swap! names-state update name inc)
                     (swap! names-state assoc name 1)) name)
        first? (if (= 1 count)
                 ""
                 (str " " count))
        titled? (if (and title (if old? true base-chance))
                  (str " " title)
                  "")

        name-with-num (str name
                           first?
                           titled?)
        years-at-the-throne (rand-int age)
        birth-date-formula (when (not (zero? year)) (- year (rem age 3)))
        death-date (:year (swap! base-state update :year #(+ age %)))
        final-sex (case sex
                    :men 1
                    :women 0)
        heir-id (:id-counter (swap! base-state update :id-counter inc))]
    {:name name-with-num
     :sex final-sex
     :age age
     :birth_date birth-date-formula
     :death_date death-date
     :years_at_the_throne years-at-the-throne
     :heir_id heir-id}))

(defn generate-noble [_]
  (let [{year :year} @base-state
        sex (rand-nth [:women :men])
        age (rand-int 110)
        name (rand-nth (sex names-config))
        bias (- year (rand-int 50))
        birth-date-formula (if (pos? bias) bias year)
        death-date (:year (swap! base-state assoc :year (+ age birth-date-formula)))
        final-sex (case sex
                    :men 1
                    :women 0)
        kings (seq (jdbc/query
                    db-spec
                    (sql/format {:select [:k.id]
                                 :from [[:kings :k]]
                                 :where [:between
                                         :k.birth_date
                                         birth-date-formula
                                         death-date]})))
        estate (rand-nth [:barons :dukes :earls])
        relative (rand-int 1000)]
    {:name name
     :sex final-sex
     :age age
     :birth_date birth-date-formula
     :death_date death-date
     :kings_id kings
     :relative_estate estate
     :relative_id relative}))

(defn generate-knight [_]
  (let [{year :year} @base-state
        sex (rand-nth [:women :men])
        age (rand-int 110)
        name (rand-nth (sex names-config))
        bias (- year (rand-int 50))
        base-chance (rand-nth [false false true false])
        old? (> age 55)
        title (when (> age 15)
                (capitalize (rand-nth ((case sex
                                         :women :women-titles
                                         :men :men-titles) names-config))))
        final-sex (case sex
                    :men 1
                    :women 0)
        birth-date-formula (if (pos? bias) bias year)
        death-date (:year (swap! base-state assoc :year (+ age birth-date-formula)))
        kings (seq (jdbc/query
                    db-spec
                    (sql/format {:select [:k.id]
                                 :from [[:kings :k]]
                                 :where [:between
                                         :k.birth_date
                                         birth-date-formula
                                         death-date]})))
        titled? (if (and title (if old? true base-chance))
                  (str " " title)
                  "")
        final-name (str name titled?)
        relative (rand-int 1000)
        estate (rand-nth [:kings :knights])]
    {:name final-name
     :sex final-sex
     :age age
     :birth_date birth-date-formula
     :death_date death-date
     :kings_id kings
     :relative_estate estate
     :relative_id relative}))

(defn generate-peasant [_]
  (let [{year :year} @base-state
        sex (rand-nth [:women :men])
        age (rand-int 40)
        bias (- year (rand-int 70))
        name (rand-nth (sex names-config))
        birth-date-formula (if (pos? bias) bias year)
        death-date (:year (swap! base-state assoc :year (+ age birth-date-formula)))
        kings (seq (jdbc/query
                    db-spec
                    (sql/format {:select [:k.id]
                                 :from [[:kings :k]]
                                 :where [:between
                                         :k.birth_date
                                         birth-date-formula
                                         death-date]})))
        final-sex (case sex
                    :men 1
                    :women 0)]
    {:name name
     :sex final-sex
     :age age
     :kings_id kings}))