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
import com.example.vmusic.R;
import com.example.vmusic.entity.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdminSongAdapter extends RecyclerView.Adapter<AdminSongAdapter.SongViewHolder> {

    private List<Song> songList = new ArrayList<>();
    private final Context context;
    private final OnSongActionClickListener listener;

    // Interface để gửi sự kiện click ra Fragment
    public interface OnSongActionClickListener {
        void onEditClick(Song song);
        void onDeleteClick(Song song);
    }

    public AdminSongAdapter(Context context, OnSongActionClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songList.get(position);

        holder.textSongTitle.setText(currentSong.getName());
        holder.textArtistName.setText(currentSong.getArtist());

        String localImagePath = currentSong.getImage();
        File imageFile = new File(localImagePath);

        if (imageFile.exists()) {
            Glide.with(context)
                    .load(imageFile)
                    .placeholder(R.drawable.ic_music_placeholder)
                    .error(R.drawable.ic_image_error)
                    .into(holder.imageCoverArt);
        } else {
            // Nếu file không tồn tại vì lý do nào đó, hiển thị ảnh lỗi
            holder.imageCoverArt.setImageResource(R.drawable.ic_image_error);
        }

        // Bắt sự kiện click
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(currentSong));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(currentSong));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    // Phương thức để cập nhật dữ liệu cho adapter
    public void setSongs(List<Song> songs) {
        this.songList = songs;
        notifyDataSetChanged();
    }

    // ViewHolder class
    static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCoverArt;
        TextView textSongTitle, textArtistName;
        ImageButton btnEdit, btnDelete;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCoverArt = itemView.findViewById(R.id.image_cover_art_item);
            textSongTitle = itemView.findViewById(R.id.text_song_title_item);
            textArtistName = itemView.findViewById(R.id.text_artist_name_item);
            btnEdit = itemView.findViewById(R.id.btn_edit_song);
            btnDelete = itemView.findViewById(R.id.btn_delete_song);
        }
    }
}
