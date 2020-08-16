package com.lis.app2;


import com.lis.ipc.annotation.ServiceId;

@ServiceId("LocationManager")
public interface ILocationManager {
    Location getLocation();
}
