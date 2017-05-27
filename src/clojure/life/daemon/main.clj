(ns life.daemon.main
    (:require [neko.activity :refer [defactivity set-content-view!]]
              [neko.debug :refer [*a]]
              [neko.notify :refer [toast]]
              [neko.resource :as res]
              [neko.find-view :refer [find-view]]
              [neko.threading :refer [on-ui]]
              [life.daemon.brocas-area :as brocas-area]
              [life.daemon.motor-cortex :as motor-cortex])
    (:import android.widget.EditText
             [android.speech.tts TextToSpeech$OnInitListener]
             [android.view View]))

;; We execute this function to import all subclasses of R class. This gives us
;; access to all application resources.
(res/import-all)

(defn notify-from-edit
  "Finds an EditText element with ID ::user-input in the given activity. Gets
  its contents and displays them in a toast if they aren't empty. We use
  resources declared in res/values/strings.xml."
  [activity]
  (let [^EditText input (.getText (find-view activity ::user-input))]
    (toast (if (empty? input)
             (res/get-string R$string/input_is_empty)
             (res/get-string R$string/your_input_fmt input))
           :long)))

(def tts-ready
  (reify TextToSpeech$OnInitListener
    (onInit [this status]
      (if (= status 0)
        (toast "TTS ready")))))

;; This is how an Activity is defined. We create one and specify its onCreate
;; method. Inside we create a user interface that consists of an edit and a
;; button. We also give set callback to the button.
(defactivity life.daemon.MainActivity
  :key :main

  (onCreate
   [this bundle]
   (-> (*a)
       (.getWindow )
       (.getDecorView)
       (.setSystemUiVisibility (bit-or
                                View/SYSTEM_UI_FLAG_LAYOUT_STABLE
                                View/SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                View/SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                View/SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                View/SYSTEM_UI_FLAG_FULLSCREEN
                                View/SYSTEM_UI_FLAG_IMMERSIVE)))
    (.superOnCreate this bundle)
    (motor-cortex/init this)
    (let [mr-robot-bt (motor-cortex/find-mr-robot)]
     (if (not (nil? mr-robot-bt))
       (do
        (def socket
          (motor-cortex/create-socket-with-mr-robot mr-robot-bt))
        (.connect socket))))
    (neko.debug/keep-screen-on this)
    (brocas-area/init this tts-ready)
    (on-ui
      (set-content-view! (*a)
        [:linear-layout {:orientation :vertical
                         :layout-width :fill
                         :layout-height :wrap}
         [:image-view
          {:image R$drawable/fidget_spinner_cat_best
           :layout-width 1080
           :layout-height 1920
           }]]))))
