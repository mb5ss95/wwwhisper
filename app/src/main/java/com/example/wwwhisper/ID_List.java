package com.example.wwwhisper;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class ID_List extends AppCompatActivity implements View.OnClickListener {

    ArrayList<String> id_list;
    ArrayList<String> image_list;

    String directory_name;

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

        fab_main.setOnClickListener(ID_List.this);
        fab_sub1.setOnClickListener(ID_List.this);
        fab_sub2.setOnClickListener(ID_List.this);
        fab_sub3.setOnClickListener(ID_List.this);
    }

    public void init_title() {
        setTitle("  WWWhisper Project");
        ActionBar ab = getSupportActionBar();

        ab.setIcon(R.drawable.people);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    public void init_listView() {
        // adapterView
        ListView listView = findViewById(R.id.id_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                id_list);
        listView.setAdapter(adapter);

        if (id_list.get(0) != "데이터가 없습니다.") {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String id_name = id_list.get(position);

                    Intent intent = new Intent(getApplicationContext(), File_List.class);
                    intent.putExtra("directory_name", directory_name);
                    intent.putExtra("image_list", image_list);
                    intent.putExtra("id_name", id_name);
                    startActivity(intent);
                }
            });
        }
    }

    private void init_name_path() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");
        //directory_name
    }

    @Override
    public void onResume() {
        super.onResume();
        init_directory();
    }

    public void init_directory() {
        id_list = new ArrayList<>();
        image_list = new ArrayList<>();
        FirebaseStorage.getInstance().getReference().child(directory_name).listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        if (listResult.getPrefixes().isEmpty()) {
                            id_list.add("데이터가 없습니다.");
                        } else {
                            for (StorageReference prefix : listResult.getPrefixes()) {
                                id_list.add(prefix.getName());
                            }
                            for (StorageReference item : listResult.getItems()) {
                                //System.out.println(listResult.getItems());
                                //[gs://project-83e1e.appspot.com/test/song1.mp3, gs://project-83e1e.appspot.com/test/song2.mp3, gs://project-83e1e.appspot.com/test/song3.mp3, gs://project-83e1e.appspot.com/test/song4.mp3]

                                image_list.add(item.getName());
                            }
                        }
                        init_listView();
                        //adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.i("TAG", e.getMessage());
                        Toast.makeText(getApplicationContext(), "폴더를 불러올 수 없습니다.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_main:
                toggleFab();
                break;
            case R.id.fab_sub1:
                toggleFab();
                startActivity(new Intent(getApplicationContext(), Choice_list.class)
                        .putExtra("image_list", image_list)
                        .putExtra("directory_name", directory_name)
                        .putExtra("where", 0));
                break;
            case R.id.fab_sub2:
                toggleFab();
                startActivity(new Intent(getApplicationContext(), Choice_list.class)
                        .putExtra("image_list", image_list)
                        .putExtra("directory_name", directory_name)
                        .putExtra("where", 1));
                break;
            case R.id.fab_sub3:
                toggleFab();
                startActivity(new Intent(getApplicationContext(), Choice_list.class)
                        .putExtra("image_list", image_list)
                        .putExtra("directory_name", directory_name)
                        .putExtra("where", 2));
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
}