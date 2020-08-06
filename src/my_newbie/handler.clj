(ns my-newbie.handler
  (:require [compojure.api.sweet :refer :all]
            [my-newbie.service.base-service :as base]
            [my-newbie.domain.schema :refer :all]
            [my-newbie.util.response-util :refer :all]
            [ring.util.http-response :refer :all]
            [ring.adapter.jetty :refer :all]
            [my-newbie.service.weixin-service :as weixin]
            [ring.middleware.reload :refer [wrap-reload]]
            [schema.core :as s]
            [my-newbie.service.user-service :as user]))

(def app
  (api
    {:swagger
     {:ui   "/"
      :spec "swagger.json"
      :data {:info {:title       "Newbie"
                    :description "Jiliguala newbie project"}
             :tags [{:name "api" :description "Api"}
                    {:name "weixin" :description "Weixin api"}]}}}

    (context "/api" []
      :tags ["api"]

      ;:summary接口注释
      (GET "/hello" []
        :summary "Hello World"
        (ok (base/hello)))

      ;:return 限定返回结果的格式和类型
      ;:tag 接口标签
      ;:query-params 取url中的参数 :- 转换类型
      (GET "/weixin-info" []
        :return Response
        :tags ["weixin"]
        :query-params [code :- String, state :- String]
        :summary "微信回调接口"
        (ok (weixin/weixin-info code state)))

      ;https://mp.weixin.qq.com/debug/cgi-bin/sandboxinfo?action=showinfo&t=sandbox/index
      ;测试号管理界面，设置URL(http://3b32m96722.wicp.vip/api/config-info?)后
      ;通过返回请求重的echostr，验证token，在这一步可以做校验，可以参照官网
      ;微信这个接口需要443或者80接口
      (GET "/config-info" [& args]
        :tags ["weixin"]
        ;:return mys/tokenRes
        :summary "微信token设置接口"
        (ok (weixin/config-token args)))

      ;把链接复制到微信中，选择在微信打开
      ;weixin-auth-info将uri重新定向到上面的方法weixin-info，来获取用户信息
      (GET "/weixin-auth-info" []
        :tags ["weixin"]
        :summary "需要在微信客户端中运行"
        (permanent-redirect (weixin/weixin-auth-info)))
      )
    (context "/user" []
      :tags ["user"]

      ;:body POST请求体中的格式
      ;optional-key 可选参数
      (POST "/register" []
        :summary "注册用户"
        :body [body {:userid                    s/Str
                     :pwd                       s/Str
                     (s/optional-key :sex)      s/Str
                     (s/optional-key :nickname) s/Str
                     (s/optional-key :country)  s/Str
                     (s/optional-key :province) s/Str
                     }]
        (ok (user/register body)))

      ;在response修改session和identity
      (POST "/logout" []
        :summary "登出用户"
        :body [_ {:userid s/Str}]
        (assoc-in (ok) [:session :identity] nil))

      ;Response map is nil可能的原因
      ;1.请求方式不对
      ;2.请求路径不对
      (POST "/login" []
        :summary "登陆用户"
        :body [body {:userid s/Str
                     :pwd    s/Str}]
        (let [response (user/login body)]
          (if (:isSuccess response)
            (assoc-in (ok response) [:session :identity] body)
            (ok response))))

      (POST "/order" []
        :summary "添加商品"
        :body [body {:orderid   s/Str
                     :ordername s/Str}]
        (ok (user/add-order body))
        )

      (POST "/consume" []
        :summary "用户下单"
        :body [body {:orderid s/Str}]
        (ok (user/consume (:orderid body))))
      )
    )
  )

(def reload-app
  (wrap-reload #'app))

(defn -main
  [& args]
  (run-jetty reload-app {:port (Integer/valueOf (or (System/getenv "port") "80"))}))