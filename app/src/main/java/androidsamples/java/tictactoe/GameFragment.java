package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class GameFragment extends Fragment {
	private static final String TAG = "GameFragment";
	private static final int GRID_SIZE = 9;
	
	private final Button[] mButtons = new Button[GRID_SIZE];
	private NavController mNavController;
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: called");
		setHasOptionsMenu(true); // Needed to display the action menu for this fragment
		
		// Extract the argument passed with the action in a type-safe way
		GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
		Log.d(TAG, "New game type = " + args.getGameType());
		
		// Handle the back press by adding a confirmation dialog
		OnBackPressedCallback callback = new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				Log.d(TAG, "Back pressed");
				
				// TODO show dialog only when the game is still in progress
				AlertDialog dialog = new AlertDialog.Builder(requireActivity())
						.setTitle(R.string.confirm)
						.setMessage(R.string.forfeit_game_dialog_message)
						.setPositiveButton(R.string.yes, (d, which) -> {
							// TODO update loss count
							mNavController.popBackStack();
						})
						.setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
						.create();
				dialog.show();
			}
		};
		requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
	                         ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_game, container, false);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mNavController = Navigation.findNavController(view);
		
		mButtons[0] = view.findViewById(R.id.button0);
		mButtons[1] = view.findViewById(R.id.button1);
		mButtons[2] = view.findViewById(R.id.button2);
		
		mButtons[3] = view.findViewById(R.id.button3);
		mButtons[4] = view.findViewById(R.id.button4);
		mButtons[5] = view.findViewById(R.id.button5);
		
		mButtons[6] = view.findViewById(R.id.button6);
		mButtons[7] = view.findViewById(R.id.button7);
		mButtons[8] = view.findViewById(R.id.button8);
		
		for (int i = 0; i < mButtons.length; i++) {
			int finalI = i;
			mButtons[i].setOnClickListener(v -> {
				Log.d(TAG, "Button " + finalI + " clicked");
				// TODO implement listeners
			});
		}
	}
	
	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_logout, menu);
		// this action menu is handled in MainActivity
	}
}