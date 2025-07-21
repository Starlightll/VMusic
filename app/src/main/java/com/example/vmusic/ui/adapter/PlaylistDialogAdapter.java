package com.example.vmusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.entity.Playlist;

import java.util.List;

public class PlaylistDialogAdapter extends RecyclerView.Adapter<PlaylistDialogAdapter.PlaylistViewHolder> {

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }

    private List<Playlist> playlists;
    private OnPlaylistClickListener listener;

    public PlaylistDialogAdapter(List<Playlist> playlists, OnPlaylistClickListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.tvPlaylistName.setText(playlist.getName());
        // Nếu có ảnh playlist thì load vào imgPlaylist, ví dụ dùng Glide
        holder.itemView.setOnClickListener(v -> listener.onPlaylistClick(playlist));
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPlaylist;
        TextView tvPlaylistName;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPlaylist = itemView.findViewById(R.id.imgPlaylist);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
        }
    }
}