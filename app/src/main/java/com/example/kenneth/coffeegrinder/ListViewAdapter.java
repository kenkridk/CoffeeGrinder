package com.example.kenneth.coffeegrinder;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Kenneth on 22-04-2015.
 */
public class ListViewAdapter extends ArrayAdapter<ListViewClass>{

    public ListViewAdapter(Context context, ArrayList<ListViewClass> list){
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ListViewClass lvc = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);

        TextView name = (TextView) convertView.findViewById(R.id.demoTitle);
        name.setText(lvc.getName());

        TextView description = (TextView) convertView.findViewById(R.id.demoDescription);
        description.setText(lvc.getDescription());

        final RelativeLayout leftContainer = (RelativeLayout) convertView.findViewById(R.id.leftContainer);
        final RelativeLayout rightContainer = (RelativeLayout) convertView.findViewById(R.id.rightContainer);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OnClickListener", "You clicked" + lvc.getName());
                leftContainer.setX(leftContainer.getX()-100f);

            }
        });

        return convertView;
    }

}
