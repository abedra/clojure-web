(ns basic-with-authentication.auth
  (:use [sandbar.form-authentication :only [FormAuthAdapter]]
        [sandbar.validation :only [add-validation-error]]))

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
