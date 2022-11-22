## Diboot SSO 附属平台模板配置更改
- 因Diboot SSO附属平台模块搭建完成，新人使用后需根据下文自行更新配置。
- **[注意]**:以下配置只显示需要更改的，没显示的则不需要更改。

## 数据库修改
- 在模板启动前，需配置数据库，只需要建立一个空数据库，Diboot会自动创建表，之后按正常的Diboot制作流程即可。
- 数据库创建完成，修改管理员账号，把 admin 更改成 '项目名_admin',防止SSO平台同步用户数据时出现相同的管理员账号。

## 后端Log设置
### resources/logback.xml 文件 [log配置文件]
```xml
  <!--log文件名字-->
  <property name="appName" value="demo" />
  <!--log文件保存地址，文件夹自动生成-->
  <property name="logPath" value="/lvzelin/java/demo/log" />
```

## 前端token 命名更改
- diboot-antd-admin\src\store\mutation-types.js  
  中的 ACCESS_TOKEN = 'Access-Token' 更改成  'Access-Token-项目名'
  防止因token名重复导致 令牌失效。

## 后端配置文件修改
### application-dev.yml 文件 [测试环境配置文件]
```
server: 
  port: 8130  #后台端口
  servlet:
    context-path: /demo_sso  #后台api前置口
diboot:
  iam: 
    # SSO 接入配置
    oauth2-client:
      # 客户端ID
      client-id: diboot-demo
      # 客户端密钥
      client-secret: diboot-demo
      # 回调地址；http://前端项目访问路径/callback
      redirect-uri: http://localhost:9529/fault/callback
      # 授权中心获取token地址；http://授权中心地址/oauth/token
      access-token-uri: http://localhost:8100/auth-center/oauth/token
    #用于用户数据同步的链接：http://授权中心地址/user-center/data-sync/
    data-url: http://localhost:8100/user-center/data-sync/
```
### application-prod.yml 文件 [生产环境配置文件]
```
server: 
  port: 8130  #后台端口
  servlet:
    context-path: /demo_sso  #后台api前置口
diboot:
  component:
    file: # 后台上传文件的位置
      storage-directory: /server/file/
  iam: 
    # SSO 接入配置
    oauth2-client:
      # 客户端ID
      client-id: diboot-demo
      # 客户端密钥
      client-secret: diboot-demo
      # 回调地址；http://前端项目访问路径/callback
      redirect-uri: http://localhost:9529/fault/callback
      # 授权中心获取token地址；http://授权中心地址/oauth/token
      access-token-uri: http://localhost:8100/auth-center/oauth/token
    #用于用户数据同步的链接：http://授权中心地址/user-center/data-sync/
    data-url: http://109.120.128.24:8080/sso/user-center/data-sync/
```
## 前端配置文件  [ antd版 ]
### src/router/index.js 文件
```
export default new Router({
  base: '/demo', // 前端使用附加前缀
})
```
### vue.config.js 文件
```
  publicPath: '/demo',
  devServer: {
    // development server port 9529
    port: 9529,
    // If you want to turn on the proxy, please remove the mockjs /src/main.jsL11
    proxy: {
      '/demo_sso': {
        target: 'http://localhost:8130',  // 后台端口
        ws: false,
        changeOrigin: true,
        pathRewrite: {
          '^/demo_sso': '/demo_sso'  // 后台端口前缀
        }
      }
    }
  },
```
### .env.development 文件 [本地测试配置文件]
```
# 客户端ID
VUE_APP_CLIENT_ID=diboot-demo
# 客户端URL（当前前端的访问路径）
VUE_APP_CLIENT_URL=http://localhost:9529/demo
# 授权中心地址
VUE_APP_AUTH_CENTER_URL=http://localhost:8100/auth-center
# 统一平台地址
VUE_APP_UNIFIY_PLATFORM_URL=http://localhost:8050
# 平台端退出路由（实现单点退出）
VUE_APP_SSO_LOGOUT_PATH=http://localhost:8050/logoutall
# --------------------项目基础配置---------------------
# 项目名称
VUE_APP_PROJECT_NAME='模板管理系统'
# 平台跳转名称
VUE_APP_SSO_PLATFORM='智能制造门户'
```
### .env.production 文件 [正式服务器配置文件]
```
# 客户端ID
VUE_APP_CLIENT_ID=diboot-demo
# 客户端URL（当前前端的访问路径）
VUE_APP_CLIENT_URL=http://109.120.128.24:8080/demo
# 授权中心地址
VUE_APP_AUTH_CENTER_URL=http://109.120.128.24:8080/sso/auth-center
# 统一平台地址
VUE_APP_UNIFIY_PLATFORM_URL=http://109.120.128.24:8080/door
# 平台端退出路由（实现单点退出）
VUE_APP_SSO_LOGOUT_PATH=http://localhost:8080/door/logoutall
# --------------------项目基础配置---------------------
# 项目名称
VUE_APP_PROJECT_NAME='模板管理系统'
# 平台跳转名称
VUE_APP_SSO_PLATFORM='智能制造门户'
```

## 程序打包文件名设置
### pom.xml 文件 
```xml
<build>
    .......
    <finalName>demo-sso-1.0.0</finalName>
</build>
```