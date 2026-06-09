# AGENTS.md

## 项目说明
- 这是一个二次元风格的音乐网站，中文名是悦听，项目名是yueting,包含 Java 后端和 Vue3 前端，是标准的前后端分离项目。
- 项目按前后端分离组织。
- 后端目录：`yueting-java`
- 前端目录：`yueting-front`,前端分为访客端和管理后台
- 访客端前端目录：`yueting-front/yueting-front-web`
- 后台管理前端目录：`yueting-front/yueting-front-admin`
- 后端分为:`yueting-common`,`yueting-admin`,`yueting-web`,admin和web分别和前端的admin和web对应，common是公共模块

## 前端核心约束
- 前端使用vue3+javascript,使用elementplus 作为UI框架，使用axios发送请求，使用pinia作为状态管理。
- 模板文件放在`src/views`下，根据模块分目录，通用组件放到 `src/components/`下
- 样式使用scss,所有的 `.scss` 文件放置在 `src/assets/styles/` 目录下，文件命名以,如果多个单词组合使用.连接，比如part.data.scss。
- 新建 `.vue` 文件时，**必须**使用 PascalCase 命名。
- 新建目录时，**必须**使用 kebab-case 命名。
- 前端路由统一管理放在src/router/index.js中
- 前端表格宽度100%铺满整个表格 
- 前端搜索的时候输入框enter可以触发搜索
- 编辑的弹窗单独用一个组件，不要写在列表页面
- 每次执行完操作记得清理多余的文件
- .vue的布局template放最上面，script放中间，style放最下面
- 搜索栏采用el-row 和 el-col布局

## 后端核心约束
- 不要将核心逻辑写在controller层，业务逻辑统一在service层编写。
- 返回统一用VO不要用Map返回。
- 返回格式统一
```json
{
    "status": "success",
    "code": 200,
    "info": "请求成功",
    "data":{}
}
```
- 共用代码在common中实现。
- 统一异常处理，统一返回格式。
- po,dto,vo统一放到entity包下，不用按照模块单独创建包