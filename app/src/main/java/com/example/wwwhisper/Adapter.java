package com.example.wwwhisper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Data> data_list = new ArrayList<>();

    // ListViewAdapter의 생성자
    public Adapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return data_list.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sample, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView chapter = (TextView) convertView.findViewById(R.id.chapter);
        TextView audio = (TextView) convertView.findViewById(R.id.audio);
        TextView text = (TextView) convertView.findViewById(R.id.text);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Data data = data_list.get(position);

        String temp = data.get_chapter();
        int num = temp.length() - 4;

        // 아이템 내 각 위젯에 데이터 반영
        chapter.setText(data.get_chapter().substring(0, num));
        if (data.get_audio() == "-") {
            audio.setText("X");
        } else {
            audio.setText("오디오 있음");
        }
        if (data.get_text() == "-") {
            text.setText("X");
        } else {
            text.setText("텍스트 있음");
        }

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Data getItem(int position) {
        return data_list.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String chapter, String audio, String text) {
        Data item = new Data();

        item.set_chapter(chapter);
        item.set_audio(audio);
        item.set_text(text);

        data_list.add(item);
    }
}