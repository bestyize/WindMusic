#TheWind服务端



# 1 无名音乐服务端

## 1.1 功能介绍

无名音乐服务端可以解析多个音乐平台的音乐源，平台，格式如下

|音质\平台|xm|kw|wy|mg|qq|kg|
|---|---|---|---|---|---|---|
|LQ|√|√|√|√|√|√|
|PQ|√|√|√|√|√|√|
|HQ|√|√|√|√|√|√|
|SQ|√|√|√|√|√|√|
|VIP资源|√|√|√|√|√|√|
|VIP需Cookie|×|×|√|×|√|√|

## 1.2 使用方式

### 1.2.1 配置工作模式

#### 1.2.1.1 Cookie配置

不需要经过Cookie配置也可获取普通音质的下载链接，所有接口都支持下载非VIP的各种音质音乐。

下表是不设置Cookie时，对VIP资源（可试听）的支持能力，对于不支持试听的资源，或者需要付费的专辑，所有下载接口都不支持

|音质\平台|xm|kw|wy|mg|qq|kg|
|---|---|---|---|---|---|---|
|LQ|√|√|√|√|√|√|
|PQ|√|√|√|√|√|√|
|HQ|√|√|×|√|×|×|
|SQ|√|√|×|√|×|×|

WY、KG、QQ三家的VIP音乐资源需要开通了VIP的帐号的cookie作为Cookie, 在无名音乐服务端，使用Redis存储这些Cookie，因此需要安装Redis。

三家需要的Cookie可以通过登录VIP后抓包获取，分别是

|项目\平台|WY|QQ|KG|
|---|---|---|---|
|需要的Cookie|MUSIC_U|qqmusic_key、qqmusic_uin|userid、token|
|对应的代号|wy_music_u|qqmusic_key、qqmusic_uin|kg_uid、kg_token|

获取cookie后，可以单独提交或者批量提交，提交之前的数据应该是这样的

```bash
qqmusic_uin=12345678;qqmusic_key=12345678;kg_uid=12345678;kg_token=12345678;kg_pid_secret=12345678;wy_music_u=12345678
```
为了提交不出问题，需要对上面给的字串BASE64编码

```html
cXFtdXNpY191aW49MTIzNDU2Nzg7cXFtdXNpY19rZXk9MTIzNDU2Nzg7a2dfdWlkPTEyMzQ1Njc4O2tnX3Rva2VuPTEyMzQ1Njc4O2tnX3BpZF9zZWNyZXQ9MTIzNDU2Nzg7d3lfbXVzaWNfdT0xMjM0NTY3OA==
```

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|https://www.thewind.xyz/api/configupdate|请求地址|√|
|params|BASE64编码后的cookie|请求参数|√|

一个完整的请求示例

```html
https://www.thewind.xyz/api/configupdate?params=cXFtdXNpY191aW49MTIzNDU2Nzg7cXFtdXNpY19rZXk9MTIzNDU2Nzg7a2dfdWlkPTEyMzQ1Njc4O2tnX3Rva2VuPTEyMzQ1Njc4O2tnX3BpZF9zZWNyZXQ9MTIzNDU2Nzg7d3lfbXVzaWNfdT0xMjM0NTY3OA==
```

#### 1.2.1.2 获取现有配置

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/getconfig|请求地址|√|

#### 1.2.1.3 音乐源配置

如果配置了服务端音乐源，那么普通请求的音乐源将不会生效，而是采用服务端的音乐源。与上述配置采用一个接口

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/configupdate|请求地址|√|
|params|配置的音乐源|请求参数|√|

实际上，上面可选的一共有六种,分别是

|可选参数|xm|kw|wy|mg|qq|kg|
|---|---|---|---|---|---|---|
|源码|songSrc=XM|songSrc=KW|songSrc=WY|songSrc=MG|songSrc=QQ|songSrc=KG|
|base64编码后|c29uZ1NyYz1YTQ==|c29uZ1NyYz1LVw==|c29uZ1NyYz1XWQ==|c29uZ1NyYz1NRw==|c29uZ1NyYz1RUQ==|c29uZ1NyYz1LRw==|

一个完整的请求示例

```html
<!--请求将音乐源改成WY-->
https://www.thewind.xyz/api/configupdate?params=c29uZ1NyYz1XWQ==
```

### 1.2.2 搜索API介绍

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/search|请求地址|√|
|src|XM、KW、WY、MG、QQ、KG|请求平台|√|
|num|整数即可，默认20个|最大请求数量|√|
|keyword|关键词|搜索的关键词|√|

一个完整请求举例

```html
https://www.thewind.xyz/api/search?src=MG&num=3&keyword=%E8%AF%B4%E5%A5%BD%E4%B8%8D%E5%93%AD
```

返回一段json

