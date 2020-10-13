package com.example.wwwhisper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Choice_list extends Activity {

    ArrayList<String> image_list = new ArrayList<>();

    String directory_name;

    int where;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choice);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        init_name_path();
        init_listView();
    }

    private void init_name_path() {
        Intent intent = getIntent();

        directory_name = intent.getStringExtra("directory_name");
        ArrayList<String> image_list = (ArrayList<String>) intent.getSerializableExtra("image_list");
        where = intent.getIntExtra("where", 0);


        TextView txt = findViewById(R.id.txt);

        if (where == 0) {
            txt.setText("녹음할 챕터를 고르시오.");
        } else if (where == 1) {
            txt.setText("텍스트 작성할 챕터를 고르시오.");
        } else {
            txt.setText("업로드할 챕터를 고르시오.");
        }

        for (String name : image_list) {
            this.image_list.add(name.substring(0, name.length() - 4));
        }
    }


    public void init_listView() {

        // adapterView
        ListView listView = findViewById(R.id.image_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                image_list);
        listView.setAdapter(adapter);

        if (!image_list.isEmpty()) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String image_name = image_list.get(position);

                    switch (where) {
                        case 0:
                            startActivity(new Intent(getApplicationContext(), Record.class)
                                    .putExtra("image_list", image_name)
                                    .putExtra("directory_name", directory_name));
                            Choice_list.this.finish();
                            break;
                        case 1:
                            startActivity(new Intent(getApplicationContext(), Text.class)
                                    .putExtra("image_list", image_name)
                                    .putExtra("directory_name", directory_name));
                            Choice_list.this.finish();
                            break;
                        case 2:
                            startActivity(new Intent(getApplicationContext(), Upload.class)
                                    .putExtra("chapter", image_name)
                                    .putExtra("directory_name", directory_name));
                            Choice_list.this.finish();
                            break;

                    }

                }
            });
        }
    }


}
