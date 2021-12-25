(ns net.lambeaux.test.async.core
  (:require [net.lambeaux.test.async.impl :as impl]
            #?(:clj [clojure.core.async :as async])
            #?(:cljs [cljs.test :as test])
            #?(:cljs [cljs.core.async :as async])))

(defn cljs? [env] (boolean (:ns env)))

(defmacro if-cljs
  [then else]
  (if (cljs? &env) then else))

(defmacro go-test
  "Asynchronously execute the test body (in a go block)."
  [& body]
  (if (cljs? &env)
    ;; CLJS
    `(test/async done#
       (impl/deep-take! done#
         (async/go (do ~@body))))
    ;; JVM
    `(async/<!! (async/go (do ~@body)))))

(defmacro go-redefs
  "Async support for with-redefs where bindings persist for the runtime of the (go) block. Supports
  nested (go) blocks."
  [bindings & body]
  (if (cljs? &env)
    ;; CLJS
    `(impl/callback-redefs ~bindings callback#
        (impl/deep-take! callback#
          (async/go (do ~@body))))
    ;; JVM
    `(impl/callback-redefs ~bindings callback#
        (async/go
          (try (do ~@body)
               (finally (callback#)))))))