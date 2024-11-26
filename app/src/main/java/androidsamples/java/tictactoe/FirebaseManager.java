package androidsamples.java.tictactoe;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {
	private static FirebaseManager instance;
	private final FirebaseAuth mAuth;
	private final FirebaseFirestore db;

	private final static String TAG = "FirebaseManager";

	private FirebaseManager() {
		Log.d(TAG, "FirebaseManager: created");
		mAuth = FirebaseAuth.getInstance();
		db = FirebaseFirestore.getInstance();
	}

	public static synchronized FirebaseManager getInstance() {
		if (instance == null) {
			instance = new FirebaseManager();
		}
		return instance;
	}

	public void signUp(String email, String password, OnAuthCompleteListener listener) {
		mAuth.createUserWithEmailAndPassword(email, password)
				.addOnSuccessListener(authResult -> {
					FirebaseUser user = authResult.getUser();
					// Initialize user data in Firestore
					if (user != null) {
						String userId = user.getUid();
						DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
						// Save only the email in the Realtime Database
						userRef.child("email").setValue(email)
								.addOnCompleteListener(task -> {
									if (task.isSuccessful()) {
										Log.d(TAG, "User data saved to Realtime Database");
									} else {
										Log.e(TAG, "Failed to save user data", task.getException());
									}
								});
					}
				})
				.addOnFailureListener(listener::onError);
	}

	public void signIn(String email, String password, OnAuthCompleteListener listener) {
		mAuth.signInWithEmailAndPassword(email, password)
				.addOnSuccessListener(authResult -> listener.onSuccess())
				.addOnFailureListener(listener::onError);
	}

	public void signOut() {
		mAuth.signOut();
	}

	public boolean isSignedIn() {
		return mAuth.getCurrentUser() != null;
	}

	public String getCurrentUserId() {
		FirebaseUser user = mAuth.getCurrentUser();
		return user != null ? user.getUid() : null;
	}

	public interface OnAuthCompleteListener {
		void onSuccess();
		void onError(Exception e);
	}

	// User data model for Firestore
	private static class UserData {
		public String email;
		public int wins;
		public int losses;

		public UserData(String email, int wins, int losses) {
			this.email = email;
			this.wins = wins;
			this.losses = losses;
		}
	}
}