(ns my-newbie.util.date-util
  (:import (java.time LocalDateTime)
           (java.time.format DateTimeFormatter)))

(defn now []
  (.format (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss") (LocalDateTime/now)))

(defn createAt [data]
  (assoc data :created_at (now)))

(defn updateAt [data]
  (assoc data :updated_at (now)))
