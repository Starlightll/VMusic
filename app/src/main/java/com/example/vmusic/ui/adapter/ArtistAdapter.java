package com.example.vmusic.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.Interface.OnArtistClickListener;
import com.example.vmusic.R;
import com.example.vmusic.entity.Artist;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private final List<Artist> artists;
    private final Context context;
    private final OnArtistClickListener onArtistClickListener;
    private NavController navController;
    public ArtistAdapter(Context context, List<Artist> artists, OnArtistClickListener listener) {
        this.artists = artists;
        this.context = context;
        this.onArtistClickListener = listener;
        this.navController = navController;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.tvArtistName.setText(artist.getName());

        Glide.with(context)
                .load(artist.getImage())
                .placeholder(R.drawable.circle_background)
                .into(holder.imgArtist);

        holder.itemView.setOnClickListener(v -> {
            if (onArtistClickListener != null) {
                onArtistClickListener.onArtistClick(artist);
            }

        });

    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgArtist;
        TextView tvArtistName;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArtist = itemView.findViewById(R.id.imgArtist);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
        }
    }
    public void setArtists(List<Artist> artists) {
        this.artists.clear();
        this.artists.addAll(artists);
        notifyDataSetChanged();
    }

}
