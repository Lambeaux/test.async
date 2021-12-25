(ns net.lambeaux.test.async.core-test
  (:require [net.lambeaux.test.async.core :refer [go-test go-redefs]]
            #?(:clj  [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :as test :refer [deftest is testing]])))

(def test-stub (constantly 10))

(deftest verify-go-redefs
  (go-test
    (go-redefs [test-stub (constantly 100)]
      (is (= 100 (test-stub))))))
