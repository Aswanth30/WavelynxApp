package com.example.wavelynx_minor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity2 extends AppCompatActivity {

    Button bluetoothBtn, pairedBtn;
    ListView deviceList;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] pairedDevicesArray;
    ArrayList<String> deviceNames = new ArrayList<>();

    // Common UUID for Bluetooth connection
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_page);

        bluetoothBtn = findViewById(R.id.bluetooth);
        pairedBtn = findViewById(R.id.paireddevice);
        deviceList = findViewById(R.id.listview);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // When "Bluetooth" button is pressed
        bluetoothBtn.setOnClickListener(v -> {
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            } else if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                Toast.makeText(this, "Bluetooth turned ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth is already ON", Toast.LENGTH_SHORT).show();
            }
        });

        // When "Paired Devices" button is pressed
        pairedBtn.setOnClickListener(v -> showPairedDevices());

        // When user clicks on a device
        deviceList.setOnItemClickListener((adapterView, view, position, id) -> {
            BluetoothDevice device = pairedDevicesArray[position];
            connectToDevice(device);
        });
    }

    // Function to list paired devices
    private void showPairedDevices() {
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Please turn ON Bluetooth first", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        deviceNames.clear();

        if (devices.size() > 0) {
            pairedDevicesArray = new BluetoothDevice[devices.size()];
            int index = 0;
            for (BluetoothDevice device : devices) {
                pairedDevicesArray[index++] = device;
                deviceNames.add(device.getName());
            }
        } else {
            deviceNames.add("No paired devices found");
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNames);
        deviceList.setAdapter(adapter);
    }

    // Function to connect with selected device
    private void connectToDevice(BluetoothDevice device) {
        Toast.makeText(this, "Connecting to " + device.getName(), Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();

                // Success → go to chat page
                runOnUiThread(() -> {
                    Toast.makeText(this, "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                    startActivity(intent);
                });

            } catch (IOException e) {
                // Failed to connect
                runOnUiThread(() ->
                        Toast.makeText(this, "Please tap on a connected device name", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
