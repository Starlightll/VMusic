package com.example.vmusic.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.models.SongWithArtists;
import com.example.vmusic.viewmodel.PlayerViewModel;
import com.example.vmusic.viewmodel.PlaylistViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchSongAdapter extends RecyclerView.Adapter<SearchSongAdapter.SearchSongViewHolder> {

    private List<SongWithArtists> songs = new ArrayList<>();
    private Set<Integer> songsInCurrentPlaylist = new HashSet<>();
    private OnItemActionListener onItemActionListener;
    private PlaylistViewModel playlistViewModel;

    public interface OnItemActionListener {
        void onAddClick(SongWithArtists song);
        void onRemoveClick(SongWithArtists song); // Đối với trường hợp đã có trong playlist, nhưng muốn xóa
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.onItemActionListener  = listener;
    }

    public void setSongs(List<SongWithArtists> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged();
    }

    public List<SongWithArtists> getSongs() {
        return songs;
    }

    public void setSongsInCurrentPlaylist(List<SongWithArtists> currentPlaylistSongs) {
        songsInCurrentPlaylist.clear();
        if (currentPlaylistSongs != null) {
            for (SongWithArtists song : currentPlaylistSongs) {
                songsInCurrentPlaylist.add(song.song.getSongId());
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_search, parent, false);
        return new SearchSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchSongViewHolder holder, int position) {
        SongWithArtists song = songs.get(position);
        holder.songTitle.setText(song.song.getName());
        holder.artistName.setText(song.artists.get(0).getName());

        boolean isInPlaylist = songsInCurrentPlaylist.contains(song.song.getSongId());

        if (isInPlaylist) {
            holder.actionIcon.setImageResource(R.drawable.circle_check_solid);
            holder.actionIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.primary, null));
            holder.actionIcon.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), "Bài hát đã có trong playlist", Toast.LENGTH_SHORT).show();
                if (onItemActionListener != null) {
                    onItemActionListener.onRemoveClick(song);
                }
            });
        }else{
            holder.actionIcon.setImageResource(R.drawable.circle_plus_solid);
            holder.actionIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.black, null));
            holder.actionIcon.setOnClickListener(v -> {
                if (onItemActionListener != null) {
                    onItemActionListener.onAddClick(song);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SearchSongViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        TextView artistName;
        CheckBox checkBox;
        ImageView actionIcon;

        public SearchSongViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.itemSongSearchTitle);
            artistName = itemView.findViewById(R.id.itemSongSearchArtist);
//            checkBox = itemView.findViewById(R.id.itemSongSearchCheckBox);
            actionIcon = itemView.findViewById(R.id.itemSongActionIcon);
        }
    }
}