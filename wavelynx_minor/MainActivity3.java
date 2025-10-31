package com.example.wavelynx_minor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity3 extends AppCompatActivity {
TextView tv;
    Toolbar tb;
    BluetoothSocket socket;
    OutputStream outputStream;
    InputStream inputStream;
    BluetoothAdapter bluetoothAdapter;

    EditText messageInput;
    Button sendButton;
    ListView chatList;

    ArrayList<String> chatMessages;
    chatAdapter adapter;

    static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_page);

        tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        messageInput = findViewById(R.id.editTextText);
        sendButton = findViewById(R.id.button);
        chatList = findViewById(R.id.listView);
        tv=findViewById(R.id.textView2);

        chatMessages = new ArrayList<>();
        adapter = new chatAdapter(this, chatMessages);
        chatList.setAdapter(adapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get the selected Bluetooth device
        String deviceAddress = getIntent().getStringExtra("device_address");
        connectToDevice(deviceAddress);
        startReceiving();

        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendMessage(msg);
                chatMessages.add("Me: " + msg);
                adapter.notifyDataSetChanged();
                chatList.smoothScrollToPosition(chatMessages.size() - 1);
                messageInput.setText("");
            }
        });
    }

    void connectToDevice(String address) {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = adapter.getRemoteDevice(address);
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            adapter.cancelDiscovery();

            try {
                socket.connect();
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                Log.i("Bluetooth", "✅ Connected to " + device.getName());
            } catch (IOException e) {
                Log.e("Bluetooth", "❌ Connection failed", e);
                try {
                    socket.close();
                } catch (IOException closeEx) {
                    Log.e("Bluetooth", "Error closing socket", closeEx);
                }
            }
        } catch (Exception e) {
            Log.e("Bluetooth", "Connection setup error", e);
        }
    }

    void sendMessage(String message) {
        try {
            if (outputStream != null) {
                outputStream.write(message.getBytes());
                Log.i("Bluetooth", "📤 Sent: " + message);
            }
        } catch (IOException e) {
            Log.e("Bluetooth", "Send failed", e);
        }
    }

    void startReceiving() {
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    if (inputStream == null) break;
                    bytes = inputStream.read(buffer);
                    String received = new String(buffer, 0, bytes);

                    runOnUiThread(() -> {
                        chatMessages.add("Friend: " + received);
                        adapter.notifyDataSetChanged();
                        chatList.smoothScrollToPosition(chatMessages.size() - 1);
                    });

                } catch (IOException e) {
                    Log.e("Bluetooth", "Receiving stopped", e);
                    break;
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Check which item is clicked
        if (item.getItemId() == R.id.bluetooth) {

            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
                return true;
            }

            // For Android 12 and above, check permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                            != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return true;
            }

            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
                Toast.makeText(this, "🔵 Bluetooth turned ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth is already ON", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
