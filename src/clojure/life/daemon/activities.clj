(ns life.daemon.activities
  (:require [room-cleaning-algo.core :as clean]
            [life.daemon.brocas-area :as brocas]
            [life.daemon.motor-cortex :as motor-cortex]
            [life.daemon.list-of-motor-commands :as commands]))

(clean/change-output-fn (fn [output]
                          (motor-cortex/move commands/look-up-left)
                          (brocas/speak output)))

(defn clean-room []
  (clean/-main))
