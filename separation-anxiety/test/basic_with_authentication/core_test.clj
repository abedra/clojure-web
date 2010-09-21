(ns basic-with-authentication.core-test
  (:use [basic-with-authentication.core] :reload-all)
  (:use clojure.test
	ring.middleware.params)
  (:use (sandbar core
                 stateful-session
                 form-authentication
                 [test :only (t)])
	)
  (:require [basic-with-authentication.auth :as auth]
            [basic-with-authentication.html :as html]
	    [ring.util.test :as tu]))

(def session-key :sandbar.stateful-session/session)

(deftest not-found-error
  (let [request {:uri "/error"}
        response (routes request)]
    (testing "not-found"
      (is (= 404 (:status response)))
      (is (re-find #"Not Found" (:body response))))))

(deftest default-page
  (binding [*sandbar-session* (atom {})]
    (let [request {:uri "/" :request-method :get}
	  response (routes request)]
      (testing "default page"
	(is (= 200 (:status response)))
	(is (re-find #"Hello World" (:body response)))
	(is (re-find #"screen.css" (:body response)))))))

(deftest decorated-default-page
  (binding [*sandbar-session* (atom {})]
    (let [request {:uri "/" :request-method :get}
	  response (application-routes request)]
      (is (= 302 (:status response)))
      (is (= "/login" ((:headers response) "Location" ))))))

(deftest decorated-login-page
  (binding [*sandbar-session* (atom {})]
    (let [request {:uri "/login" :request-method :get}
	  response (application-routes request)]
      (is (= 200 (:status response)))
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

(comment
  (deftest test-login-page
    (binding [*sandbar-session* (atom {})]
      (let [request {:uri "/login" :request-method :post}
	    response (application-routes request)]
	response))))

(comment
  (def wrapped-echo (wrap-params identity)))

(comment
  (deftest test-login-page-login
    (binding [*sandbar-session* (atom {})]
      (let [request {:uri "/login" :request-method :post
		     :query-string nil
		     :content-type "application/x-www-form-urlencoded"
		     :body         (tu/string-input-stream "username=example&password=password&submit=Login")}
	    response (application-routes #_request (wrapped-echo request))]
	response))))



