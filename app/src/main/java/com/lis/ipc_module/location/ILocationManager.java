package com.lis.ipc_module.location;

import com.lis.ipc.annotation.ServiceId;

@ServiceId("LocationManager")
public interface ILocationManager {
    Location getLocation();
}
