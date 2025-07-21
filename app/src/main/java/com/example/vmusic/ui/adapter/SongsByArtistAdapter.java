package com.example.vmusic.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.Interface.OnSongClickListener;
import com.example.vmusic.Interface.OnSongMenuClickListener;
import com.example.vmusic.R;
import com.example.vmusic.entity.Song;
import com.example.vmusic.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

public class SongsByArtistAdapter extends RecyclerView.Adapter<SongsByArtistAdapter.SongViewHolder> {
    private Context context;
    private List<Song> songList;
    private OnSongClickListener listener;
    private int currentSongId = -1;
    private SongViewModel songViewModel;
    private int userId;


    public SongsByArtistAdapter(Context context, List<Song> songList, OnSongClickListener listener, SongViewModel songViewModel, int userId) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
        this.songViewModel = songViewModel;
        this.userId = userId;

    }

    public void setCurrentSongId(int songId) {
        this.currentSongId = songId;
        notifyDataSetChanged();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSongThumbnail;
        TextView tvSongTitle, tvListenCount;
        ImageButton btnMenu;
        View rootView;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSongThumbnail = itemView.findViewById(R.id.imgSongThumbnail);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvListenCount = itemView.findViewById(R.id.tvListenCount);
            btnMenu = itemView.findViewById(R.id.btnMenu);
            rootView = itemView;
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

        // Highlight bài hát đang phát bằng màu nhẹ, không mất ripple
        if (song.getSongId() == currentSongId) {
            holder.rootView.setBackgroundColor(Color.parseColor("#1A4CAF50")); // Xanh có alpha
        } else {
            holder.rootView.setBackgroundResource(android.R.color.transparent);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });

        // Xử lý popup menu trực tiếp trong Adapter
        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMenu);
            popup.getMenu().add("Thêm vào yêu thích");
            popup.getMenu().add("Thêm vào playlist");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Thêm vào yêu thích")) {
                    songViewModel.addToFavorite(song.getSongId(), userId);
                    Toast.makeText(context, "Đã thêm vào yêu thích!", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getTitle().equals("Thêm vào playlist")) {
                    Toast.makeText(context, "Đã thêm vào playlist!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public void setSongs(List<Song> songs) {
        this.songList = new ArrayList<>(songs);
        notifyDataSetChanged();
    }

    public List<Song> getSongs() {
        return songList;
    }
}

