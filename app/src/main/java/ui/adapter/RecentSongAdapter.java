package ui.adapter;

import android.content.Context;
import android.util.Log;
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

public class RecentSongAdapter extends RecyclerView.Adapter<RecentSongAdapter.SongViewHolder> {
    private Context context;
    private List<Song> songList;
    private OnSongClickListener listener;
    public RecentSongAdapter(Context context, List<Song> songList ,OnSongClickListener listener) {
        this.context = context;
        this.songList = songList;
        this.listener = listener;
    }
    public static class SongViewHolder extends RecyclerView.ViewHolder{
         ImageView imageRecent;
         TextView tvRecentNameSong , tvRecentArtist;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imageRecent = itemView.findViewById(R.id.imgRecent);
            tvRecentNameSong = itemView.findViewById(R.id.tvRecentTitle);
            tvRecentArtist = itemView.findViewById(R.id.tvRecentArtist);
        }
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_recent, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.tvRecentNameSong.setText(song.getName());
        holder.tvRecentArtist.setText(song.getArtist());
        Glide.with(context)
                .load(song.getImage())
                .into(holder.imageRecent);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });

    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }
    public void setSongs(List<Song> songs) {
        this.songList = songs;
        notifyDataSetChanged();
    }


}
