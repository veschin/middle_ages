(ns gen-algo)

(def names-config (read-string (slurp "src/names_config.edn")))

(def base-state (atom {:year 0
                       :id-counter 0}))

(def names-state (atom {}))

(defn generate-king [_]
  (let [{year :year} @base-state
        sex (rand-nth [:women :men])
        age (rand-int 110)
        base-chance (rand-nth [false false true false])
        old? (> age 55)
        title (when (> age 15)
                (rand-nth ((case sex
                             :women :women-titles
                             :men :men-titles) names-config)))
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

