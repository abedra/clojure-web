(ns basic-with-style.core
  "Basic compojure Hello World Example with a single page.
   Except this one has a little bit of style."
  (:use [ring.adapter.jetty :only [run-jetty]]
        [compojure.core :only [defroutes GET]]
        [hiccup.core :only [html]]
        [hiccup.page-helpers :only [doctype include-css]])
  (:require [compojure.route :as route]))

(defn index []
  (html
   (doctype :html5)
   [:head
    [:title "I'm on a Web"]
    (include-css "/stylesheets/screen.css")]
   [:body
    [:div {:id "header"}
     [:h1 "Basic Compojure Application with Styles"
      [:span [:a {:href "/" :id "home"} "Home"]]]]
    [:div {:id "content"}
     [:div {:id "greeting"} "Hello World"]]]))

(defroutes routes
  (GET "/" [] (index))
  ;; Serve static files out of /. The default folder is
  ;; APP_ROOT/public but can be changed by passing options
  ;; See compojure/src/compojure/route.clj for details
  (route/files "/")
  ;; Also found in route.clj. String here can be replaced
  ;; by a function
  (route/not-found "<h1>Not Found</h1>"))

(defn start []
  "Boot up a non-blocking Jetty instance on port 8080.
   The routes are a var so that code can be recompiled
   with swank and the application doesn't have to be
   restarted with every change"
  (run-jetty (var routes) {:port 8080
                           :join? false}))
