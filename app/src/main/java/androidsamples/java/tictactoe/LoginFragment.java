package androidsamples.java.tictactoe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

		EditText etEmail = view.findViewById(R.id.edit_email);
		EditText etPassword = view.findViewById(R.id.edit_password);

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
		
		view.findViewById(R.id.txt_dont_have_account).setOnClickListener(v -> {
			NavDirections action = LoginFragmentDirections.actionLoginToRegister();
			Navigation.findNavController(v).navigate(action);
		});

		view.findViewById(R.id.btn_log_in)
				.setOnClickListener(v -> {
					String email = etEmail.getText().toString().trim();
					String password = etPassword.getText().toString().trim();

					if (email.isEmpty()) {
						SnackbarHelper.showSnackbar(v, "Please enter email");
						return;
					}else{
						if (password.isEmpty()) {
							SnackbarHelper.showSnackbar(v, "Please enter password");
							return;
						}
					}
					firebaseManager.signIn(email, password, new FirebaseManager.OnAuthCompleteListener() {
						@Override
						public void onSuccess() {
							Log.d(TAG, "signIn:onSuccess: log in successful");
							SnackbarHelper.showSnackbar(v, "Logged in");
							viewModel.clear();
							NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
							Navigation.findNavController(v).navigate(action);
						}

						@Override
						public void onError(Exception e) {
							if(e instanceof FirebaseAuthInvalidCredentialsException || (e instanceof FirebaseException && Objects.requireNonNull(e.getMessage()).contains("INVALID_LOGIN_CREDENTIALS"))){
								Log.w(TAG, "signIn:onError: invalidCredential - " + e.getMessage());
								SnackbarHelper.showSnackbar(v, "Invalid email or password", Snackbar.LENGTH_SHORT, R.color.design_default_color_error);
							}else{
								Log.e(TAG, "signIn:onError: ", e);
								SnackbarHelper.showSnackbar(v, "Sign In failed", Snackbar.LENGTH_SHORT, R.color.design_default_color_error);
							}
						}
					});
				});
		return view;
	}

	// No options menu in login fragment.
}