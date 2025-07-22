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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.Interface.OnSongClickListener;
import com.example.vmusic.Interface.OnSongMenuClickListener;
import com.example.vmusic.R;
import com.example.vmusic.entity.Song;
import com.example.vmusic.models.LibraryViewModel;
import com.example.vmusic.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

public class SongsByArtistAdapter extends RecyclerView.Adapter<SongsByArtistAdapter.SongViewHolder> {
    private Context context;
    private List<Song> songList;
    private OnSongClickListener listener;
    private int currentSongId = -1;
    private LibraryViewModel libraryViewModel;
    private int userId;
    private OnSongMenuClickListener menuClickListener;

    public SongsByArtistAdapter(Context context, List<Song> songs,
                                OnSongClickListener itemClickListener,
                                LibraryViewModel libraryViewModel, int userId,
                                OnSongMenuClickListener menuClickListener) {
        this.context = context;
        this.songList = songs;
        this.listener = itemClickListener;
        this.libraryViewModel = libraryViewModel;
        this.userId = userId;
        this.menuClickListener = menuClickListener;
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


        if (song.getSongId() == currentSongId) {
            holder.rootView.setBackgroundColor(Color.parseColor("#1A4CAF50"));
        } else {
            holder.rootView.setBackgroundResource(android.R.color.transparent);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });

        // Kiểm tra trạng thái yêu thích và trạng thái playlist, tạo menu động
        libraryViewModel.isSongFavorite(song.getSongId(), userId).observeForever(isFavorite -> {
            // Giả sử bạn có playlistId (truyền vào adapter khi khởi tạo)
            int playlistId = /* truyền playlistId phù hợp */ 0;
            libraryViewModel.isSongInPlaylist(playlistId, song.getSongId()).observeForever(isInPlaylist -> {
                holder.btnMenu.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(context, holder.btnMenu);
                    // Favorite
                    if (isFavorite != null && isFavorite) {
                        popup.getMenu().add("Xóa khỏi yêu thích");
                    } else {
                        popup.getMenu().add("Thêm vào yêu thích");
                    }


                    popup.setOnMenuItemClickListener(item -> {
                        String title = item.getTitle().toString();
                        if (title.equals("Thêm vào yêu thích") || title.equals("Xóa khỏi yêu thích")) {
                            if (menuClickListener != null) {
                                menuClickListener.onAddToFavorite(song, isFavorite != null && isFavorite);
                            }
                            return true;
                        }
                        return false;
                    });
                    popup.show();
                });
            });
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

