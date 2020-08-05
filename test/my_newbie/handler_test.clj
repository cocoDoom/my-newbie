(ns my-newbie.handler-test
  (:require [clojure.test :refer :all]
            [my-newbie.config.mongodb :as mg]
            [my-newbie.handler :refer :all]
            [ring.mock.request :as mock]
            [cheshire.core :as cheshire]))

(defn db_init "init mongodb"
  [test-fn]
  (mg/clear-orders)
  (mg/clear-users)
  (test-fn))

(deftest test-app
  ;测试hello
  (testing "hello route"
    (let [response (app (mock/request :get "/api/hello"))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= body {:isSuccess true
                   :errcode   200
                   :errmsg    ""
                   :result    {:msg "Hello World!"}}))))

  ;测试微信授权接口
  (testing "weixin auth route"
    (let [response (app (mock/request :get "/api/weixin-auth-info"))]
      (is (= (:status response) 308))))

  ;测试微信获取用户信息接口，code是随机的，所以只能是错的
  (testing "weixin info route"
    (let [response (app (-> (mock/request :get "/api/weixin-info")
                            (mock/query-string {:code "code" :state "123"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode]) {:isSuccess false
                                                       :errcode   40029}))))

  ;用户注册接口 -> 成功
  (testing "user register successfully route"
    (let [response (app (-> (mock/request :post "/user/register")
                            (mock/json-body {:userid "9"
                                             :pwd    "123456"
                                             :sex    "man"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode :result]) {:isSuccess true
                                                               :errcode   200
                                                               :result    "register successfully"}))))

  ;用户注册接口 -> 失败 -> 密码不符合校验规则
  (testing "user register password invalid route"
    (let [response (app (-> (mock/request :post "/user/register")
                            (mock/json-body {:userid "10"
                                             :pwd    "12345"
                                             :sex    "man"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode]) {:isSuccess false
                                                       :errcode   40100}))))

  ;用户注册接口 -> 失败 -> 用户已经注册过
  (testing "user register exist route"
    (let [response (app (-> (mock/request :post "/user/register")
                            (mock/json-body {:userid "9"
                                             :pwd    "123456"
                                             :sex    "man"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode]) {:isSuccess false
                                                       :errcode   40200}))))
  ;用户登陆接口 -> 成功
  (testing "user login successfully route"
    (let [response (app (-> (mock/request :post "/user/login")
                            (mock/json-body {:userid "9"
                                             :pwd    "123456"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode :result]) {:isSuccess true
                                                               :errcode   200
                                                               :result    "login successfully"}))))
  ;用户登陆接口 -> 失败 -> 用户不存在,尚未注册
  (testing "user login hasn't register route"
    (let [response (app (-> (mock/request :post "/user/login")
                            (mock/json-body {:userid "10"
                                             :pwd    "123456"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode]) {:isSuccess false
                                                       :errcode   40100}))))

  ;用户登陆接口 -> 失败 -> 用户密码错误
  (testing "user login password not correct route"
    (let [response (app (-> (mock/request :post "/user/login")
                            (mock/json-body {:userid "9"
                                             :pwd    "1234567"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode]) {:isSuccess false
                                                       :errcode   40200}))))

  ;添加商品接口 -> 成功
  (testing "order add successfully route"
    (let [response (app (-> (mock/request :post "/user/order")
                            (mock/json-body {:orderid   "3"
                                             :ordername "watermelon"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode :result]) {:isSuccess true
                                                               :errcode   200
                                                               :result    "add successfully"}))))

  ;添加商品接口 -> 失败 -> 商品信息已经存在
  (testing "order exist route"
    (let [response (app (-> (mock/request :post "/user/order")
                            (mock/json-body {:orderid   "3"
                                             :ordername "watermelon"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode]) {:isSuccess false
                                                       :errcode   40100}))))

  ;添加商品接口 -> 失败 -> 商品名称不符合校验规则
  (testing "order add ordername invalid route"
    (let [response (app (-> (mock/request :post "/user/order")
                            (mock/json-body {:orderid   "4"
                                             :ordername "peach!"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode]) {:isSuccess false
                                                       :errcode   40200}))))

  ;商品消费接口 -> 成功
  (testing "order consume successfully route"
    (let [response (app (-> (mock/request :post "/user/consume")
                            (mock/json-body {:orderid "3"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode :result]) {:isSuccess true
                                                               :errcode   200
                                                               :result    "consume successfully"}))))

  ;商品消费接口 -> 失败 -> 商品不存在
  (testing "order consume, order not exist route"
    (let [response (app (-> (mock/request :post "/user/consume")
                            (mock/json-body {:orderid "5"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode]) {:isSuccess false
                                                       :errcode   40100}))))

  ;商品消费接口 -> 失败 -> 商品库存不足
  (testing "order consume ordernum invalid route"
    (let [response (app (-> (mock/request :post "/user/consume")
                            (mock/json-body {:orderid "3"})))
          body (cheshire/parse-string (slurp (:body response)) true)]
      (is (= (:status response) 200))
      (is (= (select-keys body [:isSuccess :errcode]) {:isSuccess false
                                                       :errcode   40200}))))

  )

;将db_init重的方法，看成一个方法,一起执行,类似于打包执行
(use-fixtures :once db_init)

;运行当前命名空间的测试方法
(run-tests)
