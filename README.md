# 悦听音乐 (Yueting Music)

在线音乐平台，支持歌曲管理、歌手管理、歌单管理、首页推荐、用户收藏点赞、音乐播放等功能。

## 技术栈

**后端：**
- Java 21 + Spring Boot 3.5.4
- MyBatis + MySQL 8.x
- Maven 多模块（yueting-common / yueting-admin / yueting-web）

**前端：**
- Vue 3 + Vite
- Vue Router + Pinia
- Element Plus（UI 组件库）
- SCSS
- Axios

**数据库：**
- MySQL 8.x（utf8mb4）

## 项目结构

```
yueting-music/
├── yueting-music-java/                 # 后端（Maven 多模块）
│   ├── yueting-common/                 # 公共模块（Service / Mapper / Entity）
│   ├── yueting-admin/                  # 管理后台接口（端口 6061）
│   ├── yueting-web/                    # 用户端接口（端口 6060）
│   └── sql/                            # 数据库建表 SQL
│       ├── singer.sql / song.sql / playlist.sql
│       ├── recommend.sql / sys_dict.sql / user.sql
├── yueting-music-front/                # 前端
│   ├── yueting-music-front-web/        # 用户端（Vue3）
│   └── yueting-music-front-admin/      # 管理后台（Vue3）
└── .codex/                             # Codex CLI 配置
```

## 功能模块

| 模块 | 说明 |
| --- | --- |
| 管理后台 | 数据看板、歌曲/歌单/歌手管理、首页推荐配置、字典管理、用户管理 |
| 用户端 | 首页推荐（轮播图+今日推荐+热门列表）、歌单列表、歌手列表、用户注册/登录/个人中心 |
| 用户行为 | 点赞/收藏歌曲歌单歌手、播放历史、播放次数统计 |
| 音乐播放 | 底部固定播放条，支持播放/暂停/进度拖拽 |

## 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.x
- Node.js 20+

## 快速开始

### 1. 创建数据库

在 MySQL 中创建数据库 `yueting`：

```sql
CREATE DATABASE yueting DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

按顺序执行 `yueting-music-java/sql/` 目录下的建表 SQL：
- `singer.sql` — 歌手表
- `song.sql` — 歌曲表
- `playlist.sql` — 歌单表
- `recommend.sql` — 首页推荐表
- `sys_dict.sql` — 系统字典表
- `user.sql` — 用户/点赞/收藏/播放历史表

### 2. 配置数据库连接

编辑后端配置文件中的数据库连接信息：

- 管理后台：`yueting-music-java/yueting-admin/src/main/resources/application.yml`
- 用户端：`yueting-music-java/yueting-web/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yueting?allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2B8&characterEncoding=utf8
    username: root          # 修改为你的 MySQL 用户名
    password: 123456         # 修改为你的 MySQL 密码
```

### 3. 启动后端

```bash
# 进入后端根目录
cd yueting-music/yueting-music-java

# 编译并启动管理后台（端口 6061）
mvn spring-boot:run -pl yueting-admin

# 编译并启动用户端（端口 6060）
mvn spring-boot:run -pl yueting-web
```

### 4. 启动前端

```bash
# 用户端（默认 http://localhost:5173）
cd yueting-music/yueting-music-front/yueting-music-front-web
npm install
npm run dev

# 管理后台（默认 http://localhost:4000）
cd yueting-music/yueting-music-front/yueting-music-front-admin
npm install
npm run dev
```

### 5. 访问

| 入口 | 地址 | 说明 |
| --- | --- | --- |
| 用户端 | http://localhost:5173 | 音乐浏览、播放、注册登录 |
| 管理后台 | http://localhost:4000 | 内容管理、用户管理 |

### 端口汇总

| 服务 | 端口 |
| --- | --- |
| yueting-web（用户端 API） | 6060 |
| yueting-admin（管理后台 API） | 6061 |
| 用户端前端 | 5173 |
| 管理后台前端 | 4000 |

## 管理后台默认账号

- 用户名：`admin`
- 密码：`123456`

## 上传文件存储

上传的头像、歌曲、封面等文件存储在配置的 `project.folder` 路径下，默认为 `D:/yueting-upload/`，可在 `application.yml` 中修改。
