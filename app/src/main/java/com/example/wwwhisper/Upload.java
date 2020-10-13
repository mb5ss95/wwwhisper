package com.example.wwwhisper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;


public class Upload extends AppCompatActivity implements Button.OnClickListener {

    String audio_name;
    String chapter;
    String directory_name;

    Uri audio_path;

    ImageButton btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        init_title();
        init_wiget();
        init_data();
    }

    public void init_wiget() {
        Button btn1 = findViewById(R.id.choose);
        btn2 = findViewById(R.id.upload);

        btn1.setOnClickListener(Upload.this);
        btn2.setOnClickListener(Upload.this);
    }

    public void init_data() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");
        chapter = intent.getStringExtra("chapter");


        System.out.println("(upload) Get Directory Name : " + directory_name);
        System.out.println("(upload) Get Chapter : " + chapter);
        //(upload) Get Directory Name : Test World2
        //(upload) Get Id Name : chapter3
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");

                startActivityForResult(Intent.createChooser(intent, "오디오 파일을 선택하세요."), 0);
                break;

            case R.id.upload:
                if (audio_path != null) {
                    upload_file(audio_path, audio_name);
                    btn2.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    private void get_name() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Upload.this);

        dialog.setTitle("닉네임을 입력하세요.");
        dialog.setView(R.layout.dialog_edit);
        dialog.setIcon(R.drawable.lib2);
        dialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        dialog.setNegativeButton("입력",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Dialog f = (Dialog) dialog;

                        EditText editText = f.findViewById(R.id.add_id);

                        String ID_name = editText.getText().toString();

                        if (ID_name.isEmpty()) ID_name = "아무개";


                        TextView txt = findViewById(R.id.upload_txt1);
                        txt.setText(ID_name + "/" + chapter);

                        directory_name = directory_name + "/" + ID_name;
                        audio_name = chapter + ".mp3";

                        System.out.println("(upload) Get Audio Name & Audio Path : " + audio_name + ", " + audio_path);
                        //(upload) Get Audio Name & Audio Path : null, content://com.android.externalstorage.documents/document/primary%3Arecord.mp3
                        btn2.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "업로드 하세요!!", Toast.LENGTH_SHORT).show();
                    }
                });
        dialog.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            get_name();

            audio_path = data.getData();
        } else {
            Toast.makeText(getApplicationContext(), "다시 시도 하세요.", Toast.LENGTH_SHORT).show();
            btn2.setVisibility(View.INVISIBLE);
        }
    }

    //upload the file
    private void upload_file(final Uri file_path, String file_name) {
        if (file_path != null) {
            System.out.println("test " + file_path + ", " + file_name);
            //test content://com.android.externalstorage.documents/document/primary%3AUpbeat%20Ukulele%20Background%20Music%20-%20That%20Positive%20Feeling%20by%20Alumo.mp3, [test] 아무오디오.mp3
            final ProgressDialog progressDialog = new ProgressDialog(Upload.this);
            progressDialog.setTitle("업로드 중 입니다아");
            progressDialog.show();
            //storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(storageRef + "").child(id_name+"/" + file_name);

            FirebaseStorage.getInstance().getReference().child(directory_name +"/" + file_name).putFile(file_path)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                            //btn2.setVisibility(View.INVISIBLE);
                            Upload.this.finish();
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

    public void init_title() {
        setTitle("  WWWhisper Project");
        ActionBar ab = getSupportActionBar();

        ab.setIcon(R.drawable.people);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }
}

