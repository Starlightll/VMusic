package com.example.vmusic.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.models.LyricLine;
import com.example.vmusic.ui.adapter.LyricAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricFragment extends Fragment {

    private static final String ARG_LYRIC_PATH = "lyric_path";

    private RecyclerView recyclerView;
    private LyricAdapter adapter;
    private List<LyricLine> lyricLines = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private ExoPlayer player;
    private String lyricPath;

    public static LyricFragment newInstance(String lyricPath) {
        LyricFragment fragment = new LyricFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LYRIC_PATH, lyricPath);
        fragment.setArguments(args);
        return fragment;
    }

    public void setPlayer(ExoPlayer player) {
        this.player = player;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lyricPath = getArguments() != null ? getArguments().getString(ARG_LYRIC_PATH) : null;

        if (lyricPath != null && lyricPath.startsWith("http")) {
            loadLyricsFromUrl(lyricPath);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyric, container, false);
        recyclerView = view.findViewById(R.id.rv_lyrics);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LyricAdapter(lyricLines);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void startSync() {
        if (player == null) return;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    long currentTime = player.getCurrentPosition();
                    int currentIndex = getCurrentLyricIndex(currentTime);
                    adapter.updateHighlight(currentIndex);
                    recyclerView.scrollToPosition(currentIndex);
                }
                handler.postDelayed(this, 500);
            }
        }, 0);
    }


    private int getCurrentLyricIndex(long time) {
        for (int i = 0; i < lyricLines.size() - 1; i++) {
            if (time >= lyricLines.get(i).getTime() && time < lyricLines.get(i + 1).getTime()) {
                return i;
            }
        }
        return lyricLines.isEmpty() ? 0 : lyricLines.size() - 1;
    }

    private List<LyricLine> parseLrc(String lrcText) {
        List<LyricLine> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(\\d+):(\\d+\\.\\d+)](.*)");
        String[] lines = lrcText.split("\n");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                int min = Integer.parseInt(matcher.group(1));
                float sec = Float.parseFloat(matcher.group(2));
                long millis = (long) ((min * 60 + sec) * 1000);
                String text = matcher.group(3).trim();
                list.add(new LyricLine(millis, text));
            }
        }

        return list;
    }

    private void loadLyricsFromUrl(String urlString) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                reader.close();

                List<LyricLine> loadedLyrics = parseLrc(result.toString());

                new Handler(Looper.getMainLooper()).post(() -> {
                    lyricLines.clear();
                    lyricLines.addAll(loadedLyrics);

                    adapter.notifyDataSetChanged();

                    startSync();
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //handler.removeCallbacksAndMessages(null); // Dừng cập nhật lời
    }
}
