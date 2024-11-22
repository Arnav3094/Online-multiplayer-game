package androidsamples.java.tictactoe;

import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
	private String email;
	private String password;
	
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
	
	public void clear() {
		email = "";
		password = "";
	}
}
