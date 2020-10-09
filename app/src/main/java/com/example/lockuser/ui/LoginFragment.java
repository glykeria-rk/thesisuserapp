package com.example.lockuser.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lockuser.AuthenticationViewModel;
import com.example.lockuser.MainActivity;
import com.example.lockuser.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static com.example.lockuser.helpers.Constants.PASSWORD_PATTERN;


public class LoginFragment extends Fragment implements TextWatcher {

    private AuthenticationViewModel authenticationViewModel;


    private TextInputEditText textInputEditTextEmailAddress;
    private TextInputEditText textInputEditTextPassword;

    private TextInputLayout textInputLayoutEmailAddress;
    private TextInputLayout textInputLayoutPassword;

    private TextView textViewError;

    private Button buttonSubmit;
    private Button buttonGoToSignUpScreen;

    private NavController navController;



    public LoginFragment() {
        // Required empty public constructor
    }


    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);




        textInputEditTextEmailAddress = view.findViewById(R.id.email);
        textInputEditTextPassword = view.findViewById(R.id.password);

        textInputLayoutEmailAddress = view.findViewById(R.id.text_input_layout_email_address);
        textInputLayoutPassword = view.findViewById(R.id.text_input_layout_password);

        textViewError = view.findViewById(R.id.text_view_error);

        buttonSubmit = view.findViewById(R.id.button_sign_in);
        buttonGoToSignUpScreen = view.findViewById(R.id.button_go_to_sign_up);

        textInputEditTextEmailAddress.addTextChangedListener(this);
        textInputEditTextPassword.addTextChangedListener(this);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(validateEmail() && validatePassword())) return;
                String email = textInputEditTextEmailAddress.getText().toString();
                String password = textInputEditTextPassword.getText().toString();
                authenticationViewModel.signIn(email, password);
            }
        });



        buttonGoToSignUpScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_signUpFragment);
            }
        });

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authenticationViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);

        navController  = Navigation.findNavController(view);
        authenticationViewModel.authenticationState.observe(getViewLifecycleOwner(),
                new Observer<AuthenticationViewModel.AuthenticationState>() {
                    @Override
                    public void onChanged(AuthenticationViewModel.AuthenticationState authenticationState) {
                        switch (authenticationState) {
                            case AUTHENTICATED:
                                navController.navigate(R.id.action_loginFragment_to_QRFragment);
                                Toast.makeText(getContext(), "Successfully signed in", Toast.LENGTH_SHORT).show();
                                break;
                            case INVALID_AUTHENTICATION:
                                textViewError.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                });
    }

    private boolean validateEmail() {
        String emailInput = textInputLayoutEmailAddress.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            textInputLayoutEmailAddress.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputLayoutEmailAddress.setError("Please enter a valid email address");
            return false;
        } else {
            textInputLayoutEmailAddress.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = textInputLayoutPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            textInputLayoutPassword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textInputLayoutPassword.setError("Password too weak: use at least 1 letter, one digit, and 6 characters in total");
            return false;
        } else {
            textInputLayoutPassword.setError(null);
            return true;
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (textInputEditTextEmailAddress.getText().hashCode() == s.hashCode()) {
            validateEmail();
        } else if(textInputEditTextPassword.getText().hashCode() == s.hashCode()) {
            validatePassword();
    }
}
}
