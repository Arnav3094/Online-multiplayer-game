package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class DashboardFragment extends Fragment {
	
	private static final String TAG = "DashboardFragment";
	private NavController mNavController;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public DashboardFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: called");
		
		setHasOptionsMenu(true); // Needed to display the action menu for this fragment
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
	                         ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_dashboard, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mNavController = Navigation.findNavController(view);
		
		// TODO if a user is not logged in, go to LoginFragment
		
		// Show a dialog when the user clicks the "new game" button
		view.findViewById(R.id.fab_new_game).setOnClickListener(v -> {
			
			// A listener for the positive and negative buttons of the dialog
			DialogInterface.OnClickListener listener = (dialog, which) -> {
				String gameType = "No type";
				if (which == DialogInterface.BUTTON_POSITIVE) {
					gameType = getString(R.string.two_player);
				} else if (which == DialogInterface.BUTTON_NEGATIVE) {
					gameType = getString(R.string.one_player);
				}
				Log.d(TAG, "New Game: " + gameType);
				
				// Passing the game type as a parameter to the action
				// extract it in GameFragment in a type safe way
				NavDirections action = DashboardFragmentDirections.actionGame(gameType);
				mNavController.navigate(action);
			};
			
			// create the dialog
			AlertDialog dialog = new AlertDialog.Builder(requireActivity())
					.setTitle(R.string.new_game)
					.setMessage(R.string.new_game_dialog_message)
					.setPositiveButton(R.string.two_player, listener)
					.setNegativeButton(R.string.one_player, listener)
					.setNeutralButton(R.string.cancel, (d, which) -> d.dismiss())
					.create();
			dialog.show();
		});
	}
	
	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_logout, menu);
		// this action menu is handled in MainActivity
	}
}