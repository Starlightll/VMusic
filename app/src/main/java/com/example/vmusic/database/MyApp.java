package com.example.vmusic.database;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

import com.example.vmusic.models.PlayerManager;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Map config = new HashMap();
        config.put("cloud_name", "dkujns7st");
        config.put("api_key", "581427787878588");
        config.put("upload_preset", "zGIuMzo-kvWlNM6AjbA6leI5idM");
        config.put("secure", true);

        MediaManager.init(this, config);

        PlayerManager.init(this);
    }
}
