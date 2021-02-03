package com.example.wwwhisper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
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


public class Text extends AppCompatActivity {

    EditText editText;

    String directory_name;
    String image_name;

    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        init_title();
        init_name_path();
        init_wiget();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                String result = data.getStringExtra("text_HandWrite");
                editText.setText(editText.getText() + result);
            } else {
                int result = data.getIntExtra("Color_Books", 0);
                editText.setTextColor(result);
            }
        }
    }


    public void init_wiget() {
        editText = findViewById(R.id.text_edit);

        findViewById(R.id.text_rewrite_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        findViewById(R.id.text_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File_manager file_manager = new File_manager(Text.this);
                Uri uri = file_manager.save("/"+image_name+".txt", editText.getText().toString());

                final ProgressDialog progressDialog = new ProgressDialog(Text.this);
                progressDialog.setTitle("업로드중...");
                progressDialog.show();

                FirebaseStorage.getInstance().getReference().child(directory_name + "/test/"+image_name+".txt").putFile(uri)
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
            }
        });
    }

    private void init_name_path() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");
        image_name = intent.getStringExtra("image_name");

        storageRef = FirebaseStorage.getInstance().getReference().child(directory_name);

        System.out.println("(Text) Get Directory Name : " + directory_name);
        System.out.println("(Text) Get Image Name : " + image_name);

        TextView text_view = findViewById(R.id.txt_view);
        text_view.setText(directory_name + "/" + image_name + ".txt");
        //(Text) Get Directory Name : story1
        //(Text) Get Storage Reference : gs://project-83e1e.appspot.com/story1
    }


    public void init_title() {
        setTitle("  WWWhisper Project");
        ActionBar ab = getSupportActionBar();

        ab.setIcon(R.drawable.people);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }
}