(defproject basic "0.0.1"
  :description "Basic example compojure application with style"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [ring/ring-jetty-adapter "0.2.5"]
                 [compojure "0.4.1"]
                 [hiccup "0.2.6"]]
  :dev-dependencies [[swank-clojure "1.3.0-SNAPSHOT"]
		     [ring-mock "0.1.1"]])
