package com.example.wwwhisper;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Text extends AppCompatActivity {

    String directory_name;
    String image_name;

    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        init_title();
        init_name_path();
    }

    public void init_wiget(){

    }

    private void init_name_path() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");
        image_name = intent.getStringExtra("image_name");

        storageRef = FirebaseStorage.getInstance().getReference().child(directory_name);

        System.out.println("(Text) Get Directory Name : " + directory_name);
        System.out.println("(Text) Get Storage Reference : " + storageRef);
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