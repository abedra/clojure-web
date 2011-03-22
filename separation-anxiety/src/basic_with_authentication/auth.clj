(ns basic-with-authentication.auth
  (:use [sandbar.form-authentication :only [FormAuthAdapter]]
        [sandbar.validation :only [add-validation-error]]))

(defrecord AuthAdapter []
  FormAuthAdapter
  (load-user [this username password]
             (let [login {:username username :password password}]
		 (cond (= username "example")
		       (merge login {:roles #{:user}})
		       (= username "admin")
		       (merge login {:roles #{:admin}}))))
  (validate-password [this]
		     (fn [m]
		       (let [userbase
			     {"example" "password"
			      "admin" "secret"}]
			 (if (= (userbase (:username m)) (:password m) )
			   m
			   (add-validation-error m "Unable to authenticate user."))))))

(defn form-authentication-adapter []
  (merge
   (AuthAdapter.)
   {:username "Username"
    :password "Password"
    :username-validation-error "You must supply a valid username."
    :password-validation-error "You must supply a password."
    :logout-page "/"}))
