package com.example.wwwhisper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ConnectThread extends Thread {
    private BluetoothSocket mmSocket;
    private BluetoothAdapter bluetoothAdapter;
    private OutputStream mmOutStream;

    private String name;
    private String pass;

    public ConnectThread(BluetoothDevice device, UUID uuid, BluetoothAdapter bluetoothAdapter) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        this.bluetoothAdapter = bluetoothAdapter;
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            mmSocket = device.createRfcommSocketToServiceRecord(uuid);
            mmOutStream = mmSocket.getOutputStream();
        } catch (IOException e) {
            System.out.println("ConnectThread IOE: " + e);
        }
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            if(!mmSocket.isConnected()){
                return;
            }
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("Thread", "Could not close the client socket", closeException);
            }

            return;
        }

        String temp = name + " " + pass;

        System.out.println(temp.getBytes());
        try {
            mmOutStream.write(temp.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setString(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public boolean get_State(){
       return mmSocket.isConnected();
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("Thread", "Could not close the client socket", e);
        }
    }

}