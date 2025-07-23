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
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.models.PlaylistWithSongs;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSelectionAdapter extends RecyclerView.Adapter<PlaylistSelectionAdapter.PlaylistViewHolder> {

    private List<PlaylistWithSongs> playlists = new ArrayList<>();
    private String username = "StarLight"; // Giả sử bạn có tên người dùng
    private OnPlaylistSelectedListener listener;

    public interface OnPlaylistSelectedListener {
        void onPlaylistSelected(PlaylistWithSongs playlist);
    }

    public PlaylistSelectionAdapter(OnPlaylistSelectedListener listener) {
        this.listener = listener;
    }

    public void setPlaylists(List<PlaylistWithSongs> newPlaylists) {
        this.playlists = newPlaylists;
        notifyDataSetChanged();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_selection, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistWithSongs playlist = playlists.get(position);
        holder.playlistName.setText(playlist.playlist.getName());
        String user = username == null ? "Guest" : username;
        holder.playlistInfo.setText(String.format("của %s • %d bản nhạc", user, playlist.songs.size()));


        // Tải ảnh bìa playlist (nếu có URL trong Playlist object)
        // Glide.with(holder.itemView.getContext())
        //     .load(playlist.getCoverImageUrl()) // Giả sử Playlist có getCoverImageUrl()
        //     .placeholder(R.drawable.default_playlist_cover)
        //     .into(holder.playlistCoverImage);
        // Tạm thời dùng ảnh tĩnh hoặc placeholder
        holder.playlistCoverImage.setImageResource(R.drawable.bluelight_gradient);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlaylistSelected(playlist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView playlistCoverImage;
        TextView playlistName;
        TextView playlistInfo;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistCoverImage = itemView.findViewById(R.id.playlist_cover_image);
            playlistName = itemView.findViewById(R.id.playlist_name);
            playlistInfo = itemView.findViewById(R.id.playlist_info);
        }
    }
}