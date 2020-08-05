(ns my-newbie.util.response-util)

(defn failResponse
  ([errcode errmsg]
   {:isSuccess false
    :errcode   errcode
    :errmsg    errmsg
    :result    {}})
  ([response]
   (failResponse (:errcode response) (:errmsg response)))
  )

(defn succResponse
  [result]
  {:isSuccess true
   :errcode   200
   :errmsg    ""
   :result    result})

(defn tokenResponse
  [echostr]
  {:echostr echostr})