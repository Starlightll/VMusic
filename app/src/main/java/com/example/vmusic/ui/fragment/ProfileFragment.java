package com.example.vmusic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vmusic.R;
import com.example.vmusic.databinding.FragmentProfileBinding;
import com.example.vmusic.databinding.FragmentSettingsBinding;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private FragmentProfileBinding binding;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = com.example.vmusic.databinding.FragmentProfileBinding.inflate(inflater, container, false);
        TextView tvUserName = binding.tvUsername;
        SessionManager sessionManager = new SessionManager(requireContext());
        tvUserName.setText(sessionManager.getUsername() != null ? sessionManager.getUsername() : "Guest");
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        binding.btnBackToSettings.setOnClickListener(v -> {
            navController.navigateUp();
        });


    }

}