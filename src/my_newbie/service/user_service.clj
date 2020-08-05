(ns my-newbie.service.user-service
  (:require [my-newbie.config.mongodb :as mg]
            [clojure.tools.logging :as log]
            [my-newbie.util.response-util :as res]
            [clojure.string :as str]))

(defn register [userinfo]
  (let [{:keys [userid pwd]} userinfo exist (mg/find-user-by-userid userid)]
    (cond (< (count pwd) 6) (do (log/error "password is too short")
                                (res/failResponse 40100 "register failed"))
          (some? exist) (do (log/error "this user is already registered")
                            (res/failResponse 40200 "userid already exist"))
          :else (do (mg/add-user userinfo)
                    (log/info userid " register successfully")
                    (res/succResponse "register successfully")))))

(defn login [userinfo]
  (let [{:keys [userid pwd]} userinfo
        exist (mg/find-user-by-userid userid)]
    (cond (nil? exist)
          (do (log/error "this user is not registered")
              (res/failResponse 40100 "userid is not registered"))
          (not= pwd (:pwd (mg/find-user-by-userid userid)))
          (do (log/error "password not correct")
              (res/failResponse 40200 "login failed, password not correct"))
          :else (do (log/info "login successfully")
                    (res/succResponse "login successfully")))
    )
  )

(defn add-order [orderinfo]
  (let [{:keys [orderid ordername]} orderinfo
        exist (mg/get-order-by-orderid (:orderid orderinfo))]
    (cond (some? exist) (do (log/error "this order is already registered")
                            (res/failResponse 40100 "order already exist"))
          (str/ends-with? ordername "!")
          (do (log/error "ordername is invalid")
              (res/failResponse 40200 "ordername is invalid"))
          :else (do (mg/add-order (assoc-in orderinfo [:ordernum] 1))
                    (log/info "orderid: " orderid " add successfully")
                    (res/succResponse "add successfully"))))
  )

(defn consume [orderid]
  (let [order (mg/get-order-by-orderid orderid)]
    (cond (nil? order)
          (do (log/error "order not exist")
              (res/failResponse 40100 "consume failed"))
          (<= (:ordernum order) 0)
          (do (log/error "no more order")
              (res/failResponse 40200 "consume failed"))
          :else (do (mg/update-order (assoc-in order [:ordernum] (dec (:ordernum order))))
                    (log/info "orderid: " orderid " consume successfully, remains " (:ordernum order))
                    (res/succResponse "consume successfully"))))
  )
