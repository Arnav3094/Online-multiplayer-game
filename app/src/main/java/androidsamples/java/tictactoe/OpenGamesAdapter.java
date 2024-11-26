package androidsamples.java.tictactoe;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OpenGamesAdapter extends RecyclerView.Adapter<OpenGamesAdapter.ViewHolder> {
	private static final String TAG = "OpenGamesAdapter";
	private final List<String> mGameList;

	public OpenGamesAdapter(List<String> gameList) {
		mGameList = gameList;
		Log.d(TAG, "Adapter initialized with " + mGameList.size() + " games.");
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Log.d(TAG, "onCreateViewHolder called.");
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.fragment_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
		Log.d(TAG, "Binding game at position " + position);
		String gameId = mGameList.get(position);
		holder.mIdView.setText(gameId);
		holder.mContentView.setText("Open Game #" + (position + 1));

		holder.mView.setOnClickListener(v -> {
			Log.d(TAG, "Game with ID " + gameId + " clicked. Navigating to join game.");
			// TODO: Implement navigation to join game
		});
	}

	@Override
	public int getItemCount() {
		Log.d(TAG, "Returning item count: " + mGameList.size());
		return mGameList.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final TextView mIdView;
		public final TextView mContentView;

		public ViewHolder(View view) {
			super(view);
			mView = view;
			mIdView = view.findViewById(R.id.item_number);
			mContentView = view.findViewById(R.id.content);
		}

		@NonNull
		@Override
		public String toString() {
			return super.toString() + " '" + mContentView.getText() + "'";
		}
	}
}
