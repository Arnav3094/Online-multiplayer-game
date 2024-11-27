package androidsamples.java.tictactoe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private RegisterViewModel viewModel;
    private FirebaseManager firebaseManager;
    
    EditText etEmail, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView txtHaveAccount;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
        firebaseManager = FirebaseManager.getInstance();
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etEmail = view.findViewById(R.id.edit_email);
        etPassword = view.findViewById(R.id.edit_password);
        etConfirmPassword = view.findViewById(R.id.edit_confirm_password);
        btnRegister = view.findViewById(R.id.btn_register);
        txtHaveAccount = view.findViewById(R.id.txt_have_account);

        etEmail.setText(viewModel.getEmail());
        etPassword.setText(viewModel.getPassword());
        etConfirmPassword.setText(viewModel.getConfirmPassword());
        
        // Text Changed Listeners for the EditTexts
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setConfirmPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        txtHaveAccount.setOnClickListener(v -> navigateToLogin());

        btnRegister.setOnClickListener(v -> registerButtonAction());

        return view;
    }
    
    private void registerButtonAction(){
		Log.d(TAG, "registerButtonAction: called");
	    String email = etEmail.getText().toString().trim();
	    String password = etPassword.getText().toString().trim();
	    String confirmPassword = etConfirmPassword.getText().toString().trim();
	    
	    if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
		    SnackbarHelper.showSnackbar(requireView(), "All fields are required", Snackbar.LENGTH_LONG, R.color.design_default_color_error);
		    return;
	    }
	    
	    if (!password.equals(confirmPassword)) {
		    SnackbarHelper.showSnackbar(requireView(), "Passwords do not match", Snackbar.LENGTH_LONG, R.color.design_default_color_error);
		    return;
	    }
		
		// disable buttons
	    Log.d(TAG, "registerButtonAction: disabling button click listener and textview click listener");
		btnRegister.setOnClickListener(null);
		txtHaveAccount.setOnClickListener(null);
		
	    firebaseManager.signUp(email, password, new FirebaseManager.OnAuthCompleteListener() {
		    @Override
		    public void onSuccess() {
			    SnackbarHelper.showSnackbar(requireView(), "Registration successful", Snackbar.LENGTH_LONG);
			    navigateToLogin();
		    }
		    
		    @Override
		    public void onError(Exception e) {
			    if(e instanceof FirebaseAuthWeakPasswordException){
				    Log.w(TAG, "signUp:onError: weak password - " + e.getMessage());
				    SnackbarHelper.showSnackbar(requireView(), "Password is too weak", Snackbar.LENGTH_LONG, R.color.design_default_color_error);
				    // clear password
				    etPassword.clearFocus();
				    etConfirmPassword.clearFocus();
				    
				    // clear view model password
				    viewModel.setPassword("");
				    viewModel.setConfirmPassword("");
				    
				    // set focus to enter password
				    etPassword.requestFocus();
				    
			    }
				else if (e instanceof FirebaseAuthInvalidCredentialsException) {
				    Log.w(TAG, "signUp:onError: invalid email - " + e.getMessage());
				    SnackbarHelper.showSnackbar(requireView(), "Invalid email", Snackbar.LENGTH_LONG, R.color.design_default_color_error);
					// clear everything
				    etEmail.clearFocus();
				    etPassword.clearFocus();
				    etConfirmPassword.clearFocus();
				    
				    // clear view model
				    viewModel.clear();
				    
				    // set focus to enter email
				    etEmail.requestFocus();
			    }
				else if (e instanceof FirebaseAuthUserCollisionException) {
				    Log.w(TAG, "signUp:onError: user already exists - " + e.getMessage());
				    SnackbarHelper.showSnackbar(requireView(), "User already exists", Snackbar.LENGTH_LONG, R.color.design_default_color_error);
				    
				    // clear email and pass
				    etEmail.clearFocus();
				    etPassword.clearFocus();
				    etConfirmPassword.clearFocus();
				    
				    // clear view model email and password
				    viewModel.setEmail("");
				    viewModel.setPassword("");
				    viewModel.setConfirmPassword("");
				    
				    // set focus to enter email
				    etEmail.requestFocus();
				    
			    }
				else {
				    Log.e(TAG, "signUp:onError: failed to register", e);
				    SnackbarHelper.showSnackbar(requireView(), "Failed to register", Snackbar.LENGTH_LONG, R.color.design_default_color_error);
			    }
				
				// enable buttons
			    btnRegister.setOnClickListener(v -> registerButtonAction());
				txtHaveAccount.setOnClickListener(v -> navigateToLogin());
		    }
	    });
	    
    }
    
    private void navigateToLogin() {
        viewModel.clear();
        NavDirections action = RegisterFragmentDirections.actionRegisterToLogin();
        Navigation.findNavController(view).navigate(action);
    }
}
