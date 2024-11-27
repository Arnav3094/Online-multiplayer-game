package androidsamples.java.tictactoe;

import static org.mockito.Mockito.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FirebaseManagerInstrumentedTest {
    
    private FirebaseManager firebaseManager;
    private FirebaseAuth mockAuth;
    private FirebaseUser mockUser;
    private DatabaseReference mockDatabaseRef;
    private FirebaseManager.OnAuthCompleteListener mockListener;
    
    @Before
    public void setUp() {
        // Mock Firebase dependencies
        mockAuth = mock(FirebaseAuth.class);
        mockUser = mock(FirebaseUser.class);
        mockDatabaseRef = mock(DatabaseReference.class);
        mockListener = mock(FirebaseManager.OnAuthCompleteListener.class);
        
        // Initialize FirebaseManager and inject mocks
        firebaseManager = FirebaseManager.getInstance();
        setFirebaseAuthInstance(firebaseManager, mockAuth);
    }
    
    // Helper method to inject FirebaseAuth mock
    private void setFirebaseAuthInstance(FirebaseManager manager, FirebaseAuth auth) {
        try {
            java.lang.reflect.Field field = FirebaseManager.class.getDeclaredField("mAuth"); // using reflections to set private field
            field.setAccessible(true);
            field.set(manager, auth);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void testSignUp_Success() {
        // Mock FirebaseAuth.createUserWithEmailAndPassword
        AuthResult mockAuthResult = mock(AuthResult.class);
        when(mockAuthResult.getUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("mockUserId");
        
        // Mock DatabaseReference
        DatabaseReference mockUserRef = mock(DatabaseReference.class);
        when(mockDatabaseRef.child(anyString())).thenReturn(mockUserRef);
        when(mockUserRef.child(anyString())).thenReturn(mockUserRef);
        doAnswer(invocation -> {
            // Simulate successful completion of data saving
            ((Runnable) invocation.getArguments()[0]).run();
            return null;
        }).when(mockUserRef).setValue(any(), any());
        
        // Mock task success for sign-up
        when(mockAuth.createUserWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(new MockTask<>(true, mockAuthResult, null));
        
        // Call the method under test
        firebaseManager.signUp("test@example.com", "password123", mockListener);
        
        // Verify onSuccess is called
        verify(mockListener, timeout(1000)).onSuccess();
    }
    
    @Test
    public void testSignUp_Failure() {
        // Mock task failure for sign-up
        Exception mockException = new Exception("Sign-up failed");
        when(mockAuth.createUserWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(new MockTask<>(false, null, mockException));
        
        // Call the method under test
        firebaseManager.signUp("test@example.com", "password123", mockListener);
        
        // Verify onError is called with the exception
        verify(mockListener, timeout(1000)).onError(mockException);
    }
    
    @Test
    public void testSignIn_Success() {
        // Mock task success for sign-in
        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(new MockTask<>(true, null, null));
        
        // Call the method under test
        firebaseManager.signIn("test@example.com", "password123", mockListener);
        
        // Verify onSuccess is called
        verify(mockListener, timeout(1000)).onSuccess();
    }
    
    @Test
    public void testSignIn_Failure() {
        // Mock task failure for sign-in
        Exception mockException = new Exception("Sign-in failed");
        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(new MockTask<>(false, null, mockException));
        
        // Call the method under test
        firebaseManager.signIn("test@example.com", "password123", mockListener);
        
        // Verify onError is called with the exception
        verify(mockListener, timeout(1000)).onError(mockException);
    }
    
    @Test
    public void testSignOut() {
        // Call the method under test
        firebaseManager.signOut();
        
        // Verify FirebaseAuth.signOut is called
        verify(mockAuth, timeout(1000)).signOut();
    }
    
    @Test
    public void testIsSignedIn() {
        // Case: User is signed in
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        assert (firebaseManager.isSignedIn());
        
        // Case: User is not signed in
        when(mockAuth.getCurrentUser()).thenReturn(null);
        assert (!firebaseManager.isSignedIn());
    }
    
    @Test
    public void testGetCurrentUserId() {
        // Case: User is signed in
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("mockUserId");
        assert ("mockUserId".equals(firebaseManager.getCurrentUserId()));
        
        // Case: User is not signed in
        when(mockAuth.getCurrentUser()).thenReturn(null);
        assert (firebaseManager.getCurrentUserId() == null);
    }
}