package com.example.vmusic.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.models.LyricLine;
import com.example.vmusic.models.PlayerManager;
import com.example.vmusic.ui.adapter.LyricAdapter;
import com.example.vmusic.viewmodel.PlayerViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream; // <-- Added for local file handling
import java.io.IOException;     // <-- Added for URL handling
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;             // <-- Added for URL handling
import java.util.ArrayList;
import java.util.Collections;   // <-- Added for Collections.sort
import java.util.Comparator;    // <-- Added for Comparator
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricFragment extends Fragment {

    private RecyclerView recyclerView;
    private LyricAdapter adapter;
    private List<LyricLine> lyricLines = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateLyricRunnable;
    private ExoPlayer player;
    private PlayerViewModel playerViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyric, container, false);
        recyclerView = view.findViewById(R.id.rv_lyrics);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LyricAdapter(lyricLines);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        player = PlayerManager.getPlayer(requireContext());
        playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);

        playerViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            if (song != null && song.getUrlLyric() != null) {
                loadLyrics(song.getUrlLyric());
            } else {
                lyricLines.clear();
                adapter.setLyrics(new ArrayList<>());
            }
        });

        updateLyricRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    long currentTime = player.getCurrentPosition();
                    int currentIndex = getCurrentLyricIndex(currentTime);
                    adapter.updateHighlight(currentIndex);

                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int firstVisible = layoutManager.findFirstVisibleItemPosition();
                        int lastVisible = layoutManager.findLastVisibleItemPosition();

//                        if (currentIndex != -1 && (currentIndex < firstVisible || currentIndex > lastVisible ||
//                                currentIndex < layoutManager.findFirstCompletelyVisibleItemPosition() ||
//                                currentIndex > layoutManager.findLastCompletelyVisibleItemPosition())) {
//                            int scrollToPosition = Math.max(0, currentIndex - (layoutManager.getChildCount() / 2));
//                            recyclerView.smoothScrollToPosition(scrollToPosition);
//                        }
                    }
                }
                handler.postDelayed(this, 100);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            handler.post(updateLyricRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateLyricRunnable);
    }

    private int getCurrentLyricIndex(long time) {
        int bestIndex = -1;
        for (int i = 0; i < lyricLines.size(); i++) {
            if (time >= lyricLines.get(i).getTime()) {
                bestIndex = i;
            } else {
                break;
            }
        }
        return bestIndex;
    }

    // Your original loadLyrics method with fixes
    private void loadLyrics(String lyricPath) {
        new Thread(() -> {
            try {
                InputStream inputStream;
                if (lyricPath.startsWith("http://") || lyricPath.startsWith("https://")) {
                    // Handling URL path
                    inputStream = new URL(lyricPath).openStream();
                } else {
                    // Handling local file path
                    File file = new File(lyricPath);
                    if (!file.exists()) {
                        Log.e("LyricLoad", "Local file not found: " + lyricPath);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            lyricLines.clear();
                            adapter.setLyrics(new ArrayList<>());
                        });
                        return;
                    }
                    inputStream = new FileInputStream(file);
                }

                StringBuilder result = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                }

                List<LyricLine> loadedLyrics = parseLrc(result.toString());
                Collections.sort(loadedLyrics, new Comparator<LyricLine>() {
                    @Override
                    public int compare(LyricLine l1, LyricLine l2) {
                        return Float.compare(l1.getTime(), l2.getTime());
                    }
                });

                new Handler(Looper.getMainLooper()).post(() -> {
                    lyricLines.clear();
                    lyricLines.addAll(loadedLyrics);
                    adapter.setLyrics(loadedLyrics);
                });

            } catch (IOException e) { // Catch IOException for network/file operations
                e.printStackTrace();
                Log.e("LyricLoad", "Error loading lyrics: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> {
                    lyricLines.clear();
                    adapter.setLyrics(new ArrayList<>());
                });
            } catch (Exception e) { // Catch other potential exceptions during parsing
                e.printStackTrace();
                Log.e("LyricLoad", "General error during lyric processing: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> {
                    lyricLines.clear();
                    adapter.setLyrics(new ArrayList<>());
                });
            }
        }).start();
    }

    private List<LyricLine> parseLrc(String lrcText) {
        List<LyricLine> list = new ArrayList<>();
        // Make sure this regex matches your LRC file format precisely
        Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)");
        String[] lines = lrcText.split("\n");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                try {
                    int min = Integer.parseInt(matcher.group(1));
                    int sec = Integer.parseInt(matcher.group(2));
                    String msStr = matcher.group(3);
                    long millis;
                    if (msStr.length() == 2) { // e.g., .34 -> 340ms
                        millis = (long) ((min * 60 + sec) * 1000 + Integer.parseInt(msStr) * 10);
                    } else { // e.g., .345 -> 345ms
                        millis = (long) ((min * 60 + sec) * 1000 + Integer.parseInt(msStr));
                    }
                    String text = matcher.group(4).trim();
                    list.add(new LyricLine(millis, text));
                } catch (NumberFormatException e) {
                    Log.e("ParseLRC", "Error parsing time or text: " + line);
                }
            }
        }
        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateLyricRunnable);
        handler.removeCallbacksAndMessages(null);
    }
}