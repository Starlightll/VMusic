package com.example.vmusic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vmusic.R;
import com.example.vmusic.helper.SessionManager;
import com.example.vmusic.viewmodel.LoginViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private LoginViewModel loginVM;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        EditText email = view.findViewById(R.id.txtLoginEmail);
        EditText password = view.findViewById(R.id.txtLoginPassword);
        Button loginBtn = view.findViewById(R.id.btnLogin);
        Button gotoRegister = view.findViewById(R.id.btnGoToRegister);
        TextView gotoHome = view.findViewById(R.id.btnContinueWithoutAccount);

        loginVM = new ViewModelProvider(this).get(LoginViewModel.class);

        loginVM.loginResult.observe(getViewLifecycleOwner(), user -> {
            if (user != null) {

                SessionManager session = new SessionManager(requireContext());
                session.saveUser(user);

                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_loginFragment_to_mainFragment);
            } else {
                Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        loginBtn.setOnClickListener(v -> {
            loginVM.login(email.getText().toString(), password.getText().toString());
        });

        gotoRegister.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_loginFragment_to_registerFragment)
        );

        gotoHome.setOnClickListener(v ->
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_loginFragment_to_mainFragment)
        );

        return view;
    }
}