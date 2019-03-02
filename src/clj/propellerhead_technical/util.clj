(ns propellerhead-technical.util)

(defn parse-int
  "Converts a string to an integer. Defaults to 0."
  ([s] (let [num (re-find #"[0-9]*" (str s))]
         (if (empty? num)
           0
           (Integer/parseInt num)))))

(defn abs "(abs n) is the absolute value of n" [n]
  (cond
    (not (number? n)) (throw (IllegalArgumentException.
                               "abs requires a number"))
    (neg? n) (-' n)
    :else n))