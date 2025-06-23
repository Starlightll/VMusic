package com.example.vmusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.adapter.SongAdapter;
import com.example.vmusic.model.Song;

import java.util.ArrayList;
import java.util.List;

public class ListPlaySongFragment extends Fragment {

    public ListPlaySongFragment() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_list_play_song, container, false);

        List<Song> songList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            songList.add(new Song( "Lac Troi", "Son Tung M-TP",R.drawable.back));
        }

        SongAdapter adapterSong = new SongAdapter(songList);
        RecyclerView rc = view.findViewById(R.id.rec_song);
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.setAdapter(adapterSong);

        return view;
    }

}
