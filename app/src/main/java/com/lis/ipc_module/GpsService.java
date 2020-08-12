package com.lis.ipc_module;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.lis.ipc.IPC;
import com.lis.ipc_module.location.Location;
import com.lis.ipc_module.location.LocationManager;

/**
 * 位置提供服务S
 */
public class GpsService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //定位
        // LocationManager.getDefault().setLocation(new Location());
        /**
         * 在数据服务提供方进行注册
         */
        IPC.register(LocationManager.class);
    }
}
