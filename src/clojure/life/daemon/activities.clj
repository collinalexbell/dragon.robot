(ns life.daemon.activities
  (:require [room-cleaning-algo.core :as clean]
            [life.daemon.brocas-area :as brocas]))

(clean/change-output-fn (fn [output] (brocas/speak output)))

(defn clean-room []
  (clean/-main))
