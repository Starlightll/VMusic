package com.example.vmusic;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.adapter.RecentSongAdapter;
import com.example.vmusic.model.Artist;
import com.example.vmusic.model.Song;
import com.example.vmusic.model.dbContext;

import java.util.ArrayList;
import java.util.List;

public class HomeAppMusic extends AppCompatActivity {

    dbContext dbContext = new dbContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_app_music);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        RecyclerView recyclerView = findViewById(R.id.recyclerRecent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL , false));
        
        Artist artist = new Artist("Sơn Tùng M-TP", R.drawable.nhac);

        List<Song> recentSong = dbContext.getSongs(12);
//        recentSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        recentSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        recentSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        recentSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        recentSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        recentSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        recentSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        recentSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        recentSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));

        RecentSongAdapter adapter = new RecentSongAdapter(this , recentSong);
        recyclerView.setAdapter(adapter);

        RecyclerView recyclerView1 = findViewById(R.id.recyclerPopular);
        recyclerView1.setLayoutManager(new GridLayoutManager(this , 3));

        List<Song> popularSong = dbContext.getSongs(12);
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.nhac, R.drawable.nhac));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));
//        popularSong.add(new Song("Lạc Trôi", artist, R.drawable.circle_background, R.drawable.circle_background));

        RecentSongAdapter adapter1 = new RecentSongAdapter(this , popularSong);
        recyclerView1.setAdapter(adapter1);

    }
}