(ns slacktor-beam.logging
  (:require [clojure.tools.logging :as log]
            [clj-logging-config.log4j :as conf]))

(def layout (org.apache.log4j.PatternLayout. "%d{ISO8601} %-5p %c - %m%n"))

(defn configure-logging!
  ([] (configure-logging! "./slacktor_beam.log"))
  ([logfile]
   (conf/set-loggers!
    ["slacktor-beam"]
    {:level :debug
     :out (org.apache.log4j.DailyRollingFileAppender.
           layout
           logfile
           "'.'yyyy-MM-dd")
     })))
