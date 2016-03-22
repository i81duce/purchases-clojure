(ns purchases-clojure.core
  (:require [clojure.string :as str]
            [clojure.walk :as walk])
  (:gen-class))
; Furniture, Alcohol, Toiletries, Shoes, Food, Jewelry
(defn -main []
  (println "Please enter a category: Furniture - Alcohol - Toiletries - Shoes - Food - Jewelry")
  (let [purchases (slurp "purchases.csv")
        purchases (str/split-lines purchases)
        purchases (map (fn [line]
                         (str/split line #","))
                   purchases)
        header (first purchases)
        purchases (rest purchases)
        purchases (map (fn [line]
                         (apply hash-map (interleave header line)))
                   purchases)
        purchases (walk/keywordize-keys purchases) 
        text (read-line)
        purchases (filter (fn [line]
                            (= text (:category line)))
                   purchases)]
    (spit "filtered_purchases.edn" (pr-str purchases))
    purchases))
    

         