package com.example.vmusic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.Interface.OnSongClickListener;
import com.example.vmusic.R;
import com.example.vmusic.entity.Song;

import java.util.List;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.ListSongViewHolder> {

    private Context context;
    private List<Song> songs;

    private OnSongClickListener listener;
    public ListSongAdapter(Context context, List<Song> songs, OnSongClickListener listener) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_song, parent, false);
        return new ListSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListSongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.tvTitle.setText(song.getName());
        holder.tvArtist.setText(song.getArtist());
        Glide.with(context)
                .load(song.getImage())
                .into(holder.imgSong);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }
    public static class ListSongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong;
        TextView tvTitle, tvArtist;

        public ListSongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imv_img);
            tvTitle = itemView.findViewById(R.id.tv_name);
            tvArtist = itemView.findViewById(R.id.tv_singer);
        }
    }
}
