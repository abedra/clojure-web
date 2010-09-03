(ns basic-with-authentication.core
  "Basic compojure Hello World Example with authentication
   and role based authorization using sandbar"
  (:use [ring.adapter.jetty :only [run-jetty]]
        [compojure.core :only [defroutes GET]]
        [hiccup.core :only [html]]
        [hiccup.page-helpers :only [doctype include-css link-to]]
        [sandbar.auth :only [with-security any-role-granted?]]
        [sandbar.form-authentication :only [form-authentication-routes form-authentication FormAuthAdapter]]
        [sandbar.stateful-session :only [wrap-stateful-session]]
        [sandbar.validation :only [add-validation-error]])
  (:require [compojure.route :as route]))

(defn layout [title & body]
  (html
   (doctype :html5)
   [:head
    (include-css "/stylesheets/screen.css")
    [:title title]]
   [:body
    [:div {:id "header"}
     [:h1 "Basic Compojure Application with Styles"
      [:span [:a {:href "/" :id "home"} "Home"]]
      [:span (link-to "/logout" "Logout")]
      (when (any-role-granted? :admin)
        [:span (link-to "/admin" "Admin")])]]
    [:div {:id "content"} body ]]))

(defrecord AuthAdapter []
  FormAuthAdapter
  (load-user [this username password]
             (cond (= username "example")
                   {:username "example" :password "password" :roles #{:user}}
                   (= username "admin")
                   {:username "admin" :password "password" :roles #{:admin}}))
  (validate-password [this]
                     (fn [m]
                       (if (= (:password m) "password")
                         m
                         (add-validation-error m "Unable to authenticate user.")))))

(defn form-authentication-adapter []
  (merge
   (AuthAdapter.)
   {:username "Username"
    :password "Password"
    :username-validation-error "You must supply a valid username."
    :password-validation-error "You must supply a password."
    :logout-page "/"}))

(defn index []
  (layout "I'm on a Web Page"
          (html
           [:div {:id "greeting"} "Hello World"])))

(defn admin []
  (layout "I'm on a Web Page as an Admin"
          (html
           [:div {:id "greeting"} "Hello Admin"])))

(def security-policy
  [#".*\.(css|js|png|jpg|gif)$" :any
   #"/login.*" :any
   #"/logout.*" :any
   #"/permission-denied.*" :any
   #"/admin" :admin
   #"/" #{:user :admin}])

(defroutes routes
  (GET "/" [] (index))
  (GET "/admin" [] (admin))
  (GET "/permission-denied" [] {:status 403 :body "Permission Denied"})
  (form-authentication-routes (fn [_ c] (layout "Login" c)) (form-authentication-adapter))
  ;; Serve static files out of /. The default folder is
  ;; APP_ROOT/public but can be changed by passing options
  ;; See compojure/src/compojure/route.clj for details
  (route/files "/")
  ;; Also found in route.clj. String here can be replaced
  ;; by a function
  (route/not-found "<h1>Not Found</h1>"))

;; create a call chain for all routes to handle authentication
(def application-routes (-> routes
                            (with-security security-policy form-authentication)
                            wrap-stateful-session))

(defn start []
  "Boot up a non-blocking Jetty instance on port 8080.
   The routes are a var so that code can be recompiled
   with swank and the application doesn't have to be
   restarted with every change"
  (run-jetty (var application-routes) {:port 8080
                                       :join? false}))
