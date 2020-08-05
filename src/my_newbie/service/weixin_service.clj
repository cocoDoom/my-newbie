(ns my-newbie.service.weixin-service
  (:require [clj-http.client :as client]
            [clojure.tools.logging :as log]
            [cheshire.core :as cheshire]
            [ring.util.codec :as encoder]
            [my-newbie.util.response-util :as res]))

(def APP_ID "wx9d8a3d7b745f7323")
(def APP_SECRET "8057e593c3deb529e1eec299f392b9df")
(def SCOPE "snsapi_userinfo")
(def STATE "123")
(def TIMEOUT "200")
(def TOKEN "weixinToken")

(defn get-weixin-accesstoken
  "Get weixin access token by code"
  [code]
  (try
    (let [result (client/get "https://api.weixin.qq.com/sns/oauth2/access_token"
                             {:query-params {:appid      APP_ID
                                             :secret     APP_SECRET
                                             :code       code
                                             :grant_type "authorization_code"}
                              :timeout      TIMEOUT
                              :as           :json})
          weixin-response (:body result)]
      (log/info "access token response: " weixin-response)
      (if (:errcode weixin-response)
        (do
          (log/error "get access token failed: " (:errcode weixin-response))
          (res/failResponse weixin-response))
        (res/succResponse weixin-response)))
    (catch Exception e
      (do (log/error "get access token error: " (.getMessage e))
          (res/failResponse 50100 (.getMessage e))))))

(defn get-weixin-info
  [access_token openid]
  (try
    (let [result (client/get "https://api.weixin.qq.com/sns/userinfo"
                             {:query-params {:access_token access_token
                                             :openid       openid
                                             :lang         "zh_CN"}
                              :timeout      TIMEOUT
                              :as           :json})
          weixin-response (:body result)]
      (log/info "userinfo response: " weixin-response)
      (if (:errcode weixin-response)
        (do
          (log/error "get open user info failed: " (:errcode weixin-response))
          (res/failResponse weixin-response))
        (res/succResponse weixin-response)))
    (catch Exception e
      (log/error ("get open user info error: " (.getMessage e))))))

(defn weixin-info
  [code state]
  (let [accessTokenResponse (get-weixin-accesstoken code)
        success (:isSuccess accessTokenResponse)
        result (:result accessTokenResponse)]
    (if success
      (get-weixin-info (:access_token result) (:openid result))
      accessTokenResponse)))

(defn weixin-auth-info
  []
  (let [query-params {:appid         APP_ID
                      :redirect_uri  (encoder/url-encode "http://81bbfe4a0bee.ngrok.io/api/weixin-info")
                      :response_type "code"
                      :scope         SCOPE
                      :state         STATE}]
    (format "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s#wechat_redirect"
            (:appid query-params)
            (:redirect_uri query-params)
            (:response_type query-params)
            (:scope query-params)
            (:state query-params))))

(defn config-token [args]
  (let [echostr (:echostr args)]
    (log/info "\nsignature "
              (:signature args) "\nechostr "
              (:echostr args) "\ntimestamp "
              (:timestamp args) "\nnonce "
              (:nonce args))
    echostr)
  )
