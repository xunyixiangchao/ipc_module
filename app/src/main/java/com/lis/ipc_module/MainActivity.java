package com.lis.ipc_module;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lis.ipc.IPC;
import com.lis.ipc.IPCService;
import com.lis.ipc_module.location.ILocationManager;

/**
 * c端
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, GpsService.class));
        IPC.connect(this, IPCService.IPCService0.class);
    }

    public void showLocation(View view) {
        ILocationManager locationManager = IPC.getInstanceWithName(IPCService.IPCService0.class, ILocationManager.class, "getDefault");
    }
}
