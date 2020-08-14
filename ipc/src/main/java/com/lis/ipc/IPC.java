package com.lis.ipc;

import android.content.Context;

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

    public static void connect(Context context, String packageName, Class<? extends IPCService> service) {
        Channel.getInstance().bind(context, packageName, service);
    }

    // public static <T> T getInstanceWithName(Class<? extends IPCService> service, Class<T> instanceClass, String methodName, Object...
    //         parameters) {
    //
    //     if (!instanceClass.isInterface()) {
    //         throw new IllegalArgumentException("必须以接口进行通信。");
    //     }
    //     //服务器响应
    //     Response response = Channel.getInstance().send(Request.GET_INSTANCE, service,
    //             instanceClass, methodName, parameters);
    //     // response： 成功
    //     if (response.isSuccess()) {
    //         //返回一个假的对象 动态代理
    //         return getProxy(instanceClass, service);
    //     }
    //     return null;
    // }


}
