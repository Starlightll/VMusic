package ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vmusic.R;

import java.util.List;

import Interface.OnSongClickListener;
import entity.Song;

public class PopularSongAdapter extends RecyclerView.Adapter<PopularSongAdapter.PopularSongViewHolder>{
    private Context context;
    private List<Song> songList;

    private OnSongClickListener listener;

    public PopularSongAdapter(Context context, List<Song> songList , OnSongClickListener listener) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
    }
    public static class PopularSongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvTitle, tvArtist;


        public PopularSongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgPopular);
            tvTitle = itemView.findViewById(R.id.tvPopularTitle);
            tvArtist = itemView.findViewById(R.id.tvPopularArtist);
        }
    }
    @NonNull
    @Override
    public PopularSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_popular, parent, false);
        return new PopularSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularSongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.tvTitle.setText(song.getName());
        holder.tvArtist.setText(song.getArtist());

        Glide.with(context)
                .load(song.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgCover);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList == null ? 0 : songList.size();
    }
    public void setSongs(List<Song> songs) {
        this.songList = songs;
        notifyDataSetChanged();
    }

}
