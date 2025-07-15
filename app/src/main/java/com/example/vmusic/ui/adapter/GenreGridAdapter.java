package com.example.vmusic.ui.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.entity.Genre;

import java.util.List;

public class GenreGridAdapter extends RecyclerView.Adapter<GenreGridAdapter.GenreViewHolder> {

    private Context context;
    private List<Genre> genreList;

    private final String[] colorHexList = {
            "#FF6F61", "#6A5ACD", "#32CD32", "#00CED1", "#FF69B4", "#FFA500", "#20B2AA", "#9370DB", "#00BFFF"
    };

    public GenreGridAdapter(Context context, List<Genre> genreList) {
        this.context = context;
        this.genreList = genreList;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre_grid, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genreList.get(position);
        holder.tvGenre.setText(genre.name);

        // Đặt màu nền theo vòng lặp
        String color = colorHexList[position % colorHexList.length];
        holder.cardView.setCardBackgroundColor(Color.parseColor(color));
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public void setGenres(List<Genre> genres) {
        this.genreList = genres;
        notifyDataSetChanged();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView tvGenre;
        CardView cardView;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGenre = itemView.findViewById(R.id.tv_genre_name);
            cardView = itemView.findViewById(R.id.card_genre);
        }
    }
}