(ns basic.core-test
  (:use [basic.core] :reload-all)
  (:use clojure.test
	ring.mock.request))

(deftest not-found-error
  (let [request {:uri "/error"}
        response (routes request)]
    (testing "not-found"
      (is (nil? response)))))

(deftest default-page
  (let [request {:uri "/" :request-method :get}
        response (routes request)]
    (testing "default page"
      (is (= 200 (:status response)))
      (is (= "<h2>Hello World</h2>" (:body response))))))

;; the same tests, but using ring.mock.request

(deftest not-found-error-mock
  (let [request (request :get "/error")
        response (routes request)]
    (testing "not-found"
      (is (nil? response)))))

(deftest default-page-mock
  (let [request (request :get "/")
        response (routes request)]
    (testing "default page"
      (is (= 200 (:status response)))
      (is (= "<h2>Hello World</h2>" (:body response))))))
