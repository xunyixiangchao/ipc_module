package com.lis.ipc_module.location;

import com.lis.ipc.annotation.ServiceId;

@ServiceId("LocationManager")
public class LocationManager {
    private static final LocationManager instance = new LocationManager();

    public static LocationManager getDefault() {
        return instance;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    private Location mLocation;


}