```json
[
    {
        "songSrc": "MG",
        "songId": "60069101277",
        "songName": "说好不哭(电视剧《两生花》插曲)",
        "singersName": [
            "多亮"
        ],
        "albumName": "多原话",
        "picUrl": "https://mcontent.migu.cn/newlv2/new/album/20190520/1000018430/s_KseIpqJgeWAiF4Sk.jpg",
        "lrcUrl": "http://218.200.230.40:18089/files/lyric/2020-06-15/79f843c61e1e45d9887127da7251b7f7.lrc",
        "mvUrl": "",
        "quality": "SQ"
    },
    {
        "songSrc": "MG",
        "songId": "69906701361",
        "songName": "说好不哭",
        "singersName": [
            "廖羽涵"
        ],
        "albumName": "爱的旅程",
        "picUrl": "https://mcontent.migu.cn/newlv2/new/album/20191031/1106922315/s_whUYKDqGusyWtvr2.jpg",
        "lrcUrl": "http://218.200.230.40:18089/files/lyric/2016-09-12/1609121509117509.lrc",
        "mvUrl": "",
        "quality": "SQ"
    },
    {
        "songSrc": "MG",
        "songId": "60070861278",
        "songName": "说好不哭",
        "singersName": [
            "尼夫特"
        ],
        "albumName": "",
        "lrcUrl": "http://218.200.230.40:18089/files/lyric/2016-09-26/1609260912331774.lrc",
        "mvUrl": ""
    }
]
```

### 1.2.3 下载API介绍

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/download|请求地址|√|
|src|XM、KW、WY、MG、QQ、KG|请求平台|√|
|songid|音乐ID|音乐ID|√|
|quality|SQ、HQ、PQ、LQ|音乐质量|√|
|batch|一段json代码|json| |

batch格式如下,注意没有空格,作为参数前还需要经过BASE64编码(base64编码后不容易出错)

```json
[
    {
        "songSrc": "MG",
        "songId": "60069101277",
        "quality": "SQ"
    },
    {
        "songSrc": "MG",
        "songId": "69906701361",
        "quality": "SQ"
    },
    {
        "songSrc": "MG",
        "songId": "60069101277",
        "quality": "HQ"
    }
]
```

假如需要下载某单个歌曲，那么请求格式如下

```html
https://www.thewind.xyz/api/download?songid=60069101277&quality=HQ&src=MG
```

返回的数据是这样的

```json
{
    "songSrc": "MG",
    "songId": "60069101277",
    "downloadLinkMap": {
        "HQ": "http://freetyst.nf.migu.cn/public/ringmaker01/n4/xlwh/2013/01/2012年12月27日当然文化内容准入6首/全曲下载/MP3_320_16_Stero/说好不哭-多亮.mp3"
    }
}
```

假如需要批量下载，那么请求格式如下

```json
[
    {
        "songSrc": "QQ",
        "songId": "001OyHbk2MSIi4",
        "quality": "SQ"
    },
    {
        "songSrc": "QQ",
        "songId": "001OyHbk2MSIi4",
        "quality": "HQ"
    },
    {
        "songSrc": "QQ",
        "songId": "001OyHbk2MSIi4",
        "quality": "PQ"
    },
    {
        "songSrc": "QQ",
        "songId": "001OyHbk2MSIi4",
        "quality": "LQ"
    },
    {
        "songSrc": "QQ",
        "songId": "003e4b5o0jOMXV",
        "quality": "SQ"
    },
    {
        "songSrc": "QQ",
        "songId": "000PkZXC11iZUC",
        "quality": "HQ"
    }
]
```

```html
http://thewind.xyz/api/download?src=QQ&batch=W3sic29uZ1NyYyI6IlFRIiwic29uZ0lkIjoiMDAxT3lIYmsyTVNJaTQiLCJxdWFsaXR5IjoiU1EifSx7InNvbmdTcmMiOiJRUSIsInNvbmdJZCI6IjAwMU95SGJrMk1TSWk0IiwicXVhbGl0eSI6IkhRIn0seyJzb25nU3JjIjoiUVEiLCJzb25nSWQiOiIwMDFPeUhiazJNU0lpNCIsInF1YWxpdHkiOiJQUSJ9LHsic29uZ1NyYyI6IlFRIiwic29uZ0lkIjoiMDAxT3lIYmsyTVNJaTQiLCJxdWFsaXR5IjoiTFEifSx7InNvbmdTcmMiOiJRUSIsInNvbmdJZCI6IjAwM2U0YjVvMGpPTVhWIiwicXVhbGl0eSI6IlNRIn0seyJzb25nU3JjIjoiUVEiLCJzb25nSWQiOiIwMDBQa1pYQzExaVpVQyIsInF1YWxpdHkiOiJIUSJ9XQ==
```

返回的数据是这样的

