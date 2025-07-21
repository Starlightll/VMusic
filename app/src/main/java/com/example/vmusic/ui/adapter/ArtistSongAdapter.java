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

public class ArtistSongAdapter extends RecyclerView.Adapter<ArtistSongAdapter.SongViewHolder> {

    private List<Song> songList = new ArrayList<>();
    private final Context context;

    // Constructor nhận vào Context để sử dụng cho Glide
    public ArtistSongAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tái sử dụng layout item của màn hình quản lý bài hát
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songList.get(position);

        // Hiển thị tên bài hát
        holder.textSongTitle.setText(currentSong.getName());
        // Hiển thị tên nghệ sĩ từ trường String cũ
        holder.textArtistName.setText(currentSong.getArtist());

        // Tải ảnh bìa của bài hát từ file local
        File imageFile = new File(currentSong.getImage());
        if (imageFile.exists()) {
            Glide.with(context)
                    .load(imageFile)
                    .placeholder(R.drawable.ic_music_placeholder) // Ảnh chờ
                    .error(R.drawable.ic_image_error)         // Ảnh lỗi
                    .into(holder.imageCoverArt);
        } else {
            holder.imageCoverArt.setImageResource(R.drawable.ic_image_error);
        }

        holder.btnEdit.setVisibility(View.GONE);
        holder.btnDelete.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    // Phương thức để cập nhật dữ liệu cho adapter từ Activity
    public void setSongs(List<Song> songs) {
        this.songList = songs;
        notifyDataSetChanged(); // Báo cho RecyclerView biết dữ liệu đã thay đổi để vẽ lại
    }

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
