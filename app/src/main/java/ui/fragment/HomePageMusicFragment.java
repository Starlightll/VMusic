package ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vmusic.R;

import java.util.ArrayList;

import ui.adapter.PopularSongAdapter;
import ui.adapter.RecentSongAdapter;
import viewmodel.SongViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageMusicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerRecent , recyclerPopular;
    RecentSongAdapter recentSongAdapter ;
    PopularSongAdapter  popularSongAdapter;
    private SongViewModel songViewModel;

    public HomePageMusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePageMusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePageMusicFragment newInstance(String param1, String param2) {
        HomePageMusicFragment fragment = new HomePageMusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        recyclerRecent = view.findViewById(R.id.recyclerRecent);

        recentSongAdapter = new RecentSongAdapter(requireContext(), new ArrayList<>());
        popularSongAdapter = new PopularSongAdapter(requireContext(), new ArrayList<>());
        recyclerRecent.setAdapter(recentSongAdapter);
        recyclerRecent.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        songViewModel.getAllSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                recentSongAdapter.setSongs(songs);
            }
        });


        recyclerPopular = view.findViewById(R.id.recyclerPopular);

        recyclerPopular.setAdapter(popularSongAdapter);

        recyclerPopular.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        songViewModel.getAllSongs().observe(getViewLifecycleOwner(), songs -> {
            if (songs != null) {
                popularSongAdapter.setSongs(songs);
            }
        });

    }
}