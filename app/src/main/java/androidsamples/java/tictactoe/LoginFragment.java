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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.Objects;

public class LoginFragment extends Fragment {

	private final static String TAG = "LoginFragment";
	private LoginViewModel viewModel;
	private FirebaseManager firebaseManager;
	
	EditText etEmail;
	EditText etPassword;
	Button btnLogin;
	TextView txtDontHaveAccount;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: called");
		firebaseManager = FirebaseManager.getInstance();
		if (firebaseManager.isSignedIn()) {
			Log.d(TAG, "onCreate: User already signed in");
			NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
			Log.d(TAG, "onCreate: navigating to dashboard fragment");
			Navigation.findNavController(requireView()).navigate(action);
		}
		Log.d(TAG, "onCreate: User not signed in");
		Log.d(TAG, "onCreate: initializing view model");
		viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
	}

	/**
	 * Check if email exists in the database
	 * If email exists
	 *      if password matches -> navigate to dashboard + show snackbar saying logged in
	 *      else                -> show snackbar saying incorrect email or password
	 * else -> show snackbar saying sign up successful or new account created
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_login, container, false);

		etEmail = view.findViewById(R.id.edit_email);
		etPassword = view.findViewById(R.id.edit_password);
		btnLogin = view.findViewById(R.id.btn_log_in);
		txtDontHaveAccount = view.findViewById(R.id.txt_dont_have_account);

		etEmail.setText(viewModel.getEmail());
		etPassword.setText(viewModel.getPassword());

		etEmail.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				viewModel.setEmail(s.toString().trim());
			}
		});

		etPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				viewModel.setPassword(s.toString().trim());
			}
		});
		
		txtDontHaveAccount.setOnClickListener(v -> navigateToRegister());

		btnLogin.setOnClickListener(v -> loginButtonAction());
		
		return view;
	}
	
	private void navigateToRegister(){
		viewModel.clear();
		NavDirections action = LoginFragmentDirections.actionLoginToRegister();
		Navigation.findNavController(requireView()).navigate(action);
	}
	
	private void navigateToDashboard(){
		viewModel.clear();
		NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
		Navigation.findNavController(requireView()).navigate(action);
	}
	
	private void loginButtonAction(){
		String email = etEmail.getText().toString().trim();
		String password = etPassword.getText().toString().trim();
		
		
		if (email.isEmpty()) {
			SnackbarHelper.showSnackbar(requireView(), "Please enter email");
			return;
		}else{
			if (password.isEmpty()) {
				SnackbarHelper.showSnackbar(requireView(), "Please enter password");
				return;
			}
		}
		
		// Disable the click listener to prevent multiple clicks
		Log.d(TAG, "loginButtonAction: disabling button click listener and textview click listener");
		btnLogin.setOnClickListener(null);
		txtDontHaveAccount.setOnClickListener(null);
		
		firebaseManager.signIn(email, password, new FirebaseManager.OnAuthCompleteListener() {
			@Override
			public void onSuccess() {
				Log.d(TAG, "signIn:onSuccess: log in successful");
				SnackbarHelper.showSnackbar(requireView(), "Logged in");
				navigateToDashboard();
			}
			
			@Override
			public void onError(Exception e) {
				if(e instanceof FirebaseAuthInvalidCredentialsException || (e instanceof FirebaseException && Objects.requireNonNull(e.getMessage()).contains("INVALID_LOGIN_CREDENTIALS"))){
					Log.w(TAG, "signIn:onError: invalidCredential - " + e.getMessage());
					SnackbarHelper.showSnackbar(requireView(), "Invalid email or password", Snackbar.LENGTH_SHORT, R.color.design_default_color_error);
				}else{
					Log.e(TAG, "signIn:onError: ", e);
					SnackbarHelper.showSnackbar(requireView(), "Sign In failed", Snackbar.LENGTH_SHORT, R.color.design_default_color_error);
				}
				
				// Re-enable the click listener
				Log.d(TAG, "signIn:onError: re-enabling button click listener and textview click listener");
				btnLogin.setOnClickListener(v -> loginButtonAction());
				txtDontHaveAccount.setOnClickListener(v -> navigateToRegister());
			}
		});
	}

	// No options menu in login fragment.
}