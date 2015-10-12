# zhihu-digger

[![Build Status](https://travis-ci.org/lhcpig/zhihu-digger.svg?branch=master)](https://travis-ci.org/lhcpig/zhihu-digger)

Requires JDK 1.8 or higher


#用途
知乎没有特别关注的功能。我们如果想特别关心某一个人的回答或者点赞，除了一直手动刷新Ta的首页动态，没有其他办法了。
用这个zhihu-digger，当Ta有心动态时，就会发邮件给自己。

#使用步骤
1. 修改src/main/resources/config.properties文件，填写下面几个参数：
  + people 关心的人名称，支持多人(用逗号隔开)，比如 "zhouyuan,lhcpig"
  + host 发信服务器，比如126邮箱的是"smtp.126.com"
  + fromAddress 发信邮箱的地址
  + fromPassword 发信邮箱的密码
  + toAddressList 收信邮箱地址，支持多个收信邮箱，比如 "user1@example.com,user2@example.com"
2. mvn 打包: `mvn package`。打包生成`zhihu-digger.jar`文件，默认在target目录下。
3. 执行`java -jar zhihu-digger.jar`，完毕。

#注
因为只是一个工具，所以代码写的比较随意，见谅


