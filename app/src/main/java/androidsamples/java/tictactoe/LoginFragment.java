package androidsamples.java.tictactoe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class LoginFragment extends Fragment {
	
	private final static String TAG = "LoginFragment";
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: called");
		FirebaseManager firebaseManager = FirebaseManager.getInstance();
		if (firebaseManager.isSignedIn()) {
			Log.d(TAG, "onCreate: User already signed in");
			NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
			Log.d(TAG, "onCreate: navigating to dashboard fragment");
			Navigation.findNavController(requireView()).navigate(action);
		}
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
		view.findViewById(R.id.btn_log_in)
				.setOnClickListener(v -> {
					// TODO implement sign in logic
					NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
					Navigation.findNavController(v).navigate(action);
				});
		
		return view;
	}
	
	// No options menu in login fragment.
}