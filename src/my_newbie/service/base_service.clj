(ns my-newbie.service.base-service
  (:require [my-newbie.util.response-util :as res]))

(defn hello []
  (res/succResponse {:msg "Hello World!"}))