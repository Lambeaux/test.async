(ns net.lambeaux.test.async.core-test
  (:require [net.lambeaux.test.async.core :refer [go-test go-redefs]]
            [clojure.test :refer [deftest is testing]]
            ;; FIXME - Shouldn't need explicit core.async requirement.
            ;; Should be getting pulled in transitively.
            #?(:cljs [clojure.core.async])))

(def test-stub (constantly 10))

(deftest verify-go-redefs
  (go-test
    (go-redefs [test-stub (constantly 100)]
      (is (= 100 (test-stub))))))
