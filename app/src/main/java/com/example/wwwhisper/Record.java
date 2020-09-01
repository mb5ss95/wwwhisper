package com.example.wwwhisper;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class Record extends AppCompatActivity {
    StorageReference storageRef;

    MediaRecorder recorder;

    String directory_name;
    String image_name;
    String RECORDED_FILE;

    ImageButton btn1, btn2;
    TextView txt1;
    ImageView image;
    Chronometer ch;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        File file = new File(Environment.getExternalStorageDirectory(), "record.mp3");
        RECORDED_FILE = file.getAbsolutePath();

        System.out.println(Environment.getExternalStorageDirectory()+"555555555555555555555555555555555555555555555");
        ///storage/emulated/0

        init_wiget();
        init_title();
        init_name_path();
    }



    private void init_recorder() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(RECORDED_FILE);
        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception ex) {
            Log.e("Record", "Exception :", ex);
        }
    }

    public void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.reset();
            ch.stop();
            ch.setBase(SystemClock.elapsedRealtime());
            recorder = null;
        }
    }

    public void init_wiget() {
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

        txt1 = findViewById(R.id.txt1);
        ch = findViewById(R.id.ch);

        image = findViewById(R.id.image);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init_recorder();
                ch.start();
            }

        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });


    }

    public Uri save() {
        ContentValues values = new ContentValues(10);
        values.put(MediaStore.MediaColumns.TITLE, "Recorded");
        values.put(MediaStore.Audio.Media.ALBUM, "Audio_Album");
        values.put(MediaStore.Audio.Media.ARTIST, "Ton");
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Audio");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, 1);
        values.put(MediaStore.Audio.Media.IS_MUSIC, 1);
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3"); // 미디어 파일의 포맷
        values.put(MediaStore.Audio.Media.DATA, RECORDED_FILE); // 저장된 녹음 파일

        // ContentValues 객체를 추가할 때, 음성 파일에 대한 내용 제공자 URI 사용
        return getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
    }


    private void init_name_path() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");
        image_name = intent.getStringExtra("image_name");

        storageRef = FirebaseStorage.getInstance().getReference().child(directory_name);

        System.out.println("(Record) Get Directory Name : " + directory_name);
        System.out.println("(Record) Get Storage Reference : " + storageRef);
        //(Record) Get Directory Name : story1
        //(Record) Get Storage Reference : gs://project-83e1e.appspot.com/story1
    }


    public void init_title() {
        setTitle("  WWWhisper Project");
        ActionBar ab = getSupportActionBar();

        ab.setIcon(R.drawable.people);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }
}

