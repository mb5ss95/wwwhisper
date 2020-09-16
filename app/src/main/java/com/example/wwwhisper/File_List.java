package com.example.wwwhisper;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class File_List extends AppCompatActivity {

    String directory_name;
    String image_name;
    String id_name;

    ArrayList<String> audio_list;
    ArrayList<String> text_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        init_title();
        init_name_path();
    }


    @Override
    public void onResume() {
        super.onResume();
        init_file();
    }

    private void init_name_path() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");
        image_name = intent.getStringExtra("image_name");
        id_name = intent.getStringExtra("id_name");
        //directory_name
    }

    public void init_file() {
        audio_list = new ArrayList<>();
        text_list = new ArrayList<>();

        FirebaseStorage.getInstance().getReference().child(directory_name + "/" + id_name).listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        ArrayList<String> file_list = new ArrayList<>();
                        if (listResult.getItems().isEmpty()) {
                            file_list.add("데이터가 없습니다.");
                        } else {
                            for (StorageReference item : listResult.getItems()) {
                                //System.out.println(listResult.getItems());
                                //[gs://project-83e1e.appspot.com/test/song1.mp3, gs://project-83e1e.appspot.com/test/song2.mp3, gs://project-83e1e.appspot.com/test/song3.mp3, gs://project-83e1e.appspot.com/test/song4.mp3]
                                String temp = item.getName();
                                int len = temp.length();
                                if (temp.substring(len - 4).equals(".txt")) {
                                    text_list.add(temp);
                                } else {
                                    audio_list.add(temp);
                                }
                            }
                            file_list.addAll(audio_list);
                            file_list.addAll(text_list);
                        }
                        init_listView(file_list);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", e.getMessage());
                        Toast.makeText(getApplicationContext(), "데이터를 불러올 수 없습니다.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void init_listView(final ArrayList<String> file_list) {

        // adapterView
        ListView listView = findViewById(R.id.file_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                file_list);
        listView.setAdapter(adapter);


        if (file_list.get(0) != "데이터가 없습니다.") {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String file_name = file_list.get(position);

                    Intent intent = new Intent(getApplicationContext(), Show.class);
                    intent.putExtra("directory_name", directory_name);
                    intent.putExtra("id_name", id_name);
                    intent.putExtra("file_name", file_name);
                    intent.putExtra("image_name", image_name);
                    startActivity(intent);
                }
            });
        }
    }

    public void init_title() {
        setTitle("  WWWhisper Project");
        ActionBar ab = getSupportActionBar();

        ab.setIcon(R.drawable.people);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.add:
                play_Btn();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void play_Btn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(File_List.this);

        builder.setTitle(" 대상을 선택하세요.").setIcon(R.drawable.lib2).setItems(R.array.object,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //String[] id = getResources().getStringArray(R.array.object);
                        switch (which) {
                            case 0:
                                startActivity(new Intent(getApplicationContext(), Record.class)
                                        .putExtra("image_name", image_name)
                                        .putExtra("directory_name", directory_name));
                                break;
                            case 1:
                                startActivity(new Intent(getApplicationContext(), Text.class)
                                        .putExtra("image_name", image_name)
                                        .putExtra("directory_name", directory_name));
                                break;
                            default:
                                startActivity(new Intent(getApplicationContext(), Upload.class)
                                        .putExtra("directory_name", directory_name));
                                break;
                        }
                    }
                }).show();
    }*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}