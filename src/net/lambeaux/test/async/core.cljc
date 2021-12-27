(ns net.lambeaux.test.async.core
  (:require [clojure.core.async :as async :include-macros true]
            [net.lambeaux.test.async.impl :as impl]))

(defn cljs? [env] (boolean (:ns env)))

(defmacro go-test
  "Asynchronously execute the test body (in a go block)."
  [& body]
  (if (cljs? &env)
    `(impl/go-test-js ~body)
    `(impl/go-test-jvm ~body)))

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