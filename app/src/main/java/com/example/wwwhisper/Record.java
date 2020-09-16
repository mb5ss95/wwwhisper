package com.example.wwwhisper;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class Record extends AppCompatActivity implements Button.OnTouchListener {
    StorageReference storageRef;

    MediaRecorder recorder;

    String image_name;
    String RECORDED_FILE;

    ImageButton btn1, btn2, btn3;
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

    //upload the file
    private void upload_file(final Uri file_path, String file_name) {
        if (file_path != null) {
            System.out.println("test " + file_path + ", " + file_name);
            //test content://com.android.externalstorage.documents/document/primary%3AUpbeat%20Ukulele%20Background%20Music%20-%20That%20Positive%20Feeling%20by%20Alumo.mp3, [test] 아무오디오.mp3
            final ProgressDialog progressDialog = new ProgressDialog(Record.this);
            progressDialog.setTitle("업로드중...");
            progressDialog.show();
            storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(storageRef + "").child("/" + file_name);

            System.out.println("(upload) Get Storage Reference : " + storageRef);

            storageRef.putFile(file_path)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 실패! 잠시 후 다시 시도하세요!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }

    }


    private void init_name_path() {
        Intent intent = getIntent();

        String directory_name = intent.getStringExtra("directory_name");
        image_name = intent.getStringExtra("image_name");

        storageRef = FirebaseStorage.getInstance().getReference().child(directory_name);

        System.out.println("(Record) Get Directory Name : " + directory_name);
        System.out.println("(Record) Get Storage Reference : " + storageRef);
        //(Record) Get Directory Name : story1
        //(Record) Get Storage Reference : gs://project-83e1e.appspot.com/story1

        init_text("[문병수] test.txt");
        //init_text(temp);
    }


    public void init_title() {
        setTitle("  WWWhisper Project");
        ActionBar ab = getSupportActionBar();

        ab.setIcon(R.drawable.people);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    private void init_text(String temp) {
        //long ONE_MEGABYTE = 1024 * 1024;

        storageRef.child(temp).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                TextView txt1 = findViewById(R.id.txt1);
                txt1.setText(new String(bytes));
                btn1.setClickable(true);

                ProgressBar progressBar = findViewById(R.id.progress);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Record.this, "텍스트를 불러올 수 없습니다. 잠시 후 다시 시도하세요.",
                        Toast.LENGTH_SHORT).show();
                btn1.setClickable(true); //44444444444444444444444444444444444444444444444여기여기 수정
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
                    ch.setBase(SystemClock.elapsedRealtime());
                    ch.setAnimation(show);
                    ch.setVisibility(View.VISIBLE);
                    ch.start();

                    set_wiget_state1();
                    break;

                case R.id.btn2:
                    //stopRecording();
                    ch.setVisibility(View.INVISIBLE);
                    ch.stop();
                    //upload_file(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "[test] test.mp3");
                    set_wiget_state2();
                    break;
            }
        }
        return false;
    }
}

