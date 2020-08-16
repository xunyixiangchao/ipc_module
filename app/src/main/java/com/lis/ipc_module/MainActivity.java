package com.lis.ipc_module;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lis.ipc.IPC;
import com.lis.ipc.IPCService;
import com.lis.ipc_module.location.ILocationManager;
import com.lis.ipc_module.location.Location;

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
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocation(v);
            }
        });
    }

    public void showLocation(View view) {
        ILocationManager locationManager = IPC.getInstanceWithName(IPCService.IPCService0.class, ILocationManager.class, "getDefault");
        Location location = locationManager.getLocation();//会调用IPC中的IPCInvocationHandler中的invoke方法
        Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IPC.disConnect(this, IPCService.IPCService0.class);
    }
}
