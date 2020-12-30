package com.example.wwwhisper;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class Connect extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        init_title();
        checkSelfPermission();
        check_Blue();
    }

    public void init_listView(final ArrayList<info_blue> device_list) {

        ArrayList<String> temp_list = new ArrayList<>();

        for (info_blue i : device_list) {
            temp_list.add(i.getName());
        }

        // adapterView
        ListView listView = findViewById(R.id.blue_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                temp_list);
        listView.setAdapter(adapter);
        if (!temp_list.isEmpty()) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //String device_name = device_list.get(position).getName();
                    BluetoothDevice device = device_list.get(position).bluetoothDevice;
                    try_connect(device);

                }
            });
        }
    }

    private void try_connect(final BluetoothDevice device) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("[" + device.getName() + "]에 접속합니다.");
        dialog.setIcon(R.drawable.blue);
        dialog.setView(R.layout.dialog_ssid_pass);
        dialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        dialog.setNegativeButton("연결",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Dialog f = (Dialog) dialog;
                        EditText editText1 = f.findViewById(R.id.add_ssid);
                        EditText editText2 = f.findViewById(R.id.add_pass);

                        String name = editText1.getText().toString();
                        String pass = editText2.getText().toString();

                        if (name.isEmpty() || pass.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "제대로 해라요!!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
                        ConnectThread connectThread = new ConnectThread(device, uuid, bluetoothAdapter);
                        connectThread.setString(name, pass);
                        connectThread.run();
                        if(!connectThread.get_State()){
                            Toast.makeText(getApplicationContext(), "Server Closeed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        dialog.show();
    }


    public void check_Blue() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않습니다!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            init_listView(get_device_list(bluetoothAdapter));
        }
    }


    public ArrayList<info_blue> get_device_list(BluetoothAdapter bluetoothAdapter) {
        ArrayList<info_blue> device_list = new ArrayList<>();
        if (bluetoothAdapter.isEnabled()) {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            if (devices.size() > 0) {
                Iterator<BluetoothDevice> iter = devices.iterator();
                while (iter.hasNext()) {
                    BluetoothDevice d = iter.next();
                    //device_list.add(d.getName());
                    device_list.add(new info_blue(d, d.getName()));
                }
            }
        } else {
            Toast.makeText(this, "블루투스를 켜세요!!", Toast.LENGTH_SHORT).show();
        }
        return device_list;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "권한 허용 : " + permissions[i]);
                }
            }
        }
    }


    public void checkSelfPermission() {
        String temp = "";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.ACCESS_COARSE_LOCATION + " ";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.ACCESS_FINE_LOCATION + " ";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_PHONE_STATE + " ";
        }

        if (!temp.isEmpty()) {
            ActivityCompat.requestPermissions(this, temp.split(" "), 1);
        } else {
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }
    }

    class info_blue {
        private BluetoothDevice bluetoothDevice;
        private String name;

        info_blue(BluetoothDevice bluetoothDevice, String name) {
            this.bluetoothDevice = bluetoothDevice;
            this.name = name;
        }

        public BluetoothDevice getBluetoothDevice() {
            return bluetoothDevice;
        }

        public String getName() {
            return name;
        }
    }

    public void init_title() {
        setTitle("  WWWhisper Project");
        ActionBar ab = getSupportActionBar();

        ab.setIcon(R.drawable.people);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }
}