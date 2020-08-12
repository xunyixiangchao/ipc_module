package com.lis.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.lis.ipc.model.Parameters;
import com.lis.ipc.model.Request;
import com.lis.ipc.model.Response;

import java.lang.reflect.Method;

public abstract class IPCService extends Service {
    static Gson gson = new Gson();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IIPCService.Stub() {
            @Override
            public Response send(Request request) throws RemoteException {
                String methodName = request.getMethodName();
                Parameters[] parameters = request.getParameters();
                String serviceId = request.getServiceId();
                Object[] objects = restoreParameters(parameters);
                Method method = Registry.getInstance().findMethod(serviceId, methodName, objects);

                switch (request.getType()) {
                    //执行静态方法，单例
                    case Request.GET_INSTANCE:
                        try {
                            Object invoke = method.invoke(null, objects);
                            return new Response(null, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new Response(null, true);
                        }
                        //执行普通方法
                    case Request.GET_METHOD:
                        break;

                }
                return null;
            }
        };
    }

    public Object[] restoreParameters(Parameters[] parameters) {
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameters parameter = parameters[i];
            try {
                objects[i] = gson.fromJson(parameter.getValue(), Class.forName(parameter.getType()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return objects;
    }

    public static class IPCService0 extends IPCService {
    }

    ;

    public static class IPCService1 extends IPCService {
    }

    ;

    public static class IPCService2 extends IPCService {
    }

    ;

    public static class IPCService3 extends IPCService {
    }

    ;


}
