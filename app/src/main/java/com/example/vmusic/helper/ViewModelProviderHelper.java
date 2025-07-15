package com.example.vmusic.helper;

import com.example.vmusic.viewmodel.PlayerViewModel;

public class ViewModelProviderHelper {
    private static PlayerViewModel playerViewModel;

    public static void init(PlayerViewModel viewModel) {
        playerViewModel = viewModel;
    }

    public static PlayerViewModel getPlayerViewModel() {
        return playerViewModel;
    }
}
