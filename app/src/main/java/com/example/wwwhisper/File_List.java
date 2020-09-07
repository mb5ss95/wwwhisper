package com.example.wwwhisper;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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


public class File_List extends AppCompatActivity implements View.OnClickListener {

    ListView listView;

    String directory_name;
    String image_name;

    StorageReference storageRef;

    ArrayList<String> audio_list;
    ArrayList<String> text_list;

    FloatingActionButton fab_main, fab_sub1, fab_sub2, fab_sub3;
    Animation fab_open, fab_close;
    boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fab);

        init_wiget();
        init_title();
        init_name_path();
    }

    private void init_wiget() {
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.file_up);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.file_down);

        fab_main = findViewById(R.id.fab_main);
        fab_sub1 = findViewById(R.id.fab_sub1);
        fab_sub2 = findViewById(R.id.fab_sub2);
        fab_sub3 = findViewById(R.id.fab_sub3);

        fab_main.setOnClickListener(File_List.this);
        fab_sub1.setOnClickListener(File_List.this);
        fab_sub2.setOnClickListener(File_List.this);
        fab_sub3.setOnClickListener(File_List.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        init_file();
    }

    private void init_name_path() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");
        //directory_name

        storageRef = FirebaseStorage.getInstance().getReference().child(directory_name);

        System.out.println("(File_List) Get Directory Name : " + directory_name);
        System.out.println("(File_List) Get Storage Reference : " + storageRef);
        //(File_List) Get Directory Name : story1
        //(File_List) Get Storage Reference : gs://project-83e1e.appspot.com/story1
    }

    public void init_file() {
        audio_list = new ArrayList<>();
        text_list = new ArrayList<>();

        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            //System.out.println(listResult.getItems());
                            //[gs://project-83e1e.appspot.com/test/song1.mp3, gs://project-83e1e.appspot.com/test/song2.mp3, gs://project-83e1e.appspot.com/test/song3.mp3, gs://project-83e1e.appspot.com/test/song4.mp3]
                            String temp = item.getName();
                            int len = temp.length();
                            if (temp.split(" ")[0].equals("[관리자]")) {
                                image_name = temp;
                                System.out.println("(File_List) Get Image Name : " + image_name);
                                //(File_List) Get Image Name : [관리자] nako555.jpg
                            } else {
                                if (temp.substring(len - 4).equals(".mp3")) {
                                    audio_list.add(temp);
                                } else if (temp.substring(len - 4).equals(".txt")) {
                                    text_list.add(temp);
                                }
                            }
                        }
                        init_listView();
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

    public void init_listView() {

        // adapterView
        listView = findViewById(R.id.file_list);
        listView.setAdapter(new Adapter(
                getApplicationContext(),
                R.layout.list_item,
                audio_list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String file_name = audio_list.get(position);
                String temp = file_name.substring(file_name.length() - 4);

                if (temp.equals(".mp3")) {
                    Intent intent = new Intent(getApplicationContext(), Show.class);
                    intent.putExtra("directory_name", directory_name);
                    intent.putExtra("audio_name", file_name);
                    intent.putExtra("image_name", image_name);
                    startActivity(intent);
                }
            }
        });
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_main:
                toggleFab();
                break;
            case R.id.fab_sub1:
                toggleFab();
                startActivity(new Intent(getApplicationContext(), Record.class)
                        .putExtra("image_name", image_name)
                        .putExtra("directory_name", directory_name));
                break;
            case R.id.fab_sub2:
                toggleFab();
                startActivity(new Intent(getApplicationContext(), Text.class)
                        .putExtra("image_name", image_name)
                        .putExtra("directory_name", directory_name));
                break;
            case R.id.fab_sub3:
                toggleFab();
                startActivity(new Intent(getApplicationContext(), Upload.class)
                        .putExtra("directory_name", directory_name));
                break;
        }
    }

    private void toggleFab() {
        if (isFabOpen) {
            set_close();
        } else {
            set_open();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isFabOpen)
                        set_close();
                }
            }, 3000);
        }
    }

    private void set_open() {
        fab_main.setImageResource(R.drawable.close);
        fab_sub1.startAnimation(fab_open);
        fab_sub2.startAnimation(fab_open);
        fab_sub3.startAnimation(fab_open);
        fab_sub1.setClickable(true);
        fab_sub2.setClickable(true);
        fab_sub3.setClickable(true);
        isFabOpen = true;
    }

    private void set_close() {
        fab_main.setImageResource(R.drawable.add);
        fab_sub1.startAnimation(fab_close);
        fab_sub2.startAnimation(fab_close);
        fab_sub3.startAnimation(fab_close);
        fab_sub1.setClickable(false);
        fab_sub2.setClickable(false);
        fab_sub3.setClickable(false);
        isFabOpen = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}