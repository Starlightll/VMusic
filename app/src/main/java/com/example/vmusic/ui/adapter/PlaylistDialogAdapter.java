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
        // Nếu là mục tạo playlist mới thì chỉ hiển thị icon ic_add và tên 'Tạo playlist mới'
        if ("➕ Tạo playlist mới".equals(playlist.getName()) || "Tạo playlist mới".equals(playlist.getName())) {
            holder.imgPlaylist.setImageResource(R.drawable.ic_add);
            holder.tvPlaylistName.setText("Tạo playlist mới");
            holder.tvPlaylistName.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.spotify_green));
            holder.tvPlaylistName.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            holder.imgPlaylist.setImageResource(R.drawable.ic_playlist_add);
            holder.tvPlaylistName.setText(playlist.getName());
            holder.tvPlaylistName.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
            holder.tvPlaylistName.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
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