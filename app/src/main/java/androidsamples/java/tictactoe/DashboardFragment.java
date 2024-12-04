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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardFragment extends Fragment {
	
	FirebaseManager firebaseManager;

	private static final String TAG = "DashboardFragment";
	private NavController mNavController;
	private OpenGamesAdapter mAdapter;
	private DatabaseReference mPlayerStatsRef;
	private TextView txtStats, txtUser;

	public DashboardFragment() {
		// Empty constructor required
		Log.d(TAG, "DashboardFragment: Constructor called");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: called");
		setHasOptionsMenu(true); // To display the action menu
		firebaseManager = FirebaseManager.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView: inflating layout");
		return inflater.inflate(R.layout.fragment_dashboard, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "onViewCreated: called");

		mNavController = Navigation.findNavController(view);

		// Check if the user is not logged in
		if (!firebaseManager.isSignedIn()) {
			Log.d(TAG, "onViewCreated: User not signed in");
			NavDirections action = DashboardFragmentDirections.actionNeedAuth();
			mNavController.navigate(action);
			return;
		}
		// Display the user's email
		txtUser = view.findViewById(R.id.txt_user);
		String currUser = getResources().getString(R.string.signed_in) + " " + firebaseManager.getCurrentUserEmail();
		txtUser.setText(currUser);

		// Initialize RecyclerView for open games
		setupRecyclerView(view);

		// Load open games from Firebase
		loadOpenGames();

		// Handle new game button click
		view.findViewById(R.id.fab_new_game).setOnClickListener(v -> {
			Log.d(TAG, "Floating Action Button clicked: Showing new game dialog");
			showNewGameDialog();
		});

		// Initialize the TextView for displaying scores
		txtStats = view.findViewById(R.id.txt_stats);

		// Fetch and display player stats (wins, losses, draws)
		fetchPlayerStats();
	}

	private void setupRecyclerView(View view) {
		Log.d(TAG, "setupRecyclerView: Initializing RecyclerView");
		RecyclerView recyclerView = view.findViewById(R.id.list);
		mAdapter = new OpenGamesAdapter(new ArrayList<>()); // Temporary empty list
		recyclerView.setAdapter(mAdapter);
	}

	private void loadOpenGames() {
		Log.d(TAG, "loadOpenGames: Fetching open games from Firebase");
		DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference("games");

		gamesRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				List<String> openGameIds = new ArrayList<>();
				for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
					GameFragment.GameData game = gameSnapshot.getValue(GameFragment.GameData.class);
					boolean gameOfTheSamePerson = ( "NULL".equals(game.getWinner()) && (firebaseManager.getCurrentUserEmail().equals(game.player1) || firebaseManager.getCurrentUserEmail().equals(game.player2)));
					boolean gameHasOneSpotLeft = (game != null && "NULL".equals(game.getWinner()) && !game.isSinglePlayer && "NULL".equals(game.player2) );
					if ( gameHasOneSpotLeft || gameOfTheSamePerson  ) {
						String gameId = gameSnapshot.getKey();  // Extract game ID (key)
						if (gameId != null) {
							openGameIds.add(gameId);
						}
					}
				}

				Log.d(TAG, "loadOpenGames: Retrieved " + openGameIds.size() + " open game IDs");
				mAdapter.updateData(openGameIds);  // Update with game IDs
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.e(TAG, "loadOpenGames: Database error", error.toException());
			}
		});
	}

	private void showNewGameDialog() {
		Log.d(TAG, "showNewGameDialog: Creating new game dialog");

		DialogInterface.OnClickListener listener = (dialog, which) -> {
			String gameType = "Unknown";
			if (which == DialogInterface.BUTTON_POSITIVE) {
				gameType = getString(R.string.two_player);
			} else if (which == DialogInterface.BUTTON_NEGATIVE) {
				gameType = getString(R.string.one_player);
			}
			Log.d(TAG, "New Game selected: " + gameType);
			boolean isSinglePlayer = (gameType.equals(getString(R.string.one_player)));
			String currentTurn ="X";
			String mGameId = createNewGame(isSinglePlayer, currentTurn);
			NavDirections action = DashboardFragmentDirections.actionGame(gameType, mGameId);
			mNavController.navigate(action);
		};


		AlertDialog dialog = new AlertDialog.Builder(requireActivity())
				.setTitle(R.string.new_game)
				.setMessage(R.string.new_game_dialog_message)
				.setPositiveButton(R.string.two_player, listener)
				.setNegativeButton(R.string.one_player, listener)
				.setNeutralButton(R.string.cancel, (d, which) -> {
					Log.d(TAG, "showNewGameDialog: Dialog canceled");
					d.dismiss();
				})
				.create();
		dialog.show();
	}
	private String createNewGame(boolean isSinglePlayer, String currentTurn) {
		String mGameId = FirebaseDatabase.getInstance().getReference("games").push().getKey();
		List<String> gameState;
		final int GRID_SIZE = 9;
		DatabaseReference mGameRef;

		if (mGameId == null) {
			Log.e(TAG, "Failed to generate unique gameId");
			return null;
		}

		gameState = new ArrayList<>();
		for (int i = 0; i < GRID_SIZE; i++) {
			gameState.add("");
		}
		String secondPlayer;
		if(isSinglePlayer){
			secondPlayer = getResources().getString(R.string.single_player_2);
		}
		else{
			secondPlayer = "NULL";
		}

		// Save game data to Firebase
		mGameRef = FirebaseDatabase.getInstance().getReference("games").child(mGameId);

		mGameRef.setValue(new GameFragment.GameData(isSinglePlayer, currentTurn, gameState,"NULL",firebaseManager.getCurrentUserEmail(),secondPlayer))
				.addOnSuccessListener(aVoid -> Log.d(TAG, "New Game Created in Dashboard with ID: " + mGameId))
				.addOnFailureListener(e -> Log.e(TAG, "Failed to create new game", e));
//		mGameRef.child("player1").setValue(firebaseManager.getCurrentUserEmail());


		mGameRef.child("player1").addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				String player1Email = snapshot.getValue(String.class);
				Log.d(TAG,"In dashboard: "+player1Email);
			}
			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.e("GameFragment", "Error fetching winner", error.toException());
			}
		});
		return mGameId;
	}

	private void fetchPlayerStats() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null) {
			String userEmail = user.getEmail();
			if (userEmail != null) {
				userEmail = userEmail.replace(".", ","); // Firebase key-safe format
				mPlayerStatsRef = FirebaseDatabase.getInstance().getReference("playerStats").child(userEmail);
			}
		}
		else{
			Log.e(TAG, "fetchPlayerStats: User is null");
		}

		if (mPlayerStatsRef != null) {
			mPlayerStatsRef.addValueEventListener(statsListener);
		}
		else{
			Log.e(TAG, "fetchPlayerStats: Player stats reference is null");
		}
	}
	
	private final ValueEventListener statsListener = new ValueEventListener() {
		@Override
		public void onDataChange(@NonNull DataSnapshot ds) {
			int wins = Objects.requireNonNullElse(ds.child("wins").getValue(Integer.class), 0);
			int losses = Objects.requireNonNullElse(ds.child("losses").getValue(Integer.class), 0);
			int draws = Objects.requireNonNullElse(ds.child("draws").getValue(Integer.class), 0);
			
			String statsText = wins + " Win" + (wins != 1 ? "s" : "") + " | " +
					losses + " Loss" + (losses != 1 ? "es" : "") + " | " +
					draws + " Draw" + (draws != 1 ? "s" : "");
			txtStats.setText(statsText);
		}
		@Override
		public void onCancelled(@NonNull DatabaseError error) {
			Log.e(TAG, "fetchPlayerStats: Database error", error.toException());
		}
	};
	
	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "onDetach: Removing ValueEventListener");
		mPlayerStatsRef.removeEventListener(statsListener);
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		Log.d(TAG, "onCreateOptionsMenu: Inflating menu");
		inflater.inflate(R.menu.menu_logout, menu);
	}
}
