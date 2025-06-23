package com.example.vmusic;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.adapter.PlaylistAdapter;
import com.example.vmusic.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PlaylistAdapter adapter;
    List<Playlist> playlistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_playlist);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        playlistList = new ArrayList<>();
        playlistList.add(new Playlist("Mind Tides", "LMR Records", R.drawable.ic_playlist_placeholder));
        playlistList.add(new Playlist("Chill Hits", "Spotify", R.drawable.ic_playlist_placeholder));
        playlistList.add(new Playlist("Lofi Relax", "Lofi World", R.drawable.ic_playlist_placeholder));

        adapter = new PlaylistAdapter(playlistList);
        recyclerView.setAdapter(adapter);
    }
}
