package androidsamples.java.tictactoe;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {
	private static FirebaseManager instance;
	private final FirebaseAuth auth;
	private final FirebaseFirestore db;
	
	private final static String TAG = "FirebaseManager";
	
	private FirebaseManager() {
		Log.d(TAG, "FirebaseManager: created");
		auth = FirebaseAuth.getInstance();
		db = FirebaseFirestore.getInstance();
	}
	
	public static synchronized FirebaseManager getInstance() {
		if (instance == null) {
			instance = new FirebaseManager();
		}
		return instance;
	}
	
	public void signUp(String email, String password, OnAuthCompleteListener listener) {
		auth.createUserWithEmailAndPassword(email, password)
				.addOnSuccessListener(authResult -> {
					FirebaseUser user = authResult.getUser();
					// Initialize user data in Firestore
					if (user != null) {
						db.collection("users").document(user.getUid())
								.set(new UserData(user.getEmail(), 0, 0))
								.addOnSuccessListener(aVoid -> listener.onSuccess())
								.addOnFailureListener(listener::onError);
					}
				})
				.addOnFailureListener(listener::onError);
	}
	
	public void signIn(String email, String password, OnAuthCompleteListener listener) {
		auth.signInWithEmailAndPassword(email, password)
				.addOnSuccessListener(authResult -> listener.onSuccess())
				.addOnFailureListener(listener::onError);
	}
	
	public void signOut() {
		auth.signOut();
	}
	
	public boolean isSignedIn() {
		return auth.getCurrentUser() != null;
	}
	
	public String getCurrentUserId() {
		FirebaseUser user = auth.getCurrentUser();
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
