(ns my-newbie.config.mongodb
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :as mq]
            [cprop.core :refer [load-config]]
            [my-newbie.util.date-util :as date-util])
  (:import [com.mongodb MongoClientOptions ServerAddress]))

(def config (load-config :resource "mongodb.edn"))
(def conn (let [^MongoClientOptions opts (mg/mongo-options (:option config))
                 ^ServerAddress sa (mg/server-address (:host-address config) (:port config))]
             (mg/connect sa opts)))

(def db (mg/get-db conn (:db config)))
(def cu (:coll_users config))
(def co (:coll_orders config))

(defn find-user-by-userid [userid]
  (mc/find-one-as-map db cu {:userid userid}))

(defn add-user [userinfo]
  (mc/insert db cu (-> userinfo date-util/createAt date-util/updateAt)))

(defn add-order [orderinfo]
  (mc/insert db co orderinfo)
  )
(defn update-order [orderinfo]
  (mc/upsert db co {:orderid (:orderid orderinfo)} orderinfo)
  )

(defn get-order-by-orderid [orderid]
  (mc/find-one-as-map db co {:orderid orderid} )
  )

(defn clear-users []
  (mc/remove db cu))

(defn clear-orders []
  (mc/remove db co))
