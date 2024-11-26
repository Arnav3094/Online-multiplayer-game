package androidsamples.java.tictactoe;

import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {
	private String email;
	private String password;
	private String confirmPassword;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}
	
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	public void clear() {
		email = "";
		password = "";
		confirmPassword = "";
	}
}
