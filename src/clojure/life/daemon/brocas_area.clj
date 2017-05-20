(ns life.daemon.brocas-area
  (:import [android.speech.tts TextToSpeech]))

(def engine nil)

(defn init [context callback]
  (def engine (TextToSpeech. context callback)))

(defn speak [words]
  (let [utterance-id (.toString (java.util.UUID/randomUUID))]
    (.speak engine
           words
           TextToSpeech/QUEUE_ADD
           nil
           utterance-id)))
