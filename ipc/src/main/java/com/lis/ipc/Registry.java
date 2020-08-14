package com.lis.ipc;

import com.lis.ipc.annotation.ServiceId;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责记录 服务端注册的信息
 */
public class Registry {
    private volatile static Registry mRegistry;
    //服务表
    private ConcurrentHashMap<String, Class<?>> mServices = new ConcurrentHashMap<>();
    //方法表
    private ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> mMethods =
            new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> objects =
            new ConcurrentHashMap<>();

    public static Registry getInstance() {
        if (mRegistry == null) {
            synchronized (Registry.class) {
                if (mRegistry == null) {
                    mRegistry = new Registry();
                }
            }
        }
        return mRegistry;
    }

    public void register(Class<?> service) {
        //1.服务id与class的表
        ServiceId serviceId = service.getAnnotation(ServiceId.class);
        if (serviceId == null) {
            throw new RuntimeException("必须使用ServiceId注解的服务进行注册！");
        }
        String value = serviceId.value();
        mServices.put(value, service);
        //class与方法表
        ConcurrentHashMap<String, Method> method = mMethods.get(service);
        if (method == null) {
            method = new ConcurrentHashMap<>();
            mMethods.put(service, method);
        }
        for (Method methodItem : service.getMethods()) {
            //因为有重载方法的存在 ，所以不能以方法名作为key,要带上参数为key！
            StringBuilder builder = new StringBuilder(methodItem.getName());
            builder.append("(");
            //方法的参数类型
            Class<?>[] parameterTypes = methodItem.getParameterTypes();
            if (parameterTypes.length != 0) {
                builder.append(parameterTypes[0].getName());
            }
            for (int i = 1; i < parameterTypes.length; i++) {
                builder.append(",").append(parameterTypes[i].getName());
            }
            builder.append(")");
            method.put(builder.toString(), methodItem);
        }

        // TODO: 2020/8/12 打印 如果debug模式就可以打印

        Set<Map.Entry<String, Class<?>>> entries = mServices.entrySet();
        for (Map.Entry<String, Class<?>> entry : entries) {
            System.out.println("服务表：" + entry.getKey() + " = " + entry.getValue());
        }
        Set<Map.Entry<Class<?>, ConcurrentHashMap<String, Method>>> methodEntries = mMethods.entrySet();
        for (Map.Entry<Class<?>, ConcurrentHashMap<String, Method>> methodEntry : methodEntries) {
            System.out.println("方法表：" + methodEntry.getKey());
            ConcurrentHashMap<String, Method> value1 = methodEntry.getValue();
            for (Map.Entry<String, Method> stringMethodEntry : value1.entrySet()) {
                System.out.println(" value：" + stringMethodEntry.getKey());
            }
        }

    }


    /**
     * 查找对应的method
     *
     * @param serviceId
     * @param methodName
     * @param objects
     * @return
     */
    public Method findMethod(String serviceId, String methodName, Object[] objects) {
        Class<?> service = mServices.get(serviceId);
        ConcurrentHashMap<String, Method> methods = mMethods.get(service);

        StringBuilder builder = new StringBuilder(methodName);
        builder.append("(");
        if (objects.length != 0) {
            builder.append(objects[0].getClass().getName());
        }
        for (int i = 1; i < objects.length; i++) {
            builder.append(",").append(objects[i].getClass().getName());
        }
        builder.append(")");
        return methods.get(builder.toString());
    }

    public void putInstanceObject(String type, Object object) {
        objects.put(type, object);
    }

    public Object getInstanceObject(String type) {
        return objects.get(type);
    }

}
