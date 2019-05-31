# AndroidServer
> 代码开启热点暂只支持7.0及以前的设备，其他版本未做测试，需要先手动打开热点，大部分Android手机的默认热点地址是`192.168.43.1`。

### 目前支持的功能
 - 首页默认显示手机最新的20张照片信息
 - 支持在线浏览图片
 - 支持在线观看视频
 - 支持图片接口查询（自定义`pageIndex`和`pageSize`)
 - 支持视频接口查询（自定义`pageIndex`和`pageSize`)
 
### Usage
------
#### 1.Start Server
```
AndroidHttpServer.getInstance().startServer(new OnConnectListener() {
            @Override
            public void onConnecting() {
                Log.d(TAG, "onConnecting");
            }

            @Override
            public void onSuccess(String ip) {
                Log.d(TAG, "onSuccess, ip = " + ip);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Exception, e.cause = " + e.getCause());
            }
});
```
#### 2.Connect to hotspot `PilotHotspot`

#### 3.Stop Server
```
AndroidHttpServer.getInstance().stop();
```


### 接口文档
------
#### 查询接口

|    接口名     | 请求方式 | 示例  |
| ----------   | ------  | ----------  |
|    /image    |   GET   | http://192.168.43.1:8080/image?pageIndex=0&pageSize=20 |
|    /video    |   GET   | http://192.168.43.1:8080/video?pageIndex=0&pageSize=20 |

##### 接口参数
|    参数名     | 必选 | 类型  | 说明  |
| ----------  | ------ | ---------- | ---------- |
|  pageIndex |  是  | int | 当前第几页，从0页开始|
|  pageSize |  是  | int | 每页数量|

##### 响应示例
```
[
    {
        "duration":0,
        "id":166,
        "localPath":"/storage/emulated/0/DCIM/7675119_102145282000_2.jpg",
        "name":"7675119_102145282000_2.jpg",
        "size":132,
        "thumbnailUrl":"http://192.168.43.1:8080/storage/emulated/0/.thumbnails/1558496289340.jpg",
        "url":"http://192.168.43.1:8080/storage/emulated/0/DCIM/7675119_102145282000_2.jpg"
    },
    {
        "duration":0,
        "id":167,
        "localPath":"/storage/emulated/0/DCIM/5252423_205856094066_2.jpg",
        "name":"5252423_205856094066_2.jpg",
        "size":234,
        "thumbnailUrl":"http://192.168.43.1:8080/storage/emulated/0/.thumbnails/1558496289340.jpg",
        "url":"http://192.168.43.1:8080/storage/emulated/0/DCIM/5252423_205856094066_2.jpg"
    }
]
```

#### 删除接口
|    接口名     | 请求方式 | 示例  |
| ----------   | ------  | ----------  |
|    /image/delete   |   DELETE   | http://192.168.43.1:8080/image/delete?id=1&id=2 |
|    /video/delete   |   DELETE   | http://192.168.43.1:8080/video/delete?id=1&id=2 |

##### 接口参数
|    参数名     | 必选 | 类型  | 说明  |
| ----------  | ------ | ---------- | ---------- |
|  id |  是  | int | 图片或视频的id（支持删除多个） |

##### 响应示例
```
{
  "code": 0,
  "data": "success",
  "message": "删除成功"
}
```
