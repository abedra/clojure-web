(ns basic.core
  "Basic compojure Hello World Example with a single page."
  (:use [ring.adapter.jetty :only [run-jetty]]
        [compojure.core :only [defroutes GET]]))

(defroutes routes
  (GET "/" [] "<h2>Hello World</h2>"))

(defn start []
  "Boot up a non-blocking Jetty instance on port 8080.
   The routes are a var so that code can be recompiled
   with swank and the application doesn't have to be
   restarted with every change"
  (run-jetty (var routes) {:port 8080
                           :join? false}))
