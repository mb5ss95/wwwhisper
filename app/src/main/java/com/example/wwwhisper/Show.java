package com.example.wwwhisper;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;


public class Show extends Activity implements Button.OnClickListener {


    String directory_name;

    int progress = 0;
    int cnt;

    StorageReference storageRef;

    ImageButton btn_start, btn_pause, btn_forward;
    ImageView img;
    SeekBar seek;

    MediaPlayer audio_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show);

        cnt = 0;

        init_wiget();
        init_name_path();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init_wiget() {

        img = findViewById(R.id.img);
        seek = findViewById(R.id.seekBar);

        btn_start = findViewById(R.id.btn_play);
        btn_pause = findViewById(R.id.btn_pause);
        btn_forward = findViewById(R.id.btn_forward);

        btn_start.setOnClickListener(Show.this);
        btn_pause.setOnClickListener(Show.this);
        btn_forward.setOnClickListener(Show.this);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresss, boolean fromUser) {
                if (fromUser && audio_player.isPlaying()) {
                    progress = progresss;

                    audio_player.pause();
                    audio_player.seekTo(progresss);
                    seek.setProgress(progress);
                    audio_player.start();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btn_forward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (audio_player.isPlaying()) {
                            audio_player.pause();
                            progress = audio_player.getCurrentPosition();

                            btn_forward.animate().rotation(45).setInterpolator(new AccelerateDecelerateInterpolator());
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (progress != 0) {
                            audio_player.seekTo(progress + 10000);
                            seek.setProgress(audio_player.getCurrentPosition());
                            start_audio();

                            set_wiget_state1();
                            btn_forward.animate().rotation(0).setInterpolator(new AccelerateDecelerateInterpolator());
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (audio_player != null) {
            audio_player.pause();
            SystemClock.sleep(300);
            audio_player.release();
        }

        Show.this.finish();
        //super.onBackPressed();
    }


    private void init_name_path() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");

        final String image_name = intent.getStringExtra("image_name");
        final String audio_name = intent.getStringExtra("audio_name");
        String audio_path;

        storageRef = FirebaseStorage.getInstance().getReference().child(directory_name);

        audio_path = storageRef + "/" + audio_name;

        System.out.println("(Show) Get Directory Name : " + directory_name);
        System.out.println("(Show) Get Image Name & Audio Name : " + image_name + ", " + audio_name);
        System.out.println("(Show) Get Storage Reference : " + storageRef);
        //(Show) Get Directory Name : story1
        //(Show) Get Image Name & Audio Name : [관리자] nako4.jpg, [문병수] song4.mp3
        //(Show) Get Storage Reference : gs://project-83e1e.appspot.com/story1

        String[] temp = audio_name.split(" ");

        for(int i=2; i<temp.length; i++){
            temp[1] = temp[1] + " " + temp[i];
        }

        TextView txt = findViewById(R.id.txt);
        txt.setText(temp[1]);

        init_image(directory_name + "/" + image_name);
        init_audio(audio_path);

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cnt++;
                if (cnt == 10) {
                    Toast.makeText(Show.this, image_name + ", " + audio_name,
                            Toast.LENGTH_SHORT).show();
                    cnt = 0;
                }
            }
        });
    }


    private void init_audio(String temp) {
        //  gs://test-a526e.appspot.com/directory_name/file_name

        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(temp);
        //  gs://test-a526e.appspot.com/%EB%B9%A8%EA%B0%95%EB%A8%B8%EB%A6%AC%20%EC%95%A4/file_name

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    audio_player = new MediaPlayer();
                    audio_player.reset();
                    audio_player.setDataSource(uri.toString());
                    audio_player.prepare();
                    audio_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            progress = 0;
                            seek.setProgress(0);
                            set_wiget_state2();
                        }
                    });
                    btn_start.setClickable(true);
                    btn_forward.setClickable(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("TAG", e.getMessage());
                Toast.makeText(Show.this, "오디오를 불러올 수 없습니다. 잠시 후 다시 시도하세요.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init_image(String temp) {
        //  directory_name/file_name

        storageRef = FirebaseStorage.getInstance().getReference().child(temp);
        //  gs://test-a526e.appspot.com/%EB%B9%A8%EA%B0%95%EB%A8%B8%EB%A6%AC%20%EC%95%A4/file_name

        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(Show.this)
                            .load(task.getResult())
                            .into(img);
                    img.setVisibility(View.VISIBLE);
                } else {
                    //Toast.makeText(mp3.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(Show.this, "이미지를 불러올 수 없습니다. 잠시 후 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void start_audio() {
        audio_player.start();

        new Thread() {
            public void run() {
                if (audio_player == null) {
                    return;
                }
                seek.setMax(audio_player.getDuration());
                while (audio_player.isPlaying()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seek.setProgress(audio_player.getCurrentPosition());
                            progress = audio_player.getCurrentPosition();
                        }
                    });
                    SystemClock.sleep(200);
                }
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                set_wiget_state1();
                progress = seek.getProgress();
                audio_player.seekTo(progress);
                start_audio();
                break;
            case R.id.btn_pause:
                set_wiget_state2();
                seek.setProgress(progress);
                audio_player.pause();
                break;
        }
    }

    private void set_wiget_state1() {
        btn_start.setVisibility(View.INVISIBLE);
        btn_pause.setVisibility(View.VISIBLE);
    }

    private void set_wiget_state2() {
        btn_start.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.INVISIBLE);
    }
}

