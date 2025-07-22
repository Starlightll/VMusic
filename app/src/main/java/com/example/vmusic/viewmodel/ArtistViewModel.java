package com.example.vmusic.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vmusic.entity.Artist;
import com.example.vmusic.models.ArtistWithSongs;
import com.example.vmusic.repository.ArtistRepository;

import java.util.List;

public class ArtistViewModel extends AndroidViewModel {
    private ArtistRepository repository;
    private LiveData<List<Artist>> allArtists;
    private MutableLiveData<Boolean> canDeleteArtist = new MutableLiveData<>();
    public ArtistViewModel(@NonNull Application application) {
        super(application);
        repository = new ArtistRepository(application);
        allArtists = repository.getAllArtists();
    }

    public LiveData<List<Artist>> getAllArtists() {
        return allArtists;
    }

    public void insert(Artist artist) {
        repository.insert(artist);
    }

    public void update(Artist artist) {
        repository.update(artist);
    }
    public LiveData<Boolean> getCanDeleteArtist() {
        return canDeleteArtist;
    }
    public void deleteArtist(Artist artist) {
        new Thread(() -> {
            // Bước 1: Kiểm tra xem nghệ sĩ có bài hát nào không
            int songCount = repository.getSongCountForArtist(artist.getArtistId());

            if (songCount == 0) {
                // Bước 2a: Nếu không có bài hát nào, tiến hành xóa
                repository.delete(artist); // Giả sử bạn đã có phương thức delete này
                // Gửi tín hiệu thành công về UI thread
                canDeleteArtist.postValue(true);
            } else {
                // Bước 2b: Nếu có bài hát, không xóa và gửi tín hiệu thất bại
                canDeleteArtist.postValue(false);
            }
        }).start();
    }
    public LiveData<ArtistWithSongs> getArtistWithSongs(int artistId) {
        return repository.getArtistWithSongs(artistId);
    }
}
