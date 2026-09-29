# 无名音乐服务端

解析 QQ、网易云、咪咕、酷我、酷狗五个平台的音乐：搜索、获取各音质下载链接、歌词、歌曲详情，附带一个搜索下载页面和分享播放页。

技术栈：后端 Kotlin 2.3 + Spring Boot 4.1（Spring MVC），JDK 21，Gradle Kotlin DSL，Redis 可选；前端 Vue 3 + TypeScript + Vite，构建时打进 jar。构建需要 JDK 21 和 Node.js 22+。

| 音质\平台 | KW | WY | MG | QQ | KG |
|---|---|---|---|---|---|
| LQ / PQ / HQ / SQ | √ | √ | √ | √ | √ |
| ZQ（Hi-Res） | √ | √ | √ | √ | √ |
| EQ（杜比/Z3D） | × | × | √ | × | × |
| VIP 资源需 Cookie | √ | √ | × | √ | √ |

## 运行

```bash
./gradlew bootRun
```

默认监听 8080，打开 http://localhost:8080 即可使用。Gradle 会自动执行 `npm ci` 和前端构建。

只改前端时可以开 Vite 开发服务器（热更新，`/api` 转发到 8080 上的后端）：

```bash
cd frontend && npm run dev
```

| 环境变量 | 说明 | 默认值 |
|---|---|---|
| `PORT` | 监听端口 | 8080 |
| `SERVER_ADDRESS` | 监听地址，放在 nginx 后面时建议设为 `127.0.0.1` | 所有网卡 |
| `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD` | Redis 连接 | localhost / 6379 / 空 |
| `WIND_ADMIN_TOKEN` | 配置接口的访问口令，设置后 `/api/configupdate`、`/api/getconfig`、`/api/updatetoken` 需带 `token` 参数 | 空（不校验） |

Redis 用于保存 VIP Cookie、强制音源和访问频率计数。Redis 不可用时服务照常运行，只是这几项功能失效。

### 部署（nginx 负责 HTTPS）

```bash
./gradlew bootJar
SERVER_ADDRESS=127.0.0.1 java -jar build/libs/wind-music-1.0.0.jar
```

```nginx
server {
    listen 80;
    server_name example.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name example.com;
    ssl_certificate     /etc/nginx/cert/example.com.pem;
    ssl_certificate_key /etc/nginx/cert/example.com.key;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host              $host;
        proxy_set_header X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

应用只采信来自本机或内网地址（127.x、10.x、192.168.x、172.16-31.x）的 `X-Forwarded-For`，按 IP 限流依赖这个头拿到真实客户端地址。nginx 如果不在本机或内网，需要配置 `server.tomcat.remoteip.internal-proxies`。

## 代码结构

```
src/main/kotlin/xyz/thewind/music
├── MusicApplication.kt
├── web/            # HTTP 层：MusicController（音乐接口）、SettingsController（配置接口）、AccessLimit 限流、WebConfig
├── service/        # MusicService：按 src 把请求分发给对应平台；组装分享播放页数据
├── provider/       # 平台接入，每个平台一个 MusicProvider Bean
│   ├── MusicProvider.kt   # 平台接口：search / resolveDownloadLink / getLyric / getSongDetail ...
│   └── kg kw mg qq wy/    # XxMusicProvider（入口） + XxApi（请求构造/签名） + 加密/解析 + dto/
├── store/          # KeyValueStore（Redis 实现）与 MusicSettings（各平台 Cookie、强制音源）
├── model/          # SongBean、音源/音质常量、接口响应模型
└── common/         # HttpUtil、GsonUtil、Codec（MD5/Base64）等工具

frontend/src        # 单页应用，/ 是搜索下载页，/player?shareId=KW_228908 是分享播放页
├── api/music.ts    # 后端接口调用和数据类型
├── domain/         # 音质档位、LRC 歌词解析等纯逻辑（有单元测试）
├── player/         # usePlayer：全局唯一的播放器状态，切换页面不断播
├── components/     # SongRow、QualityMeter（音质条）、PlayerDeck（底部播放条）、LyricView 等
├── views/          # SearchView、ShareView
└── styles/base.css # 配色、字体、字号等全局变量
```

新增平台只需实现 `MusicProvider` 并标注 `@Component`，`MusicService` 会自动按 `source` 注册。

## 测试

```bash
./gradlew check                                           # 后端和前端的全部单元测试，不访问网络
./gradlew test -Dlive=true --tests '*LiveProviderTest*'   # 访问真实平台的冒烟测试
```

各平台的签名、加密、请求拼接、响应解析都有单元测试，期望值来自旧版 Java 实现的实际运行结果。

## 接口

所有接口同时支持 GET 查询参数和 POST 表单。`src` 取值为 `KW`、`WY`、`MG`、`QQ`、`KG`，服务端设置了强制音源时搜索、下载、歌词接口忽略 `src`。

| 接口 | 参数 | 说明 |
|---|---|---|
| `/api/next/search` | `src`、`keyword`、`num`（默认 10）、`page`（从 0 开始） | 搜索，返回 SongBean 数组 |
| `/api/next/download` | `src`、`songid`、`quality` | 解析单曲某音质的链接，结果在 `downloadLinkMap` |
| `/api/next/download` | `src`、`batch` | 批量解析，`batch` 为 Base64 编码的 `[{"songId":"..","quality":"SQ"}]` |
| `/api/next/searchdown` | `src`、`keyword`、`num` | 搜索并解析所有音质链接 |
| `/api/getlyric` | `src`、`songid` | LRC 歌词文本 |
| `/api/songdetail` | `src`、`songid` | 歌曲详情 |
| `/api/next/player` | `shareId`（`平台_歌曲ID`，如 `KW_228908`） | 分享播放页数据 |
| `/api/configupdate` | `params` | 写入配置，见下文 |
| `/api/getconfig` | - | 查看当前配置 |
| `/api/updatetoken` | - | 立即刷新 QQ 音乐登录态（另有每 9.6 小时一次的定时刷新） |

音乐接口按 IP 限流（12 小时内 20 次，`searchdown` 5 次，`songdetail` 50 次），超限返回 `{"status":-1,"msg":"访问量过大，请12小时后重试"}`。

下载示例：

```
/api/next/download?src=KW&songid=228908&quality=HQ
```

```json
{
  "songSrc": "KW",
  "songId": "228908",
  "downloadLinkMap": { "HQ": "http://..." }
}
```

### 配置 VIP Cookie 与强制音源

把 `key=value` 用 `;` 拼接后整体 Base64 编码，作为 `params` 传给 `/api/configupdate`：

```
kg_uid=123;kg_token=abc;wy_music_u=xxx;kw_uid=1;kw_sid=2;songSrc=WY
```

| key | 含义 |
|---|---|
| `wy_music_u` | 网易云 Cookie `MUSIC_U` |
| `kg_uid`、`kg_token` | 酷狗 Cookie `userid`、`token` |
| `kw_uid`、`kw_sid` | 酷我登录 uid / sid |
| `qqmusic_wx_login_info` | QQ 音乐登录信息 JSON（musicid、musickey、refresh_token 等），之后由服务端自动续期 |
| `songSrc` | 强制音源，设置后所有请求都走该平台 |

注意：值里不能包含 `=` 或 `;`。
