package com.nuttwarunyu.ghosthere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

    public CustomAdapter(Context context, ArrayList<MyMarker> markers) {
        this.context = context;
        this.myMarkerArrayList = markers;
        mInflater = LayoutInflater.from(context);

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
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.story_listview, null);

            viewHolder.title = (TextView) view.findViewById(R.id.txt_title_name);
            viewHolder.story = (TextView) view.findViewById(R.id.txt_location);
            viewHolder.image = (ImageView) view.findViewById(R.id.img_ghost_listview);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.title.setText(myMarkerArrayList.get(position).getmTitle());
        viewHolder.story.setText(myMarkerArrayList.get(position).getmStory());

        return view;
    }

    private static class ViewHolder {

        TextView title;
        TextView story;
        ImageView image;
    }

}
