package androidsamples.java.tictactoe;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

/**
 * Helper class to show Snackbar messages.
 */
public class SnackbarHelper {
	
	/**
	 * Show a Snackbar message.
	 * <p>
	 * Duration - Short
	 * @param view    The view to show the Snackbar on.
	 * @param message The message to show.
	 */
	public static void showSnackbar(View view, String message) {
		Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
	}
	
	/**
	 * Show a Snackbar message.
	 * @param view     The view to show the Snackbar on.
	 * @param message  The message to show.
	 * @param duration The duration to show the Snackbar.
	 */
	public static void showSnackbar(View view, String message, int duration) {
		Snackbar.make(view, message, duration).show();
	}
	
	/**
	 * Show a Snackbar message.
	 * @param view     The view to show the Snackbar on.
	 * @param message  The message to show.
	 * @param duration The duration to show the Snackbar.
	 * @param colorResource The color resource to set the Snackbar background color.
	 */
	public static void showSnackbar(View view, String message, int duration, int colorResource) {
		Snackbar snackbar = Snackbar.make(view, message, duration);
		snackbar.getView().setBackgroundColor(view.getResources().getColor(colorResource));
		snackbar.show();
	}
}
