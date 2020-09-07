package com.example.wwwhisper;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Record extends AppCompatActivity implements Button.OnTouchListener {
    StorageReference storageRef;

    MediaRecorder recorder;

    String directory_name;
    String image_name;
    String RECORDED_FILE;

    ImageButton btn1, btn2, btn3;
    TextView txt1;
    ImageView image;
    Chronometer ch;

    Animation start_rotate, stop_rotate, show;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        File file = new File(Environment.getExternalStorageDirectory(), "record.mp3");
        RECORDED_FILE = file.getAbsolutePath();

        System.out.println(Environment.getExternalStorageDirectory() + "555555555555555555555555555555555555555555555");
        ///storage/emulated/0

        init_anim();
        init_wiget();
        init_title();
        init_name_path();
    }

    private void init_text(String temp) {
        //  gs://test-a526e.appspot.com/directory_name/file_name
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(temp);
        //  gs://test-a526e.appspot.com/%EB%B9%A8%EA%B0%95%EB%A8%B8%EB%A6%AC%20%EC%95%A4/file_name
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("TAG", e.getMessage());
                Toast.makeText(Record.this, "오디오를 불러올 수 없습니다. 잠시 후 다시 시도하세요.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init_anim() {

        start_rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        stop_rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.stop);
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
            recorder = null;
        }
    }

    public void init_wiget() {
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        txt1 = findViewById(R.id.txt1);
        ch = findViewById(R.id.ch);

        image = findViewById(R.id.image);

        btn1.setOnTouchListener(Record.this);
        btn2.setOnTouchListener(Record.this);
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

        intit_text("test123.txt");
        //init_text(temp);
    }


    public void init_title() {
        setTitle("  WWWhisper Project");
        ActionBar ab = getSupportActionBar();

        ab.setIcon(R.drawable.people);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    private void intit_text(String temp) {
        //long ONE_MEGABYTE = 1024 * 1024;

        storageRef.child(temp).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                txt1.setText(new String(bytes));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Record.this, "텍스트를 불러올 수 없습니다. 잠시 후 다시 시도하세요.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void set_wiget_state1() {
        btn1.setVisibility(View.INVISIBLE);
        btn2.setVisibility(View.VISIBLE);

        btn3.setImageResource(R.drawable.ring);
        btn3.startAnimation(start_rotate);
    }

    private void set_wiget_state2() {
        btn2.setVisibility(View.INVISIBLE);
        btn1.setVisibility(View.VISIBLE);

        btn3.clearAnimation();
        btn3.setImageResource(R.drawable.eyes);
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.btn1:
                    //init_recorder();
                    ch.setAnimation(show);
                    ch.setVisibility(View.VISIBLE);
                    ch.start();

                    set_wiget_state1();

                    break;

                case R.id.btn2:
                    //stopRecording();
                    ch.setVisibility(View.INVISIBLE);
                    ch.stop();
                    ch.setBase(SystemClock.elapsedRealtime());

                    set_wiget_state2();

                    break;
            }
        }
        return false;
    }
}

