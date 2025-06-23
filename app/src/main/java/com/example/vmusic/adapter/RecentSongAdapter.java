package com.example.vmusic.adapter;

import android.content.Context;
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

public class RecentSongAdapter extends RecyclerView.Adapter<RecentSongAdapter.RecentViewHolder> {
    private Context context;
    private List<Song> songList;

    public RecentSongAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    class RecentViewHolder extends RecyclerView.ViewHolder{
        ImageView imgRecent;
        TextView tvTitle , txArtist;
        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRecent = itemView.findViewById(R.id.imgRecent);
            tvTitle = itemView.findViewById(R.id.tvRecentTitle);
            txArtist = itemView.findViewById(R.id.tvRecentArtist);
        }
    }

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent , parent , false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.imgRecent.setImageResource(song.getImageUrl());
        holder.tvTitle.setText(song.getName());
        holder.txArtist.setText(song.getArtist().getName());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }



}
