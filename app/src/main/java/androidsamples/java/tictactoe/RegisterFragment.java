package androidsamples.java.tictactoe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private LoginViewModel viewModel;
    private FirebaseManager firebaseManager;
    private FirebaseAuth mAuth;  @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = FirebaseManager.getInstance();
        mAuth = FirebaseAuth.getInstance();

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText etEmail = view.findViewById(R.id.edit_email);
        EditText etPassword = view.findViewById(R.id.edit_password);
        EditText etConfirmPassword = view.findViewById(R.id.edit_confirm_password);

        etEmail.setText(viewModel.getEmail());
        etPassword.setText(viewModel.getPassword());

        view.findViewById(R.id.btn_register)
                .setOnClickListener(v -> {
                    Log.d(TAG, "Register Button Pressed");
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String confirmPassword = etConfirmPassword.getText().toString().trim();

                    if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        Snackbar.make(v, "All fields are required", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    if (!password.equals(confirmPassword)) {
                        Snackbar.make(v, "Passwords do not match", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        saveUserToRealtimeDatabase(email); // Save to Realtime Database
                                        Snackbar.make(v, "Registration successful. Please log in.", Snackbar.LENGTH_SHORT).show();
                                        viewModel.clear();
                                        NavDirections action = RegisterFragmentDirections.actionRegisterToLogin();
                                        Navigation.findNavController(v).navigate(action);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Snackbar.make(v, "Registration failed. Please try again.", Snackbar.LENGTH_SHORT).show();
//                                        updateUI(null);
                                    }
                                }
                            });


                });

        return view;
    }

    private void saveUserToRealtimeDatabase(String email) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Save only the email in the Realtime Database
            userRef.child("email").setValue(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User data saved to Realtime Database");
                        } else {
                            Log.e(TAG, "Failed to save user data", task.getException());
                        }
                    });
        }
    }
}
