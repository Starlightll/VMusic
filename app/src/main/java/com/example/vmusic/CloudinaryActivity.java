package com.example.vmusic;

import android.app.Application;
import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryActivity extends Application { // Bạn nên đổi tên file này thành MyApplication
    @Override
    public void onCreate() {
        super.onCreate();

        Map<String, String> config = new HashMap<>();

        config.put("cloud_name", "dkujns7st");
        config.put("api_key", "581427787878588");
        config.put("api_secret", "zGIuMzo-kvWlNM6AjbA6leI5idM");

        // Khởi tạo MediaManager với cấu hình trên
        MediaManager.init(this, config);
    }
}
