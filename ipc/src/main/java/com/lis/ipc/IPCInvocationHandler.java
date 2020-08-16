package com.lis.ipc;

import com.google.gson.Gson;
import com.lis.ipc.model.Request;
import com.lis.ipc.model.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class IPCInvocationHandler implements InvocationHandler {
    private Class<? extends IPCService> mService;
    private Class<?> classType;
    static Gson mGson = new Gson();

    public IPCInvocationHandler(Class<? extends IPCService> service, Class<?> classType) {
        mService = service;
        this.classType = classType;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //要执行的是在S端
        Response response = Channel.getInstance().send(Request.GET_METHOD, mService, classType, method.getName(), args);
        if (response.isSuccess()) {
            //方法返回值
            Class<?> returnType = method.getReturnType();
            if (returnType != void.class && returnType != Void.class) {

            }
            //返回值，json
            String source = response.getSource();
            return mGson.fromJson(source, returnType);
        } else {

        }
        return null;
    }
}
