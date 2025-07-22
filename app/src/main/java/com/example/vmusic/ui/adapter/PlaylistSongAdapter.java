package com.example.vmusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;
import com.example.vmusic.models.SongWithArtists;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.PlaylistSongViewHolder> {

    private List<SongWithArtists> songs = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onSongClick(SongWithArtists song);
        void onUnlikeClick(SongWithArtists song);
    }

    public PlaylistSongAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSongs(List<SongWithArtists> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_playlist, parent, false);
        return new PlaylistSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSongViewHolder holder, int position) {
        holder.bind(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class PlaylistSongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView tvTitle, tvArtist;
        ImageView btnMore;

        public PlaylistSongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.songAlbumArt);
            tvTitle = itemView.findViewById(R.id.songTitle);
            tvArtist = itemView.findViewById(R.id.songArtist);
            btnMore = itemView.findViewById(R.id.btnMore);
        }

        void bind(SongWithArtists song) {
            tvTitle.setText(song.song.getName());
            tvArtist.setText(song.artists.get(0).getName());

            if (song.song.getImage() != null) {
                Glide.with(itemView.getContext())
                        .load(song.song.getImage())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imgThumbnail);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onSongClick(song);
            });

            btnMore.setOnClickListener(v -> {
                if (listener != null) listener.onUnlikeClick(song);
            });
        }
    }
}

