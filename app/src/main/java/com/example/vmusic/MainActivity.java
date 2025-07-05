package com.example.vmusic;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import dao.SongDao;
import database.AppDatabase;
import entity.Song;
import viewmodel.SongViewModel;

public class MainActivity extends AppCompatActivity {
    public SongViewModel songViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        Song song = new Song();
        song.name = "Text";
        song.artist = "Text";
        song.audioUrl = "Text";
        song.image = "Text";
        song.urlLyric = "Text";
        song.listenCounts = 1;

        songViewModel.insert(song);

        LiveData<List<Song>> list = songViewModel.getAllSongs();
        ((TextView)findViewById(R.id.textView)).setText(list.toString());

    }
}