(ns life.daemon.list-of-motor-commands)

(def look-up-right
  {0 [{:id 0 :amount 40 :duration 1000} {:id 1 :amount -40 :duration 1000}]
   1 [{:id 2 :amount 20 :duration 1000} {:id 3 :amount -20 :duration 1000}]})

(def look-up-left
  {0 [{:id 0 :amount -40 :duration 1000} {:id 1 :amount 40 :duration 1000}]
   1 [{:id 2 :amount 20 :duration 1000} {:id 3 :amount -20 :duration 1000}]})

(defn look-down [amount time-in-ms]
  {1 [{:id 2 :amount (* -1 amount) :duration time-in-ms}]})


(defn look-up [amount time-in-ms]
  {1 [{:id 2 :amount  amount :duration time-in-ms}]})

