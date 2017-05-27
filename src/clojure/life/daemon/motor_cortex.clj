(ns life.daemon.motor-cortex
  (:import [android.bluetooth BluetoothAdapter]
           [android.content Intent]))

(def bt nil)

(defn -throw-if-no-bt []
  (if (= bt nil)
    (throw (Exception. "BT is not enabled"))))

(defn check-if-bt-is-enabled-and-if-not-request-enablement [activity]
  (if (not (.isEnabled bt))
    (-> BluetoothAdapter/ACTION_REQUEST_ENABLE
        (Intent.)
        (#(.startActivityForResult activity % 1)))))

(defn init [activity]
  (def bt (BluetoothAdapter/getDefaultAdapter))
  (check-if-bt-is-enabled-and-if-not-request-enablement activity))


(defn find-mr-robot
  "I need to find Mr Robot. That is, find the bluetooth pair with my robot."
  []
  (-throw-if-no-bt)
  (let [paired-devices (.getBondedDevices bt)]
    (->>
     paired-devices
     (map
      (fn [d] {:name (.getName d) :address (.getAddress d) :device d}))
     (filter
      (fn [d]
        (= (d :name) "mr_robot")))
     first)))


(defn create-socket-with-mr-robot [mr-robot]
   (.createRfcommSocketToServiceRecord
    (:device mr-robot)
    (.getUuid
     (first (.getUuids (:device mr-robot))))))

(defn connect-with-mr-robot [socket]
  (.connect socket))


(defn talk-with-mr-robot [socket what-to-say]
  (.write (.getOutputStream socket)
           (byte-array what-to-say)))

(defn listen-to-mr-robot [socket]
  (let [istream (.getInputStream socket)]
    (loop [bytes-available (.available istream)]
      (if (> bytes-available 0)
        (do (print (.read istream))
            (recur (.available istream)))))))

(def ex [
         0x02 ;number of motors
         0x00 ;motor id
         0x04 ;num-commands
         0x00 ;command id
         0x00 0x14 ;amount0
         0x03 0xE8 ;duration0
         0x01 ;command id
         0xFF 0xEC ;amount1
         0x03 0xE8 ;duration1
         0x02 ;command id
         0x00 0x14 ;amount0
         0x03 0xE8 ;duration0
         0x03 ;command id
         0xFF 0xEC ;amount1
         0x03 0xE8 ;duration1
         0x01 ;motor id
         0x04 ;num-commands
         0x04 ;command id
         0x00 0x14 ;amount0
         0x03 0xE8 ;duration0
         0x05 ;command id
         0xFF 0xEC ;amount1
         0x03 0xE8 ;duration1
         0x06 ;command id
         0x00 0x14 ;amount0
         0x03 0xE8 ;duration0
         0x07 ;command id
         0xFF 0xEC ;amount1
         0x03 0xE8 ;duration1
         ])





