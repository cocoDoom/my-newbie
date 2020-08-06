# my-newbie
## 微信回调接口需要进行内网穿透
这里我用的是ngrok，下载地址https://ngrok.com/ 下载后打开terminal，进入ngrok目录，输入ngrok http 80，会生成一个随机域名，这里的域名无法固定，只能使用随机的，将就用吧

## 微信开发注意事项
1.打开https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Requesting_an_API_Test_Account.html 申请测试号

2.填写url和token，url为自己写的回调接口，点击设置微信会发送http请求，带有四个参数，校验后（可选），直接返回echostr字符串即可

3.点击最下方表格，网页服务 -> 网页账号 -> 网页授权获取用户基本信息 修改，填写ngrok生成的域名，例如fbf6c1d54ef7.ngrok.io

4.打开https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html 按照教程来做就行，非静默授权获取code -> 携带code重定向到用户信息获取接口 -> 使用code获取access_token -> 使用access_token获得用户信息

## Travis Ci使用注意事项
travis ci 是一个持续集成的工具，且只支持github

使用流程，代码上传至github，打开https://travis-ci.org ，点击右上角头像settings，打开下面要部署的项目的开关，然后点项目右侧的settings，设置Environment Variables的name，value，除了limit concurrent jobs，其他开关都打开，之后点开左上方的Dashboard

push代码后会自动构建，在dashboard中可以查看日志
