package com.example.vmusic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ChapterHolder>{

    public List<Song> songs;
    public SongAdapter(List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public ChapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_play_song,parent,false);
        return new ChapterHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterHolder holder, int position) {
        holder.imv.setImageResource(songs.get(position).getImageResId());
        holder.tv_title.setText(songs.get(position).getTitle());
        holder.tv_des.setText(songs.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class ChapterHolder extends RecyclerView.ViewHolder{
        public TextView tv_title;
        public TextView tv_des;
        public ImageView imv;

        public ChapterHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_des = itemView.findViewById(R.id.tv_des);
            imv = itemView.findViewById(R.id.imv_img);

        }
    }
}
