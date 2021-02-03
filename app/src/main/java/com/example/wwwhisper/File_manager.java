package com.example.wwwhisper;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class File_manager {

    Context context;

    public File_manager(Context context) {
        this.context = context;
    }

    public Uri save(String file_name, String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        try {
            FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + file_name);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
            writer.write(str);
            writer.close();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(Environment.getExternalStorageDirectory().toString() + file_name);

        return Uri.fromFile(file);
    }

    public String load(String str) {
        String result = "something wrong";
        try {
            FileInputStream input = new FileInputStream(Environment.getExternalStorageDirectory().toString() + str);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            result = reader.readLine();
            reader.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}