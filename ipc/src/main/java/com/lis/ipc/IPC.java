package com.lis.ipc;

import android.content.Context;

import com.lis.ipc.model.Request;
import com.lis.ipc.model.Response;

import java.lang.reflect.Proxy;

public class IPC {
    /**
     * 注册接口
     * 服务端需要暴露出去的服务 注册。
     *
     * @param service
     */
    public static void register(Class<?> service) {
        Registry.getInstance().register(service);
    }

    //==========================

    /**
     * 客服端方法
     * 连接
     *
     * @param context
     * @param service
     */
    public static void connect(Context context, Class<? extends IPCService> service) {
        Channel.getInstance().bind(context, null, service);
    }

    public static void disConnect(Context context, Class<? extends IPCService> service) {
        Channel.getInstance().unbind(context.getApplicationContext(), service);
    }

    public static void connect(Context context, String packageName, Class<? extends IPCService> service) {
        Channel.getInstance().bind(context, packageName, service);
    }

    public static <T> T getInstance(Class<? extends IPCService.IPCService0> service,
                                    Class<T> classType, Object... parameters) {
        return getInstanceWithName(service, classType, "getInstance", parameters);
    }

    public static <T> T getInstanceWithName(Class<? extends IPCService.IPCService0> service,
                                            Class<T> classType, String methodName, Object... parameters) {
        if (!classType.isInterface()) {
            //抛异常
            throw new RuntimeException("classType必需为interface");
        }
        Response response = Channel.getInstance().send(Request.GET_INSTANCE, service, classType, methodName, parameters);
        if (response.isSuccess()) {
            return (T) Proxy.newProxyInstance(classType.getClassLoader(), new Class[]{classType},
                    new IPCInvocationHandler(service, classType));
        }
        return null;
    }

}
