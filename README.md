# Englite3

## 描述

一个简单自用的安卓背单词软件，前身是[Englite2](https://github.com/Wldcmzy/Mess-Mess/tree/master/EngLite2)

客户端由Android(Java)实现，服务端由Python3实现

## 特性&功能&实现情况


- [x] **单词`熟练度`变化逻辑**
	
  1. 每个单词有三种标识——`等级`，`指数`，`加入规划标志`
  2. 若用户忘记一个单词，其`等级`和`指数`归零，并且本轮背诵完成后，`等级`和`指数`不会增加
  3. 若用户初见即记住一个单词，其`指数`+1， `等级`在满足指数增幅的条件下随机
  
- [x] **简单总览词库`熟练度`**
  
  - 把所有单词按`等级`排序，显示一系列百分比梯度内`等级`最高的单词
  
- [x] **自定义背诵单词的`熟练度`范围**
  
  - 用户可自定义每次背诵单词的数量，以及单词的`等级`范围
  
- [x] **全随机的单词出现逻辑**
  
  - 划定一次背诵的单词集后，以在单词集中全随机的方式呈现单词
  
- [x] **支持单词分步加入背诵**
	- Englite2中没有分布加入背诵的功能，除非像词库作者那样边加词边背，否则体验极不友好
  - Englite3中这个问题通过单词分步加入背诵得以解决
  
- [x] **可自定义的云端服务器**
  
  - 服务器为python实现，用户可以使用python脚本一键开服
  - 客户端中可以自定义改变服务器地址配置
  
- [x] **云端身份验证（若数据加密没实现，用处不大）**


  - 服务器加入用户验证功能，不通过验证的用户无法使用云端功能
  - 用户需要提前在客户端配置`账号`和`密码`，用于云端身份验证
  - 合法的`账号`和`密码`由管理员通关管理工具自行加入，而非注册
- [ ] **传输过程中的数据加密**
- 每次会话随机AES密钥，首先以RSA密文传递密钥，之后本次会话使用AES加密通信
- [ ] **其他安全措施**

  - [ ] 防重放攻击
- [x] **从云端导入原始词库文件**
- [ ] **把词库文件保存到云端**
- [ ] **从云端导入用户词库文件**
- [ ] **从本地导入词库**
  - 太菜了不会搞，暂时搁置
- [ ] **把词库导出到本地**
- [ ] **删除词库**
- [x] **词库快速制作工具**

  - 根据文件给出的单词英文对接有道翻译接口制作: 见[Englite2](https://github.com/Wldcmzy/Mess-Mess/tree/master/EngLite2)

  - 根据文件给出的单词全部信息制作: 见[Englite2](https://github.com/Wldcmzy/Mess-Mess/tree/master/EngLite2)（未完成）