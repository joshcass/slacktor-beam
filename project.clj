(defproject slacktor-beam "0.1.0-SNAPSHOT"
  :description "Those who enter DJ Swig may never leave"
  :url "https://github.com/worace/slacktor-beam"
  :main slacktor-beam.core
  :profiles {:uberjar {:aot :all}}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.julienxx/clj-slack "0.5.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [clj-logging-config "1.9.12"]
                 [org.clojure/tools.nrepl "0.2.10"]])
