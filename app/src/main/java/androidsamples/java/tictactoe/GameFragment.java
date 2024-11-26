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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameFragment extends Fragment {
	private static final String TAG = "GameFragment";
	private static final int GRID_SIZE = 9;

	private final Button[] mButtons = new Button[GRID_SIZE];
	private NavController mNavController;
	private String mGameId;
	private boolean isSinglePlayer;
	private char[] board; // 'X', 'O', or empty (' ')
	private char currentPlayer;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: called");
		setHasOptionsMenu(true);

		GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
		String gameType = args.getGameType();
		Log.d(TAG, "Game type: " + gameType);

		isSinglePlayer = (args.getGameType().equals("One-Player"));
		if (isSinglePlayer) {
			Log.d(TAG, "Starting a single-player game.");
			initializeSinglePlayerGame();
		} else {
			Log.d(TAG, "Starting a two-player game.");
			mGameId = args.getGameId();
			if (mGameId == null) {
				Log.d(TAG, "Game ID is null. Creating a new game for two players.");
				createNewGame();
			} else {
				Log.d(TAG, "Game ID is " + mGameId + ". Joining an existing game.");
				joinExistingGame(mGameId);
			}
		}

		OnBackPressedCallback callback = new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				Log.d(TAG, "Back pressed");
				AlertDialog dialog = new AlertDialog.Builder(requireActivity())
						.setTitle(R.string.confirm)
						.setMessage(R.string.forfeit_game_dialog_message)
						.setPositiveButton(R.string.yes, (d, which) -> {
							Log.d(TAG, "User confirmed forfeit. Navigating back.");
							mNavController.popBackStack();
						})
						.setNegativeButton(R.string.cancel, (d, which) -> {
							Log.d(TAG, "User canceled forfeit.");
							d.dismiss();
						})
						.create();
				dialog.show();
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

		board = new char[GRID_SIZE];
		for (int i = 0; i < GRID_SIZE; i++) {
			board[i] = ' ';
			int finalI = i;
			String buttonId = "button" + i;
			int resId = getResources().getIdentifier(buttonId, "id", requireContext().getPackageName());
			mButtons[i] = view.findViewById(resId);
			mButtons[i].setOnClickListener(v -> {
				Log.d(TAG, "Button " + finalI + " clicked.");
				if (board[finalI] == ' ') {
					handlePlayerMove(finalI);
				} else {
					Log.d(TAG, "Cell " + finalI + " is already occupied.");
				}
			});
		}
	}

	private void initializeSinglePlayerGame() {
		currentPlayer = 'X'; // Player starts as 'X'
		Log.d(TAG, "Single-player game initialized. Player is 'X', computer is 'O'.");
	}

	private void handlePlayerMove(int cellIndex) {
		board[cellIndex] = currentPlayer;
		mButtons[cellIndex].setText(String.valueOf(currentPlayer));
		Log.d(TAG, "Player " + currentPlayer + " marked cell " + cellIndex);

		if (checkWinCondition()) {
			Log.d(TAG, "Player " + currentPlayer + " won the game!");
			showWinDialog(currentPlayer);
			return;
		}

		switchPlayer();
		if (isSinglePlayer && currentPlayer == 'O') {
			handleComputerMove();
		}

	}

	private void handleComputerMove() {
		Log.d(TAG, "Computer's turn.");
		List<Integer> availableCells = new ArrayList<>();
		for (int i = 0; i < GRID_SIZE; i++) {
			if (board[i] == ' ') {
				availableCells.add(i);
			}
		}

		if (!availableCells.isEmpty()) {
			int randomCell = availableCells.get(new Random().nextInt(availableCells.size()));
			board[randomCell] = currentPlayer;
			mButtons[randomCell].setText(String.valueOf(currentPlayer));
			Log.d(TAG, "Computer marked cell " + randomCell);

			if (checkWinCondition()) {
				Log.d(TAG, "Computer won the game!");
				showWinDialog(currentPlayer);
				return;
			}

			switchPlayer();
		}
	}

	private void switchPlayer() {
		currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
		Log.d(TAG, "Switched to player " + currentPlayer);
	}

	private boolean checkWinCondition() {
		int[][] winPatterns = {
				{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
				{0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
				{0, 4, 8}, {2, 4, 6}             // Diagonals
		};
		for (int[] pattern : winPatterns) {
			if (board[pattern[0]] != ' ' &&
					board[pattern[0]] == board[pattern[1]] &&
					board[pattern[1]] == board[pattern[2]]) {
				return true;
			}
		}
		return false;
	}

	private void showWinDialog(char winner) {
		String message = (winner == 'X') ? "Player wins!" : (isSinglePlayer ? "Computer wins!" : "Player O wins!");
		AlertDialog dialog = new AlertDialog.Builder(requireActivity())
				.setTitle("Game Over")
				.setMessage(message)
				.setPositiveButton("OK", (d, which) -> mNavController.popBackStack())
				.create();
		dialog.show();
	}

	private void createNewGame() {
		Log.d(TAG, "Initializing Firebase to create a new game.");
		// TODO: Add Firebase logic to create a new game entry
	}

	private void joinExistingGame(String gameId) {
		Log.d(TAG, "Joining the existing game with ID: " + gameId);
		// TODO: Add Firebase logic to fetch and sync the game state
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_logout, menu);
	}
}
