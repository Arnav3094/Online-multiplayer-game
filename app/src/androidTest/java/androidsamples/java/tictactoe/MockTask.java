package androidsamples.java.tictactoe;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

class MockTask<T> extends Task<T> {
	private final boolean isSuccess;
	private final T result;
	private final Exception exception;
	
	MockTask(boolean isSuccess, T result, Exception exception) {
		this.isSuccess = isSuccess;
		this.result = result;
		this.exception = exception;
	}
	
	@Override
	public boolean isComplete() {
		return true;
	}
	
	@Override
	public boolean isSuccessful() {
		return isSuccess;
	}
	
	@Override
	public T getResult() {
		if (!isSuccess) throw new RuntimeException("Task failed");
		return result;
	}
	
	@Override
	public <X extends Throwable> T getResult(@NonNull Class<X> aClass) throws X {
		return getResult();
	}
	
	@Override
	public Exception getException() {
		return exception;
	}
	
	@Override
	public boolean isCanceled() {
		return false;
	}
	
	@NonNull
	@Override
	public Task<T> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
		if (!isSuccess && exception != null) {
			onFailureListener.onFailure(exception);
		}
		return this;
	}
	
	@NonNull
	@Override
	public Task<T> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
		if (!isSuccess && exception != null) {
			executor.execute(() -> onFailureListener.onFailure(exception));
		}
		return this;
	}
	
	@NonNull
	@Override
	public Task<T> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
		return addOnFailureListener(executor -> onFailureListener.onFailure(exception));
	}
	
	@NonNull
	@Override
	public Task<T> addOnSuccessListener(@NonNull OnSuccessListener<? super T> onSuccessListener) {
		if (isSuccess) {
			onSuccessListener.onSuccess(result);
		}
		return this;
	}
	
	@NonNull
	@Override
	public Task<T> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super T> onSuccessListener) {
		if (isSuccess) {
			executor.execute(() -> onSuccessListener.onSuccess(result));
		}
		return this;
	}
	
	@NonNull
	@Override
	public Task<T> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super T> onSuccessListener) {
		return addOnSuccessListener(executor -> onSuccessListener.onSuccess(result));
	}
}