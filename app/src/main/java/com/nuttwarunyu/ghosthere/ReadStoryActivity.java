package com.nuttwarunyu.ghosthere;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ReadStoryActivity extends AppCompatActivity {

    ListView listView;
    List<ParseObject> parseObjectList;
    ProgressDialog progressDialog;
    private ArrayList<MyMarker> myMarkerArrayList = new ArrayList<MyMarker>();
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_story);

        listView = (ListView) findViewById(R.id.listView_newFeed);
        new TaskProcess().execute();
    }

    private class TaskProcess extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            myMarkerArrayList = new ArrayList<MyMarker>();

            try {
                ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("TestObject");

                parseQuery.whereDoesNotExist("marker_only");
                parseObjectList = parseQuery.find();

                for (ParseObject parseObject : parseObjectList) {
                    MyMarker myMarker = new MyMarker();
                    myMarker.setmTitle((String) parseObject.get("title_ghost"));
                    myMarker.setmLatitude((String) parseObject.get("lat_ghost"));
                    myMarker.setmLongitude((String) parseObject.get("lng_ghost"));
                    myMarker.setmIcon((String) parseObject.get("img_info"));
                    myMarker.setmStory((String) parseObject.get("story_ghost"));
                    myMarker.setmRadius((String) parseObject.get("radius"));
                    myMarker.setmColor((String) parseObject.get("zone"));

                    myMarkerArrayList.add(myMarker);
                    Log.d("myMarkerArrayList", "myMarkerArrayList : " + myMarker);
                }

            } catch (ParseException e) {
                Log.e("doInBackground", "Error : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            customAdapter = new CustomAdapter(getApplicationContext(), myMarkerArrayList);
            listView.setAdapter(customAdapter);
        }
    }

}
