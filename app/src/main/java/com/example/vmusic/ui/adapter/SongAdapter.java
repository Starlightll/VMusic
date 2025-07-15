package com.example.vmusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import com.example.vmusic.entity.Song;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songs = new ArrayList<>();
    private OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public SongAdapter(OnSongClickListener listener) {
        this.listener = listener;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.name.setText(song.getName());
        holder.artist.setText(song.getArtist());
        Glide.with(holder.itemView.getContext()).load(song.getImage()).into(holder.image);
        holder.itemView.setOnClickListener(v -> listener.onSongClick(song));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView name, artist;
        ImageView image;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.songName);
            artist = itemView.findViewById(R.id.songArtist);
            image = itemView.findViewById(R.id.songImage);
        }
    }
}
