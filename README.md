# my-newbie
## 微信回调接口需要进行内网穿透
这里我用的是ngrok，下载地址https://ngrok.com/，下载后打开terminal，进入ngrok目录，输入ngrok http 80，会生成一个随机域名，这里的域名无法固定，只能使用随机的，将就用吧
## 微信开发注意事项
1.打开https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Requesting_an_API_Test_Account.html，申请测试号
2.填写url和token，url为自己写的回调接口，点击设置微信会发送http请求，带有四个参数，校验后（可选），直接返回echostr字符串即可
3.点击最下方表格，网页服务 -> 网页账号 -> 网页授权获取用户基本信息 修改，填写ngrok生成的域名，例如fbf6c1d54ef7.ngrok.io
4.打开https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html，按照教程来做就行，如果发生48001，先用非静默方式执行一次，之后再换回静默执行就行
