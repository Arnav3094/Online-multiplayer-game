package androidsamples.java.tictactoe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ViewModelsTest {
    // TESTS FOR LOGIN VIEW MODEL
    @Test
    public void setEmailAndGetEmail() {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.setEmail("test");
        assertEquals("test", viewModel.getEmail());
    }
    
    @Test
    public void setPasswordAndGetPassword() {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.setPassword("test");
        assertEquals("test", viewModel.getPassword());
    }
    
    @Test
    public void clearWorksAsExpected() {
        LoginViewModel viewModel = new LoginViewModel();
        viewModel.setEmail("test");
        viewModel.setPassword("test");
        viewModel.clear();
        assertEquals("", viewModel.getEmail());
        assertEquals("", viewModel.getPassword());
    }
    
    // TESTS FOR REGISTER VIEW MODEL
    
    @Test
    public void setEmailAndGetEmailForRegisterViewModel() {
        RegisterViewModel viewModel = new RegisterViewModel();
        viewModel.setEmail("test");
        assertEquals("test", viewModel.getEmail());
    }
    
    @Test
    public void setPasswordAndGetPasswordForRegisterViewModel() {
        RegisterViewModel viewModel = new RegisterViewModel();
        viewModel.setPassword("test");
        assertEquals("test", viewModel.getPassword());
    }
    
    @Test
    public void setConfirmPasswordAndGetConfirmPasswordForRegisterViewModel() {
        RegisterViewModel viewModel = new RegisterViewModel();
        viewModel.setConfirmPassword("test");
        assertEquals("test", viewModel.getConfirmPassword());
    }
    
    @Test
    public void clearWorksAsExpectedForRegisterViewModel() {
        RegisterViewModel viewModel = new RegisterViewModel();
        viewModel.setEmail("test");
        viewModel.setPassword("test");
        viewModel.setConfirmPassword("test");
        viewModel.clear();
        assertEquals("", viewModel.getEmail());
        assertEquals("", viewModel.getPassword());
        assertEquals("", viewModel.getConfirmPassword());
    }
}