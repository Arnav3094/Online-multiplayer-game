package androidsamples.java.tictactoe;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity {
	
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "onCreate: called");

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.menu_logout) {
			Log.d(TAG, "Logout menu item clicked");

			FirebaseManager firebaseManager = FirebaseManager.getInstance();
			firebaseManager.signOut(); // Clear Firebase authentication session

			// Navigate to the Login Fragment
			NavDirections action = DashboardFragmentDirections.actionNeedAuth();
			Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_need_auth);
			Log.d(TAG, "User logged out and navigated to login fragment");

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}