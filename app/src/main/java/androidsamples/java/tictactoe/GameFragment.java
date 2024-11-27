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
import java.util.Random;

public class GameFragment extends Fragment {
	private static final String TAG = "GameFragment";
	private static final int GRID_SIZE = 9;

	private final Button[] mButtons = new Button[GRID_SIZE];
	private NavController mNavController;
	private String mGameId;
	private boolean isSinglePlayer;
	private String currentTurn = "X"; // X or O
	private String mySymbol = "";
	private List<String> gameState;
	private DatabaseReference mGameRef;
	private DatabaseReference mPlayerStatsRef;
	private String userEmail;

	private final String[] positions = {
			"Top-left", "Top-center", "Top-right",
			"Middle-left", "Center", "Middle-right",
			"Bottom-left", "Bottom-center", "Bottom-right"
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: called");
		setHasOptionsMenu(true);

		GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
		String gameType = args.getGameType();
		isSinglePlayer = "One-Player".equals(gameType);

		mGameId = args.getGameId();
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		mGameRef = database.getReference("games").child(mGameId);
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null) {
			userEmail = user.getEmail();
			if (userEmail != null) {
				userEmail = userEmail.replace(".", ","); // Firebase key-safe format
				mPlayerStatsRef = FirebaseDatabase.getInstance().getReference("playerStats").child(userEmail);
			}
		}

		if (Objects.equals(mGameId, "NULL")) {
			Log.d(TAG, "Creating a new game.");
			mySymbol = "X";
			createNewGame();
		} else {
			Log.d(TAG, "Joining existing game with ID: " + mGameId);
			mySymbol = "O";
			joinExistingGame();
		}

