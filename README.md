# AndroidServer
Create a http server in android device

> 代码开启热点暂只支持5.0，其他版本未做测试，需要先手动打开热点

### 目前支持的功能
 - 首页默认显示手机最新的20张照片信息
 - 支持在线浏览图片
 - 支持在线观看视频
 - 支持图片接口查询（自定义`pageIndex`和`pageSize`)
 - 支持视频接口查询（自定义`pageIndex`和`pageSize`)
 
### 说明
- 大部分Android手机的默认热点地址是`192.168.43.1`

### 接口文档

#### 查询接口

|    接口名     | 请求方式 | 示例  |
| ----------   | ------  | ----------  |
|    /image    |   GET   | http://192.168.43.1:8080/image?pageIndex=0&pageSize=20 |
|    /video    |   GET   | http://192.168.43.1:8080/video?pageIndex=0&pageSize=20 |

###### 接口参数
|    参数名     | 必选 | 类型  | 说明  |
| ----------  | ------ | ---------- | ---------- |
|  pageIndex |  是  | int | 当前第几页，从0页开始|
|  pageSize |  是  | int | 每页数量|

#### 删除接口
|    接口名     | 请求方式 | 示例  |
| ----------   | ------  | ----------  |
|    /image/delete   |   DELETE   | http://192.168.43.1:8080/image/delete?id=1&id=2 |
|    /video/delete   |   DELETE   | http://192.168.43.1:8080/video/delete?id=1&id=2 |

###### 接口参数
|    参数名     | 必选 | 类型  | 说明  |
| ----------  | ------ | ---------- | ---------- |
|  id |  是  | int | 图片或视频的id|
