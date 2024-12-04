package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GameFragment extends Fragment {
	private static final String TAG = "GameFragment";
	private static final int GRID_SIZE = 9;
// TODO: when player 1 leaves, and reopens then he is logged in as player 2
// TODO: player 2 leaves
	FirebaseManager firebaseManager;
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
	private String winner = "NULL";
	private Integer scoreUpdated = 0;
	private Integer popCnt = 0;
	private final String[] positions = {
			"Top-left", "Top-center", "Top-right",
			"Middle-left", "Center", "Middle-right",
			"Bottom-left", "Bottom-center", "Bottom-right"
	};
	private String player1Email;
	private String player2Email;
	
	
	TextView txtTurn, txtYouAre, txtPlayingAgainst;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate: called");
		setHasOptionsMenu(true);
		
		// get fm
		firebaseManager = FirebaseManager.getInstance();
		
		// get game type
		GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
		String gameType = args.getGameType();
		
		isSinglePlayer = "One-Player".equals(gameType);
		Log.d(TAG,"isSinglePlayer: "+isSinglePlayer);
		
		mGameId = args.getGameId();
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		
		userEmail = firebaseManager.getCurrentUserEmail();
		mPlayerStatsRef = FirebaseDatabase.getInstance().getReference("playerStats").child(userEmail.replace(".", ","));// Firebase key-safe format
		
		if (Objects.equals(mGameId, "NULL")) {
			Log.e(TAG, "ERROR game id is NULL");
		} else {
			// getting the game data for that ID
			mGameRef = database.getReference("games").child(mGameId);
			Log.d(TAG, "Joining existing game with ID: " + mGameId);
			
			// One time retrieval when the game fragment is created/re-created
			mGameRef.child("player1").get().addOnSuccessListener(snapshot -> {
				player1Email = snapshot.getValue(String.class); // Assign the value to player1Email
				Log.d(TAG, "Player 1 Email: " + player1Email); // Optional log
			}).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch Player 1 Email", e));

			// One time retrieval when the game fragment is created/re-created
			mGameRef.child("isSinglePlayer").get().addOnSuccessListener(snapshot -> {
				isSinglePlayer = Boolean.TRUE.equals(snapshot.getValue(boolean.class)); // Assign the value to player1Email
				Log.d(TAG, "isSinglePlayer: " + isSinglePlayer); // Optional log
			}).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch Player 1 Email", e));
			
			// One time retrieval when the game fragment is created/re-created
			mGameRef.child("player2").get().addOnSuccessListener(snapshot -> {
				player2Email = snapshot.getValue(String.class); // Assign the value to player1Email
				Log.d(TAG, "Player 2 Email: " + player2Email);
				if(userEmail.equals(player1Email)) mySymbol = "X";
				else mySymbol ="O";
				Log.d(TAG, userEmail+" player1 " + player1Email + " mysymbol "+mySymbol);
				String youAreText = "You are: " + mySymbol;
				if(txtYouAre == null)Log.e(TAG,"txtYouAre is null");
				else txtYouAre.setText(youAreText);
				
				joinExistingGame();
			}).addOnFailureListener(e -> Log.e(TAG, "Failed to fetch Player 2 Email", e));
		}
		
		mGameRef.child("winner").addValueEventListener(setWinner);

		OnBackPressedCallback callback = new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				Log.d(TAG, "Back pressed");
				if(Objects.equals(winner, "NULL")){
					Log.d("GameFragment", "winner is NULL, showing alert");
					AlertDialog dialog = new AlertDialog.Builder(requireActivity())
							.setTitle(R.string.confirm)
							.setMessage(R.string.forfeit_game_dialog_message)
							.setPositiveButton(R.string.yes, (d, which) -> {
								Log.d(TAG, "User confirmed forfeit. Navigating back.");
								// Loss for the player who forfeited
								popCnt++;
								updatePlayerStats("loss");
								scoreUpdated++;
								mGameRef.child("winner").setValue((Objects.equals(mySymbol, "X"))?"O":"X");
								Log.d(TAG,"Number of popups: "+popCnt+" player score changed: "+scoreUpdated);
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
		};
		requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
		
		Log.d(TAG, "onCreate: over");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_game, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "onViewCreated: called");
		
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
        else{
			if(currentTurn.equals("O")){
				makeComputerMove();
				Log.d(TAG,"Computer move in view Created");
				Log.d(TAG,"Before database setting value gamestate array "+gameState.toString() );
				Map<String, Object> updates = new HashMap<>();
				updates.put("currentTurn",currentTurn);
				updates.put("gameState",gameState);
				updateGameFields(mGameId, updates);
			}
			
			mGameRef.child("currentTurn").addValueEventListener(turnUIEventListener);
		}
		
		txtTurn = view.findViewById(R.id.txt_turn);
		txtYouAre = view.findViewById(R.id.txt_you_are);
		txtPlayingAgainst = view.findViewById(R.id.txt_playing_against);
		
		mGameRef.child("player2").addValueEventListener(player2Listener);
		
		mGameRef.child("winner").addValueEventListener(winnerUIListener);
	}
	
	private final ValueEventListener setWinner = new ValueEventListener() {
		@Override
		public void onDataChange(@NonNull DataSnapshot snapshot) {
			Log.d(TAG, "setWinner:onDataChange: winner = " + winner);
			winner = snapshot.getValue(String.class);
		}
		@Override
		public void onCancelled(@NonNull DatabaseError error) {
			Log.e(TAG, "Failed to fetch winner", error.toException());
		}
	};
	
	private final ValueEventListener turnUIEventListener = new ValueEventListener() {
		@Override
		public void onDataChange(@NonNull DataSnapshot snapshot) {
			Log.d(TAG, "turnUIEventListener: onDataChange: updating turn ui");
			updateTurnUI();
		}
		
		@Override
		public void onCancelled(@NonNull DatabaseError error) {
			Log.e(TAG, "Failed to fetch current turn", error.toException());
		}
	};
	
	private final ValueEventListener listenToGameUpdatesListener = new ValueEventListener() {
		@Override
		public void onDataChange(@NonNull DataSnapshot snapshot) {
			Log.d(TAG, "listenToGameUpdatesListener: onDataChange: called");
			GameData data = snapshot.getValue(GameData.class);
			if (data != null) {
				Log.d(TAG, "listenToGameUpdatesListener: onDataChange: data.gameState = " + data.gameState);
//					otherForfeited = !(data.getWinner().equals("NULL"));
				winner = data.winner;
				
				if(!(winner.equals("NULL"))) { // Game has reached a conclusion
					Log.d(TAG,"Winner is decided: "+winner);
					if(winner.equals("draw")){
						if(scoreUpdated == 0) {
							updatePlayerStats("draw");
							scoreUpdated++;
						}
						if(popCnt == 0) {
							Log.d(TAG,"Draw in onDatachange");
							showWinDialog("Draw");
							popCnt++;
						}
					}
					else{
						if(scoreUpdated == 0) {
							updatePlayerStats(winner.equals(mySymbol) ? "win" : "loss");
							scoreUpdated++;
						}
						if(popCnt == 0) {
							showWinDialog(winner);
							popCnt++;
						}
					}
				}
				currentTurn = data.currentTurn;
				gameState = data.gameState;
				updateUI();
				updateTurnUI();
			}
		}
		
		@Override
		public void onCancelled(@NonNull DatabaseError error) {
			Log.e(TAG, "Failed to listen for game updates", error.toException());
		}
	};
	
	private final ValueEventListener winnerUIListener = new ValueEventListener() {
		@Override
		public void onDataChange(@NonNull DataSnapshot snapshot) {
			// if winner is "NULL" then nothing to do
			// if winner is "O" or "X" then change your turn to you win/lose/draw
			String winnerSymbol = snapshot.getValue(String.class);
			if(winnerSymbol == null){
				Log.e(TAG, "Winner is null");
				return;
			}
			if(winnerSymbol.equals("NULL")) return;
			else if(winnerSymbol.equals(mySymbol)){
				Log.d(TAG, "onViewCreated: "+ "You win");
				txtTurn.setText(getResources().getString(R.string.win));
			} else if(winnerSymbol.equals("draw")){
				Log.d(TAG, "onViewCreated: " + "Draw");
				txtTurn.setText(getResources().getString(R.string.draw));
			}
			else{
				Log.d(TAG, "onViewCreated: " + "You lose");
				txtTurn.setText(getResources().getString(R.string.lose));
			}
		}
		
		@Override
		public void onCancelled(@NonNull DatabaseError error) {
			Log.e(TAG, "Failed to fetch winner", error.toException());
		}
	};
	
	private final ValueEventListener player2Listener = new ValueEventListener() {
		@Override
		public void onDataChange(@NonNull DataSnapshot snapshot) {
			player2Email = snapshot.getValue(String.class);
			Log.d(TAG, "Player 2 Email:: " + player2Email);
			String playingAgainstText;
			if(player2Email.equals("NULL")) playingAgainstText = "Waiting for player to join...";
			else playingAgainstText = "Playing against: " + ( userEmail.equals(player1Email) ? player2Email :  player1Email);
			txtPlayingAgainst.setText(playingAgainstText);
			
		}
		@Override
		public void onCancelled(@NonNull DatabaseError error) {
			Log.e(TAG, "Failed to fetch Player 2 Email", error.toException());
		}
	};
	
	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "onDetach: called");
		Log.d(TAG, "onDetach: removing valueEventListeners");
		mGameRef.child("winner").removeEventListener(setWinner);
		mGameRef.child("winner").removeEventListener(winnerUIListener);
		mGameRef.removeEventListener(listenToGameUpdatesListener);
		mGameRef.child("player2").removeEventListener(player2Listener);
		mGameRef.child("currentTurn").removeEventListener(turnUIEventListener);
		Log.d(TAG, "onDetach: removed valueEventListeners");
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
		if ( !(winner.equals("NULL")) || (!gameState.get(index).isEmpty() || (!currentTurn.equals(mySymbol))) ){
			return;
		}
		gameState.set(index, currentTurn);
		Log.d(TAG,"isSinglePlayer: "+isSinglePlayer+" currentTurn "+currentTurn);
		updateUI();
		if (checkWin()) {
			Map<String, Object> updates = new HashMap<>();
			updates.put("currentTurn",currentTurn);
			updates.put("gameState",gameState);
			updates.put("winner",currentTurn);
			updateGameFields(mGameId, updates);
//			mGameRef.setValue(new GameData(isSinglePlayer, currentTurn, gameState,currentTurn,player1Email,player2Email));
			winner = (currentTurn.equals(mySymbol) ? "win" : "loss");
			Log.d(TAG,"Winner is: "+winner+" inside checkwin ");
			Log.d(TAG," scoreupdate: " + scoreUpdated);
			if(scoreUpdated == 0) {
				updatePlayerStats(currentTurn.equals(mySymbol) ? "win" : "loss");
				scoreUpdated++;
			}
			if(popCnt == 0) {
				showWinDialog(currentTurn);
				popCnt++;
			}
		} else if (isDraw()) {
			Map<String, Object> updates = new HashMap<>();
			updates.put("currentTurn",currentTurn);
			updates.put("gameState",gameState);
			updates.put("winner","draw");
			updateGameFields(mGameId, updates);
//			mGameRef.setValue(new GameData(isSinglePlayer, currentTurn, gameState,"draw",player1Email,player2Email));
			winner = "draw";
			Log.d(TAG,"Drawn popcnt "+popCnt);
			if(scoreUpdated == 0) {
				updatePlayerStats("draw");
				scoreUpdated++;
			}
			if(popCnt == 0) {
				showWinDialog("Draw");
				popCnt++;
			}
		} else {
			switchTurn();
			//For sleeping

			if (isSinglePlayer && currentTurn.equals("O")) {
				new Handler().postDelayed(() -> {
					Log.d(TAG,"Computer move");
					makeComputerMove();
					Log.d(TAG,"Before database setting value gamestate array "+gameState.toString() );
					Map<String, Object> updates = new HashMap<>();
					updates.put("currentTurn",currentTurn);
					updates.put("gameState",gameState);
					updateGameFields(mGameId, updates);
					return;
				}, 0);
			}
//			sleep(1000);

			Log.d(TAG,"Before database setting value gamestate array "+gameState.toString() );
			Map<String, Object> updates = new HashMap<>();
			updates.put("currentTurn",currentTurn);
			updates.put("gameState",gameState);
			updateGameFields(mGameId, updates);
		}
	}
	private void updatePlayerStats(String result) {
		if (mPlayerStatsRef == null) return;

		mPlayerStatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				int wins = Objects.requireNonNullElse(snapshot.child("wins").getValue(Integer.class), 0);
				int losses = Objects.requireNonNullElse(snapshot.child("losses").getValue(Integer.class), 0);
				int draws = Objects.requireNonNullElse(snapshot.child("draws").getValue(Integer.class), 0);

				Log.d(TAG,"prev wins: "+wins+" prev losses: "+losses+" cur result: "+result);
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
		Log.d(TAG,"In Computer: Before database setting value gameState array " + gameState);
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
	
	/**
	 * Update the text on the buttons to match game state
	 */
	private void updateUI() {
		Log.d(TAG,"updateUI: gameState: " + gameState);
		for (int i = 0; i < GRID_SIZE; i++) {
			mButtons[i].setText(gameState.get(i));
		}
	}

	private void listenToGameUpdates() {
		mGameRef.addValueEventListener(listenToGameUpdatesListener);
	}
	
	/**
	 * Updates UI regarding turn state such as "Your turn"'s visibility, and button enable state
	 */
	private void updateTurnUI(){
		boolean myTurn = currentTurn.equals(mySymbol);
		Log.d(TAG,"updateTurnUI: currentTurn: " + currentTurn + " mySymbol: " + mySymbol);
		txtTurn.setVisibility(myTurn ? View.VISIBLE : View.INVISIBLE);
		for(Button button : mButtons){
			button.setEnabled(myTurn);
		}
	}

	private void showWinDialog(String winner) {
		String dialogMessage;
		if(winner.equals(mySymbol))
		{
			dialogMessage ="Congrats You Win!!";
		}
		else{
			dialogMessage ="You Lost :(";
		}
		String message = "Draw".equals(winner) ? "It's a draw!" : dialogMessage;
		if (!"Draw".equals(winner) && winner.equals(mySymbol)) {
			updatePlayerStats("win");
		} else if (!"Draw".equals(winner)) {
			updatePlayerStats("loss");
		}
		if(getActivity() == null){
//			Log.e(TAG,"Activity is null");
			return;
		}
		new AlertDialog.Builder(getActivity())
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
		public String player1 = "NULL";
		public String player2 = "NULL";
		public GameData(boolean isSinglePlayer, String currentTurn, List<String> gameState,String winner,String player1, String player2) {
			this.isSinglePlayer = isSinglePlayer;
			this.currentTurn = currentTurn;
			this.gameState = gameState;
			this.winner = winner;
			this.player1 = player1;
			this.player2 = player2;
		}
		public String getWinner() {
			return winner;
		}
		public String getPlayer1() {return player1;}
		public String getPlayer2() {return player2;}
	}

	private void updateGameFields(String gameId, Map<String, Object> updates) {
		DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference("games").child(gameId);

		gameRef.updateChildren(updates)
				.addOnSuccessListener(aVoid -> Log.d("DashboardFragment", "Game fields updated successfully"))
				.addOnFailureListener(e -> Log.e("DashboardFragment", "Failed to update game fields", e));
	}

}
