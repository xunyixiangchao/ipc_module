# IPC通信框架

## 1 多进程使用的场景

- 使用系统服务`ACTIVITY_SERVICE`,`InputMethodService`等
- 自己APP中下载，WebView，视频播放，定位等



查看应用进程命令：

`adb shell ps|grep com.tencent.mm`

![mm](.\mm.png)

> com.tencent.mm ：微信主进程，会话和朋友圈相关    
> com.tencent.mm:push ：推送    
> com.tencent.mm:tools: 比如微信中打开一个独立网页是在tools进程中      
> com.tencent.mm:appbrand[x] ：小程序进程



### 为什么使用多进程？

1. 突破虚拟机分配里程的运行内存限制：

   在Android，虚拟机分配给各进程运行内存是有限制值的（根据设备：32M，48M，64M等）随着项目不断增大，app在运行时内存消耗也在不断增加，甚至系统bug导致的内存泄漏，最终结果就是OOM。

2. 提高各个进程的稳定性，单一进程崩溃后不影响整个程序

3. 对内存更可按，通过主动释放进程，减小系统压力，提高系统的流畅性：

   在接收到系统发出的trimMemory(int level)中主动释放重要级低的进程。

## 2 框架的原理

整个框架包含了服务端和客户端两端接口。

![框架原理](.\框架原理.png)

1：51











































































