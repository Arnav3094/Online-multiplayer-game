package androidsamples.java.tictactoe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OpenGamesAdapter extends RecyclerView.Adapter<OpenGamesAdapter.ViewHolder> {
	
	public OpenGamesAdapter() {
		// FIXME if needed
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.fragment_item, parent, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
		// TODO bind the item at the given position to the holder
	}
	
	@Override
	public int getItemCount() {
		return 0; // FIXME
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