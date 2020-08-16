package com.lis.app2;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lis.ipc.IPC;
import com.lis.ipc.IPCService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IPC.connect(this,"com.lis.ipc_module", IPCService.IPCService0.class);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ILocationManager locationManager = IPC.getInstanceWithName(IPCService.IPCService0.class,
                        ILocationManager.class, "getDefault");
                Location location = locationManager.getLocation();//会调用IPC中的IPCInvocationHandler中的invoke方法
                Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
