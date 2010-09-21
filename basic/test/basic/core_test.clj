(ns basic.core-test
  (:use [basic.core] :reload-all)
  (:use [clojure.test]))

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
