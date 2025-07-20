package com.example.vmusic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.Interface.OnSongClickListener;
import com.example.vmusic.R;
import com.example.vmusic.entity.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsByArtistAdapter extends RecyclerView.Adapter<SongsByArtistAdapter.SongViewHolder> {
    private Context context;
    private List<Song> songList;
    private OnSongClickListener listener;

    public SongsByArtistAdapter(Context context, List<Song> songList, OnSongClickListener listener) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSongThumbnail;
        TextView tvSongTitle, tvListenCount;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSongThumbnail = itemView.findViewById(R.id.imgSongThumbnail);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvListenCount = itemView.findViewById(R.id.tvListenCount);
        }
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song_by_artist, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.tvSongTitle.setText(song.getName());
        holder.tvListenCount.setText(song.getListenCounts() + " lượt nghe");

        Glide.with(context)
                .load(song.getImage())
                .placeholder(R.drawable.ic_music_placeholder)
                .into(holder.imgSongThumbnail);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public void setSongs(List<Song> songs) {
        this.songList = songs;
        notifyDataSetChanged();
    }

    public List<Song> getSongs() {
        return songList;
    }
}

