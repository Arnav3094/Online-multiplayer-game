package androidsamples.java.tictactoe;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class TicTacToeApp extends Application {
	
	private static final String TAG = "TicTacToeApp";
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate: called");
		
		// Initialize Firebase
		FirebaseApp.initializeApp(this);
		Log.d(TAG, "onCreate: Firebase initialized");
		FirebaseManager.getInstance();
		Log.d(TAG, "onCreate: FirebaseManager initialized");
	}
}
