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

在服务进程中会缓存`ServiceId`与对应的服务实现Class对象：**服务表**，同时服务实现中的所有方法列表也需要进行记录：**方法表**。由于一个服务中可能存在多个方法，所以其数据结构为`Map<Class,Map<String,Method>>`,外层`Map`的key为服务Class，内层`Map`的key则为方法标记。

> 重载方法怎么办？
>
> 记录方法表时不能简单的以MethodName作为内层`Map`的key，需要将MethodName+参数列表作为方法标记。



当客户端需要调用服务时，将`ServiceId`、MethodName以及执行方法需要的参数传递给服务端，服务端查表利用反射`Method#invoke`即可执行服务中的方法。

> 如果方法执行返回一个JavaBean数据，将JavaBean序列化为json数据；客户端接收到结果后反序列化为对应的JavaBean即可。



其中客户端的请求被封装为`Request`对象，服务端响应则封装为`Response`对象

![协议](img\协议.png)




### 2.1、服务端接口



#### 2.1.1、注册

服务端只需要暴露服务接口给其他进程使用，所以服务端只需要调用框架的注册接口`regiest`对服务实现进行注册。(***注册的是服务实现，而不是服务接口***)

![服务注册](img\服务注册.png)

```java
//注册服务实现
IPC.regiest(LocationManager.class);
```

注册时，通过反射获得Class上的`ServiceId`即可记录**服务表**。同时利用反射获得Class中所有的public Method即可记录**方法表**。



#### 2.1.2、通信Service

由于框架本质还是利用Binder来完成通信，为了与其他进程通信，框架内部提供了多个预留的Service。

通信Service会返回一个AIDL生成的Binder类对象

```java
//AIDL：
package com.enjoy.ipc;

import com.enjoy.ipc.model.Request;
import com.enjoy.ipc.model.Response;
interface IIPCService {

    Response send(in Request request);
}
```

客户端使用`send`方法向服务端发起请求。



服务端接收到请求后的实现：

```java
/**
  * 执行客户端的请求
  * 通信
  */
//serviceid：LocationManager
String serviceId = request.getServiceId();
//从服务表中获得 对应的Class对象。
//具体类型  Class<LocationManager>
Class<?> instanceClass = Registry.getInstance().getService(serviceId);

//参数以json记录的数据，反序列化为对应类型的对象
Parameters[] parameters = request.getParameters();
Object[] objects = restoreParameters(parameters);

//从方法表中获得 对应的Method对象
String methodName = request.getMethodName();
Method method = Registry.getInstance().getMethod(instanceClass, methodName,parameters);
Response response;
//客户端的请求类型
switch (request.getType()) {
		//单例方法
		case Request.GET_INSTANCE:
		try {
			Object instance = method.invoke(null, objects);
            // 单例类的serviceId与 单例对象 保存
            Registry.getInstance().putObject(serviceId, instance);
            response = new Response(null, true);
            } catch (Exception e) {
				e.printStackTrace();
				response = new Response(null, false);
		}
		break;
		//普通方法
		case Request.GET_METHOD:
		try {
			Object object = Registry.getInstance().getObject(serviceId);
            // getLocation 返回Location
            Object returnObject = method.invoke(object, objects);
            response = new Response(gson.toJson(returnObject), true);
            } catch (Exception e) {
				e.printStackTrace();
                response = new Response(null, false);
            }
            break;
        default:
            response = new Response(null, false);
            break;
        }

return response;
```



### 2.2、客户端接口



#### 2.2.1、绑定

客户端需要先与服务端建立连接，因此框架中提供了`connect`方法，内部封装`bindService`实现与服务端通信Service（`IPCService`）的绑定。

唯一需要注意的是：

```java
Intent intent;
//客户端与服务端在同一App
if (TextUtils.isEmpty(packageName)) {
    intent = new Intent(context, service);
} else {
    //客户端与服务端不在同一App，客户端需要传递 Service所在App的packageName
	intent = new Intent();
	intent.setClassName(packageName, service.getName());
}
context.bindService(intent, ipcServiceConnection, Context.BIND_AUTO_CREATE);
```



#### 2.2.1、请求

当完成绑定后，客户端就可以获得服务端通信Service提供的`IIPCService`对象，客户端调用`IIPCService#send`发起请求。

当我们需要获得`Location`。则应该调用`LocationManager.getDefault().getLocation()`。这句调用会需要执行`LocationManager`的两个方法：`getDefault`与`getLocation`。

> 服务端执行完`getDefault()`之后，框架会根据`ServiceId`保存这个单例对象。
> 当执行`getLocation`时，就可以根据`ServiceId`获得这个单例对象

然而这个对象存在服务端，客户端如何获得？

> 注意：我们在客户端只存在服务接口。

我们可以利用动态代理，在客户端创建一个**"假的"**服务接口对象（代理）。

```java
public static <T> T getInstanceWithName(Class<? extends IPCService> service, Class<T> instanceClass, String methodName, Object... parameters) {
	if (!instanceClass.isInterface()) {
        throw new IllegalArgumentException("必须以接口进行通信。");
    }
    //服务器响应
    Response response = Channel.getInstance().send(Request.GET_INSTANCE, service,
                instanceClass, methodName, parameters);
    // response： 成功
    if (response.isSuccess()) {
        //返回一个假的对象 动态代理
        return getProxy(instanceClass, service);
    }
	return null;
}

private static <T> T getProxy(Class<T> instanceClass, Class<? extends IPCService> service) {
    //动态代理
	ClassLoader classLoader = instanceClass.getClassLoader();
	return (T) Proxy.newProxyInstance(classLoader, new Class[]{instanceClass},
                new IPCInvocationHandler(instanceClass, service));
}
```



当我们执行这个代理对象的方法(`getLocation`)时，会回调`IPCInvocationHandler#invoke`方法，在这个方法中框架会向服务端发起请求：`IIPCService#send`

```java
static class IPCInvocationHandler implements InvocationHandler {

	private final Class<?> instanceClass;
	private final Class<? extends IPCService> service;
	static Gson gson = new Gson();


	public IPCInvocationHandler(Class<?> instanceClass, Class<? extends IPCService> service) {
		this.instanceClass = instanceClass;
		this.service = service;
	}

	@Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		/**
		* 请求服务端执行对应的方法。内部执行：IIPCService#send
		*/
		Response response = Channel.getInstance().send(Request.GET_METHOD, service, instanceClass,method.getName(), args);

		if (response.isSuccess()) {
			Class<?> returnType = method.getReturnType();
            //不是返回void
            if (returnType != Void.class && returnType != void.class) {
            	//获取Location的json字符
                String source = response.getSource();
                //反序列化 回 Location
                return gson.fromJson(source, returnType);
            }
        }
        return null;
	}
}
```

而`getLocation`会返回一个`Location`记录定位信息的对象，这个对象会被服务端json序列化发送过来，因此，客户端只需要在此处获得`Method`的返回类型并反序列化即可。



## 补充：什么是RPC？

RPC指的是：从客户端上通过参数传递的方式调用服务器上的一个函数并得到返回的结果，隐藏底层的通讯细节。在使用形式上像调用本地函数一样去调用远程的函数。












































