```json
[
    {
        "songSrc": "QQ",
        "songId": "001OyHbk2MSIi4",
        "quality": "SQ",
        "downloadLinkMap": {
            "SQ": "http://ws.stream.qqmusic.qq.com/F000001OyHbk2MSIi4.flac?guid=0&vkey=0D3D3999927DBB78281F7377835E2C7A76AAC0B177EA89DFC59F0001D4C212E1C62CEE2FDC5ADD7E478A4D9DE82F241EDE3D2E274DAF16C5&uin=1496&fromtag=999"
        }
    },
    {
        "songSrc": "QQ",
        "songId": "001OyHbk2MSIi4",
        "quality": "HQ",
        "downloadLinkMap": {
            "HQ": "http://ws.stream.qqmusic.qq.com/M800001OyHbk2MSIi4.mp3?guid=0&vkey=355F09128E3B6533433EFDB67D9A6EBFD3278FED8E97B778BC341F110AF3800FAF2463C8E8A298694C68E838E3BD1ED6A841E827DB0A9B18&uin=1496&fromtag=999"
        }
    },
    {
        "songSrc": "QQ",
        "songId": "001OyHbk2MSIi4",
        "quality": "PQ",
        "downloadLinkMap": {
            "PQ": "http://ws.stream.qqmusic.qq.com/M500001OyHbk2MSIi4.mp3?guid=0&vkey=F5FA87B695FF2B17FA0C96D2D21EF3B813E9D2DAF4866631E568B1475408FA45C24EEC94561AF66F01C40CC2F73D677D1109BA94FCB936FC&uin=1496&fromtag=999"
        }
    },
    {
        "songSrc": "QQ",
        "songId": "001OyHbk2MSIi4",
        "quality": "LQ",
        "downloadLinkMap": {
            "LQ": "http://ws.stream.qqmusic.qq.com/C400001OyHbk2MSIi4.m4a?guid=0&vkey=9CCC87A2A8115CC5E2FF700A8E5C5C8F52FE3C239CB150FD0FC541A3388D77EE17E8DDDC58D714F2EF57BD6A871236AE6BC0397470AA01B5&uin=1496&fromtag=999"
        }
    },
    {
        "songSrc": "QQ",
        "songId": "003e4b5o0jOMXV",
        "quality": "SQ",
        "downloadLinkMap": {
            "SQ": "http://ws.stream.qqmusic.qq.com/F000003e4b5o0jOMXV.flac?guid=0&vkey=5ABA827A7A239E8EAE6F27E045AEE28C9F9C7B03ED9C1986400BD6D510F13C29C20129009FA366C0ACA7EDCD0906CA924042014D984C9D4B&uin=1496&fromtag=999"
        }
    },
    {
        "songSrc": "QQ",
        "songId": "000PkZXC11iZUC",
        "quality": "HQ",
        "downloadLinkMap": {
            "HQ": "http://ws.stream.qqmusic.qq.com/"
        }
    }
]
```

### 1.2.4 歌词搜索能力

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|https://www.thewind.xyz/api/getlyric|请求地址|√|
|src|XM、KW、WY、MG、QQ、KG|请求平台|√|
|songid|音乐ID|音乐ID|√|

### 1.2.5 播放支持

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/player|请求地址|√|
|shareId|分享ID|格式为平台 "ID_歌曲ID" |√|

shareId由平台和songid组成。例如：WY_65766

### 1.2.6 歌曲详情
|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/songdetail|请求地址|√|
|src|KW、KG、QQ、WY、MG、XM|平台ID |√|
|songid|歌曲ID|歌曲ID |√|

### 1.2.7 更新QQ音乐Cookie

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/tokenrefresh|请求地址|√|
|force|不为空即可|是否强制更新Cookie（不强制的话50000s才能进行下一次更新） |×|

# 2 用户管理模块

## 2.1 功能介绍

为了实现网站的访问控制，需要实现用户模块

## 2.2 使用方式

### 2.2.1 用户注册

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/reg|请求地址|√|
|userId|长度>=6、以字母开头的，且所有字符为字母和数字组合(大小写不敏感)|用户ID|√|
|password|长度>=6，且所有字符为字母和数字组合(大小写敏感)|密码|√|
|email|符合邮箱格式|邮箱|√|
|userName|字符，汉字均可|用户昵称|√|

返回一段Cookie，表明用户身份

### 2.2.2 用户登录

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/login|请求地址|√|
|userId|长度>=6、以字母开头的，且所有字符为字母和数字组合(大小写不敏感)|用户ID|√|
|password|长度>=6，且所有字符为字母和数字组合(大小写敏感)|密码|√|

返回一段Cookie，表明用户身份

### 2.2.3 发送验证码

发送验证码。本项目用的网易邮箱，可以根据实际自行配置

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/mail|请求地址|√|
|userId|长度>=6、以字母开头的，且所有字符为字母和数字组合(大小写不敏感)|用户ID|√|
|email|符合邮箱格式|邮件地址|√|

### 2.2.3 修改用户状态

发送验证码。本项目用的网易邮箱，可以根据实际自行配置

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/verifycode|请求地址|√|
|userId|长度>=6、以字母开头的，且所有字符为字母和数字组合(大小写不敏感)|用户ID|√|
|email|符合邮箱格式|邮件地址|√|
|code|收到的验证码|验证码|√|

### 2.2.4 查询所有用户

后台管理需要。使用时附带Cookie,用户的等级要>=7

|名称|内容|备注|是否必须|
|---|---|---|---|
|api|/api/queryalluser|请求地址|√|


