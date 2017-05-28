(ns life.daemon.motor-cortex
  (:require [life.daemon.list-of-motor-commands :as commands])
  (:import [android.bluetooth BluetoothAdapter]
           [android.content Intent]
           [java.nio ByteBuffer]))

(def bt nil)
(def actiity nil)
(def socket nil)

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
  (let [s
        (.createRfcommSocketToServiceRecord
         (:device mr-robot)
         (.getUuid
          (first (.getUuids (:device mr-robot)))))]
    (def socket s)
    s))

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


(defn unsigned-to-signed
  [unsigned]
  (if (> unsigned 127)
    (+ -128 (- unsigned 128))
    unsigned))

(defn bytes? [x]
  (if (nil? x)
    false
    (= (Class/forName "[B")
       (.getClass x))))

(defn clj-motor-cmds->choreo-bytes
  [commands]
  (->> [
        ;;Number of motors
        (count commands)
        (map
         (fn [[key data]]
           [
            ;;motor id
            key

            ;;num-commands
            (count data)

            (map
             (fn [procedure]
               [(byte (:id procedure))
                (-> (ByteBuffer/allocate 4)
                    (.putShort (:amount procedure))
                    (.putShort (:duration procedure))
                    (.array))])
             data)])
         commands)]
       (flatten)
       (map #(if (not (bytes? %))
               (vector (unsigned-to-signed %))
               %))

       (mapcat seq)
       (byte-array)))

(defn move [commands]
  (if (nil? socket)
    (throw
     (Exception. "Can not move without a connection to the arduino"))
    (talk-with-mr-robot socket (clj-motor-cmds->choreo-bytes commands))))

