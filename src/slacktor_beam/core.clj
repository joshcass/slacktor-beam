(ns slacktor-beam.core
  (require [clj-slack.users :as users]
           [clj-slack.channels :as channels]
           [clojure.tools.logging :as log]
           [slacktor-beam.logging]
           [clojure.tools.nrepl.server :as repl]
           [clojure.set :refer [difference]]))

(def token (System/getenv "SLACK_OAUTH_TOKEN"))
(def conn-info {:api-url "https://slack.com/api" :token token})
(def dj-swig "C06BTMB4N")

(def members (atom #{}))
(def monitoring-users (atom false))

#_(println "found new users: " (difference current @members))

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
                (log/info (channels/invite conn-info dj-swig "U02MYKGQB"))))
            (Thread/sleep 10000))))

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
