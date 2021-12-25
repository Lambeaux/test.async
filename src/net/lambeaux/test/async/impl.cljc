(ns net.lambeaux.test.async.impl
  (:require #?(:clj [clojure.core.async :as async])
            #?(:cljs [cljs.core.async :as async])))

(defmacro callback-redefs
  "Mutates state. Call (done) to reset to prior state."
  [bindings done & body]
  (let [names (take-nth 2 bindings)
        vals (take-nth 2 (drop 1 bindings))
        orig-val-syms (map (comp gensym #(str % "-orig-val__") name) names)
        temp-val-syms (map (comp gensym #(str % "-temp-val__") name) names)
        binds (map vector names temp-val-syms)
        resets (reverse (map vector names orig-val-syms))
        bind-value (if (:ns &env)
                     (fn [[k v]] (list 'set! k v))
                     ;; TODO - verify behavior on dynamic vars with binding
                     (fn [[k v]] (list 'alter-var-root (list 'var k) (list 'constantly v))))]
    `(let [~@(interleave orig-val-syms names)
           ~@(interleave temp-val-syms vals)]
       ~@(map bind-value binds)
       (let [body-fn# (fn [~done] ~@body)
             done-fn# (fn [] ~@(map bind-value resets))]
         (body-fn# done-fn#)))))

(defn deep-take!
  "Continuously takes until the result is no longer a channel, then runs f and returns
  the final result (that wasn't a channel)."
  [f x]
  (if (instance? #?(:clj  clojure.core.async.impl.channels.ManyToManyChannel
                    :cljs cljs.core.async.impl.channels.ManyToManyChannel)
                 x)
    (async/take! x (partial deep-take! f) false)
    (f))
  x)