(ns basic-with-style.core-test
  (:use [basic-with-style.core] :reload-all)
  (:use [clojure.test]))

(deftest not-found-error
  (let [request {:uri "/error"}
        response (routes request)]
    (testing "not-found"
      (is (= 404 (:status response)))
      (is (re-find #"Not Found" (:body response))))))

(deftest default-page
  (let [request {:uri "/" :request-method :get}
        response (routes request)]
    (testing "default page"
      (is (= 200 (:status response)))
      (is (re-find #"Hello World" (:body response)))
      (is (re-find #"screen.css" (:body response))))))

(deftest file-delivery
  (let [request {:uri "/stylesheets/screen.css" :request-method :get}
	response (routes request)]
    (testing "file delivery"
      (is (= 200 (:status response)))
      (is (.exists (response :body)))
      (is (.isFile (response :body)))
      (is (= "screen.css" (.getName (response :body)))))))
