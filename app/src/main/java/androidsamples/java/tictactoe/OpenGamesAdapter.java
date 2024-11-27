package androidsamples.java.tictactoe;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OpenGamesAdapter extends RecyclerView.Adapter<OpenGamesAdapter.ViewHolder> {
	private static final String TAG = "OpenGamesAdapter";
	private List<String> mGameList;

	public OpenGamesAdapter(List<String> gameList) {
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
		holder.mContentView.setText("Open Game #" + (position + 1));

		holder.mView.setOnClickListener(v -> {
			Log.d(TAG, "Game clicked: Game ID " + gameId);
			NavDirections action = DashboardFragmentDirections.actionGame("two_player", gameId);
			Navigation.findNavController(v).navigate(action);
		});
	}

	@Override
	public int getItemCount() {
		Log.d(TAG, "getItemCount: Returning item count of " + mGameList.size());
		return mGameList.size();
	}

	public void updateData(List<String> newGameList) {
		Log.d(TAG, "updateData: Updating game list with " + newGameList.size() + " games");
		mGameList = newGameList != null ? newGameList : new ArrayList<>();
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
