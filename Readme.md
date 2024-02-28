## Homer

![xxx](https://github.com/iDeMonnnnnn/Homer/blob/master/icon_logo.png?raw=true)

**Android端与网页端在局域网络下进行文件传输的App项目。**

### 前言

>为什么有这个项目？

之前看了[WifiTransfer](https://github.com/MZCretin/WifiTransfer-master) 这个项目的相关文章，就对这个项目很感兴趣。  
自己平时在家喜欢下载一些电影，美剧，动漫传到智能电视（Android系统）上，每次都用U盘传，感觉特别麻烦。于是想起了[WifiTransfer](https://github.com/MZCretin/WifiTransfer-master) 项目。  
但是[WifiTransfer](https://github.com/MZCretin/WifiTransfer-master) 并不符合我的使用场景，比如：限制了只能传输APK，Android TV上不兼容等。  
作为Android开发者便利用空余时间进行了改造，改造后：

1. 对传输的文件类型没有限制。
2. 兼容AndroidQ及以上设备。
3. 兼容适配Android TV设备。
4. 兼容了有线网络（AndroidTV连接网线）的场景。


### 使用

1. Android手机或者TV，下载安装[app-release.apk](https://github.com/iDeMonnnnnn/Homer/raw/master/app/release/app-release.apk)
2. 启动点击右上角wifi图标，查看访问地址IP+端口。
3. 打开手机或PC浏览器,输入访问地址。
4. 在浏览器中选择文件上传，App中即可看到上传的文件。

>Tips:
> 如果浏览器无法访问地址，请注意浏览器设备是否可以ping通Android端的IP。

### 效果

[下载Demo.apk体验](https://github.com/iDeMonnnnnn/Homer/raw/master/app/release/app-release.apk)

![xxx](https://github.com/iDeMonnnnnn/Homer/blob/master/Screenshot.png?raw=true)
![xxx](https://github.com/iDeMonnnnnn/Homer/blob/master/1111.png?raw=true)

### 其他

如果你有问题或者建议，请[Issues](https://github.com/iDeMonnnnnn/Homer/issues).

### 致谢

1. [WifiTransfer](https://github.com/MZCretin/WifiTransfer-master)  
2. [AndroidAsync](https://github.com/koush/AndroidAsync)

### License

```
  Copyright [2022] [Homer]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
