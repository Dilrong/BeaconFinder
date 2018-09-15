package com.beaconfinder.dilrong.beaconfinder;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView img_state;
    private TextView tv_state;
    private Button btn_find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_state = findViewById(R.id.img_state);
        tv_state = findViewById(R.id.tv_state);
        btn_find = findViewById(R.id.btn_find);
        btn_find.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                findBeacon();
            }
        });

        checkPermission();
    }

    private void findBeacon(){
        MinewBeaconManager minewBeaconManager = MinewBeaconManager.getInstance(this);
        minewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {
            @Override
            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {
                //3seconds callback
                tv_state.setText("Finding Your Item...");
                img_state.setImageResource(R.drawable.ic_finding);
            }

            @Override
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                //1seconds callback
                tv_state.setText("Disappearing Beacon...");
                img_state.setImageResource(R.drawable.ic_disconnect);
            }

            @Override
            public void onRangeBeacons(List<MinewBeacon> minewBeacons) {
                //every 1seconds callback
                for(MinewBeacon minewBeacon : minewBeacons){
                    int rssi = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getIntValue();
                    if(rssi > 100){
                        tv_state.setText("Wow Find Your Item!");
                        img_state.setImageResource(R.drawable.ic_find);
                    }
                }
            }

            @Override
            public void onUpdateState(BluetoothState bluetoothState) {
                //Bluetooth State
                switch (bluetoothState) {
                    case BluetoothStateNotSupported:
                        tv_state.setText("Bluetooth State Not Supported...");
                        img_state.setImageResource(R.drawable.ic_error);
                        break;
                    case BluetoothStatePowerOff:
                        tv_state.setText("Plz PowerOn Bluetooth!");
                        img_state.setImageResource(R.drawable.ic_disconnect);
                        break;
                    case BluetoothStatePowerOn:
                        tv_state.setText("Wait...finding");
                        img_state.setImageResource(R.drawable.ic_connect);
                        break;
                }
            }
        });

        minewBeaconManager.startScan();
    }

    private boolean checkPermission(){
        BluetoothManager bluetoothManager = (BluetoothManager)
                getApplication().getSystemService(getApplicationContext().BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

        if((mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())){
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            return false;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return false;
            }
        }
        return true;
    }
}
