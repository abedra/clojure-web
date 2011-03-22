(ns basic-with-authentication.core-test
  (:use [basic-with-authentication.core] :reload-all)
  (:use [clojure.test])
  (:use (sandbar core
                 stateful-session
                 form-authentication)))

(def session-key :sandbar.stateful-session/session)

(deftest not-found-error
  (let [request {:uri "/error"}
        response (routes request)]
    (testing "not-found"
      (is (= 404 (:status response)))
      (is (re-find #"Not Found" (:body response))))))

(deftest default-page
  (binding [sandbar-session (atom {})]
    (let [request {:uri "/" :request-method :get}
	  response (routes request)]
      (testing "default page"
	(is (= 200 (:status response)))
	(is (re-find #"Hello World" (:body response)))
	(is (re-find #"screen.css" (:body response)))))))

(deftest decorated-default-page
  (binding [sandbar-session (atom {})]
    (let [request {:uri "/" :request-method :get}
	  response (application-routes request)]
      (is (= 302 (:status response)))
      (is (= "/login" ((:headers response) "Location" ))))))

(deftest decorated-login-page
  (binding [sandbar-session (atom {})]
    (let [request {:uri "/login" :request-method :get}
	  response (application-routes request)]
      (is (= 200 (:status response)))
      (is (re-find #"sandbar-form" (:body response)))
      (is (re-find #"Login" (:body response)))
      (is (re-find #"Username" (:body response)))
      (is (re-find #"Password" (:body response))))))

(deftest file-delivery
  (let [request {:uri "/stylesheets/screen.css" :request-method :get}
	response (routes request)]
    (testing "file delivery"
      (is (= 200 (:status response)))
      (is (.exists (response :body)))
      (is (.isFile (response :body)))
      (is (= "screen.css" (.getName (response :body)))))))

(deftest test-login-validator
  (is (= ((login-validator (form-authentication-adapter)) {})
	 {:_validation-errors {:password ["You must supply a password."],
			       :username ["You must supply a valid username."]}}))
  (is (= ((login-validator (form-authentication-adapter))
	  {:username "example"})
	 {:_validation-errors {:password ["You must supply a password."]}
	  :username "example"}))
  (is (= ((login-validator (form-authentication-adapter))
	  {:password "password"})
	 {:_validation-errors {:username ["You must supply a valid username."]}
	  :password "password"}))
  (is (= ((login-validator (form-authentication-adapter))
	  {:username "example" :password "wrong"})
	 {:_validation-errors {:form ["Unable to authenticate user."]}
	  :username "example", :password "wrong"}))
  (is (= ((login-validator (form-authentication-adapter))
	  {:username "example" :password "password"})
	 {:username "example" :password "password"}))
  ;; admin has a secret password
  (is (= ((login-validator (form-authentication-adapter))
	  {:username "admin" :password "secret"})
	 {:username "admin" :password "secret"}))
  ;; unknown users are denided access, even if they know the password
  (is (= ((login-validator (form-authentication-adapter))
	  {:username "unknown" :password "password"})
	 {:_validation-errors {:form ["Unable to authenticate user."]}
	  :username "unknown" :password "password"}))
  ;; unknown users that supply a wrong password, are denied access.
  (is (= ((login-validator (form-authentication-adapter))
	  {:username "unknown" :password "wrong"})
	 {:_validation-errors {:form ["Unable to authenticate user."]}
	  :username "unknown", :password "wrong"})))