		OnBackPressedCallback callback = new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				Log.d(TAG, "Back pressed");
				mGameRef.child("winner").addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot snapshot) {
						String winner = snapshot.getValue(String.class);
						if (Objects.equals(winner, "NULL")) {
							// No winner set yet
							Log.d("GameFragment", "Winner is null (no winner yet)");
							AlertDialog dialog = new AlertDialog.Builder(requireActivity())
									.setTitle(R.string.confirm)
									.setMessage(R.string.forfeit_game_dialog_message)
									.setPositiveButton(R.string.yes, (d, which) -> {
										Log.d(TAG, "User confirmed forfeit. Navigating back.");
// FIX: add loss condition here
										mNavController.popBackStack();
									})
									.setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
									.create();
							dialog.show();

						} else {
							// Winner exists
							Log.d("GameFragment", "Winner is: " + winner);
							mNavController.popBackStack();
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError error) {
						Log.e("GameFragment", "Error fetching winner", error.toException());
					}
				});
			}
		};
		requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_game, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mNavController = Navigation.findNavController(view);
		Log.d(TAG, "IN THE GAME");
		for (int i = 0; i < GRID_SIZE; i++) {
			int finalI = i;
			String buttonId = "button" + i;
			int resId = getResources().getIdentifier(buttonId, "id", requireContext().getPackageName());
			mButtons[i] = view.findViewById(resId);
			mButtons[i].setOnClickListener(v -> handleMove(finalI));
		}
		updateContentDescription();
		if (!isSinglePlayer) {
			listenToGameUpdates();
		}
	}

	private void updateContentDescription(int i){
		if (mButtons[i].getText().equals("")) {
			mButtons[i].setContentDescription(positions[i] + ", empty");
		} else if (mButtons[i].getText().equals("X")) {
			mButtons[i].setContentDescription(positions[i] + ", X");
		} else if (mButtons[i].getText().equals("O")) {
			mButtons[i].setContentDescription(positions[i] + ", O");
		}
		else{
			Log.e(TAG, "Something went wrong. mButtons["+i+"].getText() = " + mButtons[i].getText());
		}
	}

	private void updateContentDescription(){
		for (int i = 0; i < GRID_SIZE; i++) {
			updateContentDescription(i);
		}
	}

	private void createNewGame() {
		mGameId = FirebaseDatabase.getInstance().getReference("games").push().getKey();
		if (mGameId == null) {
			Log.e(TAG, "Failed to generate unique gameId");
			return;
		}

		gameState = new ArrayList<>();
		for (int i = 0; i < GRID_SIZE; i++) {
			gameState.add("");
		}

		// Save game data to Firebase
		mGameRef = FirebaseDatabase.getInstance().getReference("games").child(mGameId);
		mGameRef.setValue(new GameData(isSinglePlayer, currentTurn, gameState,"NULL"))
				.addOnSuccessListener(aVoid -> Log.d(TAG, "New Game Created with ID: " + mGameId))
				.addOnFailureListener(e -> Log.e(TAG, "Failed to create new game", e));
	}

	private void joinExistingGame() {
		mGameRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				GameData data = snapshot.getValue(GameData.class);
				if (data != null) {
					currentTurn = data.currentTurn;
					gameState = data.gameState;
					Log.d(TAG, "Successfully joined game with ID: " + mGameId);
					updateUI();
				} else {
					Log.e(TAG, "Game not found in database with ID: " + mGameId);
				}
			}
			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.e(TAG, "Failed to fetch game data", error.toException());
			}
		});
	}

	private void handleMove(int index) {
		if (!gameState.get(index).isEmpty() || (!isSinglePlayer && !currentTurn.equals(mySymbol))) {
			return;
		}
		gameState.set(index, currentTurn);
		updateUI();
//		FIX : add when other forfeits you get a win
		if (checkWin()) {
			mGameRef.child("winner").setValue(currentTurn);
			updatePlayerStats(currentTurn.equals(mySymbol) ? "win" : "loss");
			showWinDialog(currentTurn);
		} else if (isDraw()) {
			mGameRef.child("winner").setValue("Draw");
//		FIX : add draw condition update on db
			updatePlayerStats("draw");
			showWinDialog("Draw");
		} else {
			switchTurn();
			if (isSinglePlayer && currentTurn.equals("O")) {
				makeComputerMove();
			}
			mGameRef.setValue(new GameData(isSinglePlayer, currentTurn, gameState,"NULL"));
		}
	}
	private void updatePlayerStats(String result) {
		if (mPlayerStatsRef == null) return;

		mPlayerStatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				int wins = snapshot.child("wins").getValue(Integer.class) != null
						? snapshot.child("wins").getValue(Integer.class) : 0;
				int losses = snapshot.child("losses").getValue(Integer.class) != null
						? snapshot.child("losses").getValue(Integer.class) : 0;
				int draws = snapshot.child("draws").getValue(Integer.class) != null
						? snapshot.child("draws").getValue(Integer.class) : 0;
				if ("win".equals(result)) {
					mPlayerStatsRef.child("wins").setValue(wins + 1);
				} else if ("loss".equals(result)) {
					mPlayerStatsRef.child("losses").setValue(losses + 1);
				}
				else if ("draw".equals(result)) {
					mPlayerStatsRef.child("draws").setValue(draws + 1);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.e(TAG, "Failed to update player stats", error.toException());
			}
		});
	}

	private void switchTurn() {
		currentTurn = currentTurn.equals("X") ? "O" : "X";
	}

	private void makeComputerMove() {
		Random random = new Random();
		int move;
		do {
			move = random.nextInt(GRID_SIZE);
		} while (!gameState.get(move).isEmpty());

		gameState.set(move, currentTurn);
		updateUI();

		if (checkWin()) {
			mGameRef.child("winner").setValue(currentTurn);
			showWinDialog(currentTurn);
		} else if (isDraw()) {
			mGameRef.child("winner").setValue("Draw");
			showWinDialog("Draw");
		} else {
			switchTurn();
		}
	}

	private boolean checkWin() {
		int[][] winConditions = {
				{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
				{0, 3, 6}, {1, 4, 7}, {2, 5, 8},
				{0, 4, 8}, {2, 4, 6}
		};

		for (int[] condition : winConditions) {
			String a = gameState.get(condition[0]);
			String b = gameState.get(condition[1]);
			String c = gameState.get(condition[2]);
			if (!a.isEmpty() && a.equals(b) && b.equals(c)) {
				return true;
			}
		}
		return false;
	}

	private boolean isDraw() {
		for (String cell : gameState) {
			if (cell.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private void updateUI() {
		for (int i = 0; i < GRID_SIZE; i++) {
			mButtons[i].setText(gameState.get(i));
		}
	}

	private void listenToGameUpdates() {
		mGameRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				GameData data = snapshot.getValue(GameData.class);
				if (data != null) {
					currentTurn = data.currentTurn;
					gameState = data.gameState;
					updateUI();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.e(TAG, "Failed to listen for game updates", error.toException());
			}
		});
	}

	private void showWinDialog(String winner) {
		String message = "Draw".equals(winner) ? "It's a draw!" : winner + " wins!";
		if (!"Draw".equals(winner) && winner.equals(mySymbol)) {
			updatePlayerStats("win");
		} else if (!"Draw".equals(winner)) {
			updatePlayerStats("loss");
		}
		new AlertDialog.Builder(requireActivity())
				.setTitle("Game Over")
				.setMessage(message)
				.setPositiveButton("OK", (dialog, which) -> mNavController.popBackStack())
				.create()
				.show();
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_logout, menu);
	}

	static class GameData {
		public boolean isSinglePlayer;
		public String currentTurn;
		public List<String> gameState;
		public String winner;
		public GameData() {}

		public GameData(boolean isSinglePlayer, String currentTurn, List<String> gameState,String winner) {
			this.isSinglePlayer = isSinglePlayer;
			this.currentTurn = currentTurn;
			this.gameState = gameState;
			this.winner = winner;
		}
	}
}
