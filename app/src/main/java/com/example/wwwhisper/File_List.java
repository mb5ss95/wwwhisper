package com.example.wwwhisper;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class File_List extends AppCompatActivity {

    String directory_name;
    String id_name;

    Adapter adapter = new Adapter();
    ArrayList<String> image_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        init_title();
        init_name_path();
        init_file();
    }


    /*@Override
    public void onResume() {
        super.onResume();
        init_file();
    }*/

    private void init_name_path() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");
        image_list = (ArrayList<String>) intent.getSerializableExtra("image_list");
        id_name = intent.getStringExtra("id_name");
        //directory_name
    }

    public void init_file() {

        FirebaseStorage.getInstance().getReference().child(directory_name + "/" + id_name).listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        ArrayList<String> audio_list = new ArrayList<>();
                        ArrayList<String> text_list = new ArrayList<>();
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
                        init_data(audio_list, text_list);
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

    public void init_data(ArrayList<String> audio_list, ArrayList<String> text_list) {
        for (int i = 0; i < image_list.size(); i++) {
            String image_name = image_list.get(i);
            String audio_name = "";
            String text_name = "";

            int image_num = image_name.length() - 4;
            int audio_num = 0;
            int text_num = 0;

            try {
                audio_name = audio_list.get(i);
                audio_num = audio_name.length() - 4;
            } catch (IndexOutOfBoundsException e) {
            }
            try {
                text_name = text_list.get(i);
                text_num = text_name.length() - 4;
            } catch (IndexOutOfBoundsException e) {
            }

            image_name = image_name.substring(0, image_num);

            if (!image_name.equals(audio_name.substring(0, audio_num))) {
                audio_list.add(i, "-");
            }
            if (!image_name.equals(text_name.substring(0, text_num))) {
                text_list.add(i, "-");
            }

            adapter.addItem(image_list.get(i), audio_list.get(i), text_list.get(i));
        }
        init_listView();

    }

    public void init_listView() {
        // adapterView
        ListView listView = findViewById(R.id.file_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Data data = adapter.getItem(position);
                String image_name = data.get_chapter();
                String audio_name = data.get_audio();
                String text_name = data.get_text();

                Intent intent = new Intent(getApplicationContext(), Show.class);
                intent.putExtra("directory_name", directory_name);
                intent.putExtra("id_name", id_name);
                intent.putExtra("image_name", image_name);
                intent.putExtra("audio_name", audio_name);
                intent.putExtra("text_name", text_name);
                startActivity(intent);
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
    public void onBackPressed() {
        super.onBackPressed();
    }
}