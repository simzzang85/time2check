package com.simstudious.time2check;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class UrlAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    public static final String FIRST_COLUMN="First";
    public static final String SECOND_COLUMN="Second";
    public static final String THIRD_COLUMN="Third";

    public UrlAdapter(Activity activity,ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    private class ViewHolder{
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder;

        LayoutInflater inflater = activity.getLayoutInflater();

        if(convertView == null){

            convertView = inflater.inflate(R.layout.dialog_rows, null);
            holder = new ViewHolder();

            holder.txtFirst = convertView.findViewById(R.id.tvRowId);
            holder.txtSecond = convertView.findViewById(R.id.tvUrl);
            //holder.txtThird = convertView.findViewById(R.id.tvDel);

            convertView.setTag(holder);
        }else{

            holder=(ViewHolder) convertView.getTag();
        }

        HashMap<String, String> map=list.get(position);
        holder.txtFirst.setText(map.get(FIRST_COLUMN));
        holder.txtSecond.setText(map.get(SECOND_COLUMN));
        //holder.txtThird.setText(map.get(THIRD_COLUMN));
        //holder.txtFourth.setText(map.get(FOURTH_COLUMN));

        return convertView;
    }
}
