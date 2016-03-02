(ns slacktor-beam.core
  (:gen-class)
  (require [clj-slack.users :as users]
           [clj-slack.channels :as channels]
           [clojure.tools.logging :as log]
           [slacktor-beam.logging]
           [clojure.tools.nrepl.server :as repl]
           [clojure.set :refer [difference]]))

(def token (System/getenv "SLACK_OAUTH_TOKEN"))
(def conn-info {:api-url "https://slack.com/api" :token token})

(defn channels-list []
  (:channels (channels/list conn-info)))

(defonce dj-swig
  (->> (channels-list)
       (filter #(= "dj_swig" (:name %)))
       (first)
       (:id)))

(def members (atom #{}))
(def monitoring-users (atom false))

(defn monitor-users! []
  (reset! monitoring-users true)
  (future (while @monitoring-users
            (let [info (channels/info conn-info dj-swig)
                  current (into #{} (get-in info [:channel :members]))
                  escapees (difference @members current)]
              (log/info "pulled new members: " (difference current @members))
              ;; add all current members to roster
              (swap! members into current)
              ;; invite any members who have left
              (doseq [e escapees]
                (log/info "Re-inviting escapee: " e)
                (log/info (channels/invite conn-info dj-swig e))))
            (Thread/sleep 10000))
          (log/info "Stopping monitoring loop...")))

(defn stop-monitoring-users! []
  (reset! monitoring-users false))

;; Host repl
(def repl-server (atom nil))

(defn start-repl! []
  (if @repl-server
    (repl/stop-server @repl-server))
  (reset! repl-server (repl/start-server :port 7890))
  (.addShutdownHook (Runtime/getRuntime) (Thread. (fn [] (repl/stop-server @repl-server)))))

(defn -main [& args]
  (slacktor-beam.logging/configure-logging!)
  (start-repl!)
  (monitor-users!)
  (log/info "***** Engaging Slacktor Beam *****", args))
