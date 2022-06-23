package com.example.samsungapp.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.samsungapp.R;
import com.example.samsungapp.viewmodel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;


public class SignUpFragment extends Fragment {

    private AuthViewModel viewModel;
    private NavController navController;
    private EditText editEmail , editPass;
    private TextView signInText;
    private Button signUpBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Идентификация всех элементов на экране
        navController = Navigation.findNavController(view);
        editEmail = view.findViewById(R.id.vvedi_email);
        editPass = view.findViewById(R.id.vvedi_parol);
        signInText = view.findViewById(R.id.textReg);
        signUpBtn = view.findViewById(R.id.registration);

        //Нажатие на текст
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_signUpFragment_to_signInFragment);
            }
        });

        //Нажатие на кнопку регистрации
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String pass = editPass.getText().toString();
                if (!email.isEmpty() && !pass.isEmpty()){
                    viewModel.signUp(email , pass);
                    Toast.makeText(getContext(), "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                    viewModel.getFirebaseUserMutableLiveData().observe(getViewLifecycleOwner(), new Observer<FirebaseUser>() {
                        @Override
                        public void onChanged(FirebaseUser firebaseUser) {
                            if (firebaseUser !=null){
                                navController.navigate(R.id.action_signUpFragment_to_signInFragment);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this , ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())).get(AuthViewModel.class);

    }

}