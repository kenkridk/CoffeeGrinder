package com.example.kenneth.coffeegrinder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kenneth on 22-04-2015.
 */
public class ListViewAdapter extends ArrayAdapter<ListViewClass>{

    private TranslateAnimation animation;
    private boolean isCellsCollapsed[];

    public ListViewAdapter(Context context, ArrayList<ListViewClass> list){
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ListViewClass lvc = getItem(position);

        notifyDataSetChanged();//notify activity that list has changed...

        isCellsCollapsed = new boolean[position+1];

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

        Button remove = (Button) convertView.findViewById(R.id.buttonRemove);
        Button mute = (Button) convertView.findViewById(R.id.buttomMute);

        rightContainer.setVisibility(View.VISIBLE);

        for(int i = 0; i < isCellsCollapsed.length; i++){
            isCellsCollapsed[i] = true;
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCellsCollapsed[lvc.getId()]){
                    animation = new TranslateAnimation(0,-200,0,0);
                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    leftContainer.startAnimation(animation);
                    isCellsCollapsed[lvc.getId()] = false;
                }else{
                    animation = new TranslateAnimation(-200,0,0,0);
                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    leftContainer.startAnimation(animation);
                    isCellsCollapsed[lvc.getId()] = true;
                }
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Remove", "Try to remove" + lvc.getId());
            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Mute", "Mute button was pressed");
            }
        });

        return convertView;
    }
}
