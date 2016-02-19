package com.nuttwarunyu.ghosthere;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Dell-NB on 18/2/2559.
 */
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

    }
}
