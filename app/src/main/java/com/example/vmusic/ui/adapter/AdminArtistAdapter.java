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
import com.example.vmusic.entity.Artist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdminArtistAdapter extends RecyclerView.Adapter<AdminArtistAdapter.ArtistViewHolder> {

    private List<Artist> artistList = new ArrayList<>();
    private final Context context;
    private final OnArtistActionClickListener listener;

    public interface OnArtistActionClickListener {
        void onEditClick(Artist artist);
        void onDeleteClick(Artist artist);
    }

    public AdminArtistAdapter(Context context, OnArtistActionClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_artist_item, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist currentArtist = artistList.get(position);

        holder.textArtistName.setText(currentArtist.getName());
        holder.textArtistListeners.setText(String.format("%,d lượt nghe", currentArtist.getListenCounts()));

        // Tải ảnh từ file local bằng Glide
        File imageFile = new File(currentArtist.getImage());
        if (imageFile.exists()) {
            Glide.with(context)
                    .load(imageFile)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(holder.imageArtist);
        } else {
            holder.imageArtist.setImageResource(R.drawable.ic_person);
        }

        // Bắt sự kiện click
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(currentArtist));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(currentArtist));
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public void setArtists(List<Artist> artists) {
        this.artistList = artists;
        notifyDataSetChanged();
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        ImageView imageArtist;
        TextView textArtistName, textArtistListeners;
        ImageButton btnEdit, btnDelete;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            imageArtist = itemView.findViewById(R.id.image_artist_item);
            textArtistName = itemView.findViewById(R.id.text_artist_name_item);
            textArtistListeners = itemView.findViewById(R.id.text_artist_listeners_item);
            btnEdit = itemView.findViewById(R.id.btn_edit_artist);
            btnDelete = itemView.findViewById(R.id.btn_delete_artist);
        }
    }
}
