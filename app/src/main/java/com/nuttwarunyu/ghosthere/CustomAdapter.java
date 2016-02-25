package com.nuttwarunyu.ghosthere;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Dell-NB on 22/2/2559.
 */
public class CustomAdapter extends BaseAdapter {

    Context context;
    private ViewHolder mViewHolder;
    private LayoutInflater mInflater;
    ArrayList<MyMarker> myMarkerArrayList = null;
    ArrayList<MyMarker> arrayList;
    Typeface myFont;

    public CustomAdapter(Context context, ArrayList<MyMarker> markers) {
        this.context = context;
        this.myMarkerArrayList = markers;
        mInflater = LayoutInflater.from(context);
        myFont = Typeface.createFromAsset(this.context.getAssets(), "CSPraJad.otf");


    }

    @Override
    public int getCount() {
        return myMarkerArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return myMarkerArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.story_listview, null);

            viewHolder.title = (TextView) view.findViewById(R.id.txt_title_name);
            viewHolder.province = (TextView) view.findViewById(R.id.txt_location);
            viewHolder.image = (ImageView) view.findViewById(R.id.img_ghost_listview);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.title.setTypeface(myFont);
        viewHolder.province.setTypeface(myFont);

        viewHolder.title.setText(myMarkerArrayList.get(position).getmTitle());
        viewHolder.province.setText(myMarkerArrayList.get(position).getmProvince());
        viewHolder.story = myMarkerArrayList.get(position).getmStory();

        Glide.with(context).load(myMarkerArrayList.get(position).getmPhotoFile()).centerCrop().into(viewHolder.image);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GhostStoryActivity.class);
                intent.putExtra("title", viewHolder.title.getText());
                intent.putExtra("story", viewHolder.story);
                intent.putExtra("province", viewHolder.province.getText());
                intent.putExtra("photoFile",myMarkerArrayList.get(position).getmPhotoFile());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        return view;
    }

    private static class ViewHolder {

        TextView title;
        String story;
        TextView province;
        ImageView image;
    }

}
