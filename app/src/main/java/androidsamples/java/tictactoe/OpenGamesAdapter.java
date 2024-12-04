package androidsamples.java.tictactoe;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OpenGamesAdapter extends RecyclerView.Adapter<OpenGamesAdapter.ViewHolder> {
	private static final String TAG = "OpenGamesAdapter";
	private List<String> mGameList;
	public GameViewModel gViewModel;
	public OpenGamesAdapter(List<String> gameList, GameViewModel gviewModel) {
		this.gViewModel = gviewModel;
		mGameList = gameList != null ? gameList : new ArrayList<>();
		Log.d(TAG, "Adapter initialized with " + mGameList.size() + " games.");
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		Log.d(TAG, "onCreateViewHolder: Creating view holder");
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.fragment_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
		String gameId = mGameList.get(position);
		Log.d(TAG, "onBindViewHolder: Binding game ID " + gameId + " at position " + position);
		holder.mIdView.setText(gameId);
		String openGameText = "Open Game #" + (position + 1);
		holder.mContentView.setText(openGameText);

		holder.mView.setOnClickListener(v -> {
			Log.d(TAG, "Game clicked: Game ID " + gameId);
			DatabaseReference mGameRef;
			FirebaseDatabase database = FirebaseDatabase.getInstance();
			mGameRef = database.getReference("games").child(gameId);
			FirebaseManager firebaseManager = FirebaseManager.getInstance();

			mGameRef.child("player1").addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot snapshot) {
					String player1Email = snapshot.getValue(String.class);
					Log.d(TAG,"In opengamesadapter: "+player1Email);
					if(!(firebaseManager.getCurrentUserEmail().equals(player1Email))) {
						mGameRef.child("player2").setValue(firebaseManager.getCurrentUserEmail());
					}
					gViewModel.reset();
					NavDirections action = DashboardFragmentDirections.actionGame("two_player", gameId);
					Navigation.findNavController(v).navigate(action);
				}
				@Override
				public void onCancelled(@NonNull DatabaseError error) {
					Log.e("GameFragment", "Error fetching winner", error.toException());
				}
			});

		});
	}

	@Override
	public int getItemCount() {
		Log.d(TAG, "getItemCount: Returning item count of " + mGameList.size());
		return mGameList.size();
	}

	public void updateData(List<String> newGameList) {
		Log.d(TAG, "updateData: Updating game list with " + newGameList.size() + " games");
		mGameList = Objects.requireNonNullElse(newGameList, new ArrayList<>());
		notifyDataSetChanged();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final TextView mIdView;
		public final TextView mContentView;

		public ViewHolder(View view) {
			super(view);
			Log.d(TAG, "ViewHolder: ViewHolder created");
			mView = view;
			mIdView = view.findViewById(R.id.item_number);
			mContentView = view.findViewById(R.id.content);
		}

		@NonNull
		@Override
		public String toString() {
			return "ViewHolder{" + "ID='" + mIdView.getText() + "', content='" + mContentView.getText() + "'}";
		}
	}
}
