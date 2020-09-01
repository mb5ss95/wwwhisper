package com.example.wwwhisper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class Adapter extends BaseAdapter {

    Context context;
    int layout;
    ArrayList<String> al;
    LayoutInflater inf;

    public Adapter(Context context, int layout, ArrayList<String> al) {
        this.context = context;
        this.layout = layout;
        this.al = al;
        this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { // 총 데이터의 개수
        return al.size();
    }

    @Override
    public Object getItem(int position) { // 해당 행의 데이터
        return al.get(position);
    }

    @Override
    public long getItemId(int position) { // 해당 행의 유니크한 id
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inf.inflate(layout, null);

        TextView tv1 = convertView.findViewById(R.id.txt1);
        TextView tv2 = convertView.findViewById(R.id.txt2);

        String[] temp = al.get(position).split(" ");

        for(int i=2; i<temp.length; i++){
            temp[1] = temp[1] + " " + temp[i];
        }

        System.out.println("(Adapter) Get Writer_Name & File_Name : " + temp[0] + ", " + temp[1]);
        //(Adapter) Get Writer_Name & File_Name : [문병수], nako1.jpg

        tv1.setText(temp[0]);
        tv2.setText(temp[1]);

        return convertView;
    }
}
