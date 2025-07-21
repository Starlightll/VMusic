package com.example.vmusic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vmusic.R;
import com.example.vmusic.databinding.FragmentMainBinding;
import com.example.vmusic.helper.ViewModelProviderHelper;
import com.example.vmusic.viewmodel.PlayerViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private NavHostFragment homeNavHostFragment;
    private NavHostFragment searchNavHostFragment;
    private NavHostFragment libraryNavHostFragment;
    private Fragment activeFragment;
    private FragmentMainBinding binding;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        PlayerViewModel viewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        ViewModelProviderHelper.init(viewModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = com.example.vmusic.databinding.FragmentMainBinding.inflate(inflater, container, false);
        if (getChildFragmentManager().findFragmentById(R.id.playSongPanel) == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.playSongPanel, new PlaySongPanelFragment())
                    .commit();
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        homeNavHostFragment = createNavHost(R.id.nav_host_home, R.navigation.home_nav_graph);
        searchNavHostFragment = createNavHost(R.id.nav_host_search, R.navigation.search_nav_graph);
        libraryNavHostFragment = createNavHost(R.id.nav_host_library, R.navigation.library_nav_graph);

        // Đặt tab mặc định là Home
        showFragment(homeNavHostFragment);
        activeFragment = homeNavHostFragment;

        BottomNavigationView bottomNav = view.findViewById(R.id.bottomNavigationView);

        View playSongPanel = view.findViewById(R.id.playSongPanel);

        binding.miniPlayerContainer.setOnClickListener(v -> {
            showPlayerPanel();
        });

        playSongPanel.setOnClickListener(v -> {
            hidePlayerPanel();
        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                switchToFragment(homeNavHostFragment);
                return true;
            } else if (id == R.id.nav_search) {
                switchToFragment(searchNavHostFragment);
                return true;
            } else if (id == R.id.nav_library) {
                switchToFragment(libraryNavHostFragment);
                return true;
            }
            return false;
        });



        PlayerViewModel playerViewModel = new ViewModelProvider(requireActivity()).get(PlayerViewModel.class);
        playerViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            View miniContainer = view.findViewById(R.id.miniPlayerContainer);
            if (song != null) {
                if (getChildFragmentManager().findFragmentById(R.id.miniPlayerContainer) == null) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.miniPlayerContainer, new MiniPlayerFragment())
                            .commit();
                }
                miniContainer.setVisibility(View.VISIBLE);
            } else {
                miniContainer.setVisibility(View.GONE);
            }
        });


    }

    private NavHostFragment createNavHost(int containerId, int navGraphId) {
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(containerId);
        if (navHostFragment == null) {
            navHostFragment = NavHostFragment.create(navGraphId);
            getChildFragmentManager().beginTransaction()
                    .hide(navHostFragment)
                    .replace(containerId, navHostFragment)
                    .commitNow();
        }
        return navHostFragment;
    }

    private void switchToFragment(Fragment targetFragment) {
        if (activeFragment != targetFragment) {
            getChildFragmentManager().beginTransaction()
                    .hide(activeFragment)
                    .show(targetFragment)
                    .commit();

            // Ẩn layout hiện tại, hiện layout mới
            getContainerViewForFragment(activeFragment).setVisibility(View.GONE);
            getContainerViewForFragment(targetFragment).setVisibility(View.VISIBLE);

            activeFragment = targetFragment;
        }
    }

    private View getContainerViewForFragment(Fragment fragment) {
        if (fragment == homeNavHostFragment) {
            return binding.getRoot().findViewById(R.id.nav_host_home);
        } else if (fragment == searchNavHostFragment) {
            return binding.getRoot().findViewById(R.id.nav_host_search);
        } else {
            return binding.getRoot().findViewById(R.id.nav_host_library);
        }
    }

    public void showFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .show(fragment)
                .commit();
    }

    private void showPlayerPanel() {
        View playSongPanel = binding.getRoot().findViewById(R.id.playSongPanel);
        playSongPanel.setVisibility(View.VISIBLE);
        playSongPanel.animate()
                .translationY(0)
                .setDuration(300)
                .start();
    }

    public void hidePlayerPanel() {
        View playSongPanel = binding.getRoot().findViewById(R.id.playSongPanel);
        playSongPanel.animate()
                .translationY(playSongPanel.getHeight())
                .setDuration(300)
                .withEndAction(() -> playSongPanel.setVisibility(View.GONE))
                .start();
    }



}