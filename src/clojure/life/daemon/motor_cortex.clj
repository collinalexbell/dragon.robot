(ns life.daemon.motor-cortex
  (:import [android.bluetooth BluetoothAdapter]
           [android.content Intent]))

(def bt nil)

(defn check-if-bt-is-enabled-and-if-not-request-enablement [activity]
  (if (not (.isEnabled bt))
    (-> BluetoothAdapter/ACTION_REQUEST_ENABLE
        (Intent.)
        (#(.startActivityForResult activity % 1)))))

(defn init [activity]
  (def bt (BluetoothAdapter/getDefaultAdapter))
  (check-if-bt-is-enabled-and-if-not-request-enablement activity))



