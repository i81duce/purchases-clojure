(ns purchases-clojure.core
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [ring.middleware.params :as p]
            [hiccup.core :as h])
  (:gen-class))

(defn read-purchases []
  ;(println "Please enter a category: Furniture - Alcohol - Toiletries - Shoes - Food - Jewelry")
  (let [purchases (slurp "purchases.csv")
        purchases (str/split-lines purchases)
        purchases (map 
                   (fn [line] (str/split line #","))
                   purchases)
        header (first purchases)
        purchases (rest purchases)
        purchases (map (fn [line]
                         (apply hash-map (interleave header line)))
                   purchases)
        purchases (walk/keywordize-keys purchases)] 
;        text (read-line)
;        purchases (filter (fn [line]
;                            (= text (:category line)))
;                   purchases)]
    ;(spit "filtered_purchases.edn" (pr-str purchases))
    purchases))
    
(defn categories-html [purchases]
  (let [all-categories (map :category purchases)
        unique-categories (set all-categories)
        sorted-categories (sort unique-categories)]
    [:div
      (map (fn [category]
             [:span
                [:a {:href (str "/?category=" category)} category]
              " "])
        sorted-categories)]))

(defn purchase-html [purchases]
  [:ol
    (map (fn [purchases]
           [:li (str (:cvv purchases) " / " (:credit_card purchases) " / " (:customer_id purchases) " / " (:date purchases))])
         purchases)])
(c/defroutes app
  (c/GET "/" request
    (let [params (:params request)
          category (get params "category")
          purchases (read-purchases)
          filtered-purchases (filter (fn [purchase]
                                       (= (:category purchase) category))
                              purchases)]
      
      
      (h/html [:html
                [:body
                   (categories-html purchases)
                   (purchase-html filtered-purchases)]]))))
                 

(defn -main []
  (j/run-jetty (p/wrap-params app) {:port 5000}))
         