package com.example.wwwhisper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

public class Upload extends AppCompatActivity implements Button.OnClickListener {

    Button btn1;
    ImageButton btn2;
    TextView txt;

    String directory_name;
    String audio_name;

    StorageReference storageRef;

    Uri audio_path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        init_title();
        init_wiget();
        init_data();
    }

    public void init_wiget() {
        btn1 = findViewById(R.id.choose);
        btn2 = findViewById(R.id.upload);

        btn1.setOnClickListener(Upload.this);
        btn2.setOnClickListener(Upload.this);

        txt = findViewById(R.id.upload_txt1);
    }

    public void init_data() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");

        storageRef = FirebaseStorage.getInstance().getReference().child(directory_name);

        //System.out.println("(upload) Get Directory Name : " + directory_name);
        //System.out.println("(upload) Get Storage Reference : " + storageRef);
        //(upload) Get Directory Name : story1
        //(upload) Get Storage Reference : gs://project-83e1e.appspot.com/story1
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

        dialog.setTitle("이름을 입력하고, 챕터를 선택하세요.");
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

                        EditText editText1 = f.findViewById(R.id.add_id);
                        Spinner spinner = f.findViewById(R.id.add_name);


                        String temp1 = editText1.getText().toString();
                        final String[] temp2 = new String[1];

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                temp2[0] = adapterView.getItemAtPosition(i).toString();
                                Toast.makeText(getApplicationContext(), temp2[0], Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        if(temp1.isEmpty()) temp1 = "아무개";
                        if(temp2[0] == null) temp2[0] = "아무오디오";

                        txt.setText("[" + temp1 + "] " + temp2[0]);
                        audio_name = txt.getText().toString() + ".mp3";

                        System.out.println("(upload) Get Audio Name & Audio Path : " + audio_name + ", " + audio_path);

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
            System.out.println("test "+ file_path + ", " + file_name);
            //test content://com.android.externalstorage.documents/document/primary%3AUpbeat%20Ukulele%20Background%20Music%20-%20That%20Positive%20Feeling%20by%20Alumo.mp3, [test] 아무오디오.mp3
            final ProgressDialog progressDialog = new ProgressDialog(Upload.this);
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

