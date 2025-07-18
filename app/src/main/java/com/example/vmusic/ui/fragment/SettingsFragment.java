package com.example.vmusic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vmusic.R;
import com.example.vmusic.databinding.FragmentSettingsBinding;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.ui.adapter.SettingsAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    RecyclerView recyclerView;
    private FragmentSettingsBinding binding;
    List<String> settingsList = Arrays.asList(
            "Tài khoản", "Ngôn ngữ",
            "Theme", "Tải xuống",
            "Thông báo", "Quyền riêng tư",
            "Hỗ trợ", "Giới thiệu", "Chính sách dịch vụ" , "Chính sách dịch vụ" , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
            , "Chính sách dịch vụ"
    );

    SessionManager sessionManager;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = com.example.vmusic.databinding.FragmentSettingsBinding.inflate(inflater, container, false);
        sessionManager = new SessionManager(requireContext());
        View view = binding.getRoot();
        recyclerView = view.findViewById(R.id.recyclerViewSettings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SettingsAdapter(settingsList, NavHostFragment.findNavController(this)));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Clear the binding reference to avoid memory leaks
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Additional setup if needed
        NavController navController = NavHostFragment.findNavController(this);

        binding.btnBackToLibrary.setOnClickListener(v -> {
            navController.navigateUp();
        });

    }
}