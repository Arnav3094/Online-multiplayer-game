package androidsamples.java.tictactoe;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class TicTacToeApp extends Application {
	
	private static final String TAG = "TicTacToeApp";
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate: called");
		
		// Initialize Firebase
		FirebaseApp.initializeApp(this);
		Log.d(TAG, "onCreate: Firebase initialized");
		
		// Use Firebase Emulator ONLY FOR TESTING REGISTER
//		if (BuildConfig.USE_FIREBASE_EMULATOR) {
//			Log.d(TAG, "onCreate: USE_FIREBASE_EMULATOR = true");
//			Log.d(TAG, "onCreate: BuildConfig.DEBUG = " + BuildConfig.DEBUG);
//			// Initialize Firebase Emulator
//			FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);  // 10.0.2.2 is the default IP for localhost on Android Emulator
//			System.out.println("Firebase emulator initialized");
//			Log.d(TAG, "onCreate: Firebase emulator initialized");
//			return;
//		}
		
		FirebaseManager.getInstance();
		Log.d(TAG, "onCreate: FirebaseManager initialized");
	}
}
