package com.example.vmusic.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;

import java.util.ArrayList;
import java.util.List;

import com.example.vmusic.models.LyricLine;

public class LyricAdapter extends RecyclerView.Adapter<LyricAdapter.LyricViewHolder> {

    private List<LyricLine> lyrics = new ArrayList<>();
    private int currentIndex = -1;

    public LyricAdapter(List<LyricLine> lyrics) {
        if (lyrics != null) {
            this.lyrics = lyrics;
        }
    }


    public void setLyrics(List<LyricLine> newLyrics) {
        this.lyrics = newLyrics != null ? newLyrics : new ArrayList<>();
        currentIndex = -1;
        notifyDataSetChanged();
    }

    // Gọi mỗi khi cần highlight dòng mới
    public void updateHighlight(int index) {
        if (index < 0 || index >= lyrics.size()) return;

        if (index != currentIndex) {
            int previousIndex = currentIndex;
            currentIndex = index;

            if (previousIndex >= 0 && previousIndex < getItemCount()) {
                notifyItemChanged(previousIndex);
            }
            notifyItemChanged(currentIndex);
        }
    }

    @NonNull
    @Override
    public LyricViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lyric_line, parent, false);
        return new LyricViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LyricViewHolder holder, int position) {
        LyricLine line = lyrics.get(position);
        holder.textView.setText(line.getText());
        holder.textView.setTextColor(position == currentIndex ? Color.YELLOW : Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return lyrics.size();
    }

    static class LyricViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public LyricViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_lyric_line);
        }
    }
}
