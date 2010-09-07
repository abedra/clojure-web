(ns basic-with-authentication.html
  "Basic compojure Hello World Example with authentication
   and role based authorization using sandbar"
  (:use [hiccup.core :only [html]]
        [hiccup.page-helpers :only [doctype include-css link-to]]
        [sandbar.auth :only [any-role-granted? current-user]]))

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

(defn index []
  (layout "I'm on a Web Page"
          (html
           [:div {:id "greeting"} "Hello World"])))

(defn admin []
  (layout "I'm on a Web Page as an Admin"
          (html
           [:div {:id "greeting"} "Hello Admin"])))
