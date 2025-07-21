package com.example.vmusic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.Interface.OnSongClickListener;
import com.example.vmusic.R;
import com.example.vmusic.entity.Song;
import com.example.vmusic.viewmodel.SongViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.ListSongViewHolder> {

    private final Context context;
    private List<Song> songs;
    private final SongViewModel songViewModel;
    private final int userId;
    private final OnSongClickListener listener;
    private Set<Integer> favoriteSongIds = new HashSet<>();

    public ListSongAdapter(Context context, List<Song> songs, OnSongClickListener listener,
                           SongViewModel songViewModel, int userId) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
        this.songViewModel = songViewModel;
        this.userId = userId;
    }

    public void initFavoriteObserver() {
        songViewModel.getFavoriteSongIds().observeForever(ids -> {
            if (ids != null) {
                favoriteSongIds.clear();
                favoriteSongIds.addAll(ids);
                notifyDataSetChanged(); // cập nhật lại giao diện trái tim
            }
        });
    }


    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
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
        int songId = song.getSongId();

        holder.tvTitle.setText(song.getName());
        holder.tvArtist.setText(song.getArtist());
        Glide.with(context).load(song.getImage()).into(holder.imgSong);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });

        if (favoriteSongIds.contains(songId)) {
            holder.imgFavorite.setImageResource(R.drawable.ic_favorite_full);
        } else {
            holder.imgFavorite.setImageResource(R.drawable.ic_favorite);
        }

        holder.imgFavorite.setOnClickListener(v -> {
            if (favoriteSongIds.contains(songId)) {
                songViewModel.removeFromFavorite(songId, userId);
            } else {
                songViewModel.addToFavorite(songId, userId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs != null ? songs.size() : 0;
    }

    public static class ListSongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong, imgFavorite;
        TextView tvTitle, tvArtist;

        public ListSongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imv_img);
            tvTitle = itemView.findViewById(R.id.tv_name);
            tvArtist = itemView.findViewById(R.id.tv_singer);
            imgFavorite = itemView.findViewById(R.id.ivFavorite);
        }
    }
}
