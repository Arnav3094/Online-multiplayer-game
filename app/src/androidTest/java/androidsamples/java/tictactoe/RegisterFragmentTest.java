package androidsamples.java.tictactoe;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class RegisterFragmentTest {
	
	private final String email = "test@test.test";
	private final String password = "testtest";
	
	private void goToRegister(){
		onView(withId(R.id.txt_dont_have_account)).perform(click());
	}
	
	@Rule
	public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
	
	@Test
	public void testSuccessfulRegister() {
		goToRegister();
		
		onView(withId(R.id.edit_email)).perform(typeText("1"+email));
		onView(withId(R.id.edit_password)).perform(typeText(password));
		onView(withId(R.id.edit_confirm_password)).perform(typeText(password));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		
		// Add a small delay to wait for the fragment to load
		try {
			Thread.sleep(500); // Sleep for 0.5 seconds
			// To account for the delay to fetch from firebase
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Verify navigation to the dashboard fragment
		onView(withId(R.id.fab_new_game)).check(matches(isDisplayed()));
		
		// log out
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText("Log Out")).perform(click());
	}
	
	@Test
	public void testWeakPasswordDoesNotRegister(){
		goToRegister();
		onView(withId(R.id.edit_email)).perform(typeText("2" + email));
		onView(withId(R.id.edit_password)).perform(typeText("1234"));
		onView(withId(R.id.edit_confirm_password)).perform(typeText("1234"));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		
		// Verify that the user is still on the register fragment
		onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
	}
	
	private void clearAll(){
		// clear all
		onView(withId(R.id.edit_email)).perform(clearText());
		onView(withId(R.id.edit_password)).perform(clearText());
		onView(withId(R.id.edit_confirm_password)).perform(clearText());
		closeSoftKeyboard();
	}
	
	@Test
	public void testEmptyFieldsDoNotRegister() {
		goToRegister();
		
		// empty email, others filled
		onView(withId(R.id.edit_email)).perform(typeText(""));
		onView(withId(R.id.edit_password)).perform(typeText(password));
		onView(withId(R.id.edit_confirm_password)).perform(typeText(password));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		// Verify that the user is still on the register fragment
		onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
		
		clearAll();
		
		// empty password, others filled
		onView(withId(R.id.edit_email)).perform(typeText(email));
		onView(withId(R.id.edit_password)).perform(typeText(""));
		onView(withId(R.id.edit_confirm_password)).perform(typeText(password));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		// Verify that the user is still on the register fragment
		onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
		
		clearAll();
		
		
		// empty confirm password, others filled
		onView(withId(R.id.edit_email)).perform(typeText(email));
		onView(withId(R.id.edit_password)).perform(typeText(password));
		onView(withId(R.id.edit_confirm_password)).perform(typeText(""));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		// Verify that the user is still on the register fragment
		onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
		
		clearAll();
		
		
		// all empty
		onView(withId(R.id.edit_email)).perform(typeText(""));
		onView(withId(R.id.edit_password)).perform(typeText(""));
		onView(withId(R.id.edit_confirm_password)).perform(typeText(""));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		// Verify that the user is still on the register fragment
		onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
		
	}
	
	@Test
	public void testInvalidEmailFormatDoesNotRegister(){
		goToRegister();
		
		onView(withId(R.id.edit_email)).perform(typeText("invalid_email"));
		onView(withId(R.id.edit_password)).perform(typeText(password));
		onView(withId(R.id.edit_confirm_password)).perform(typeText(password));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		
		// Verify that the user is still on the register fragment
		onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
	}
	
	@Test
	public void testPasswordMismatchDoesNotRegister(){
		goToRegister();
		
		onView(withId(R.id.edit_email)).perform(typeText(email));
		onView(withId(R.id.edit_password)).perform(typeText(password));
		onView(withId(R.id.edit_confirm_password)).perform(typeText("mismatch"));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		
		// Verify that the user is still on the register fragment
		onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
	}
	
	@Test
	public void testExistingEmailDoesNotRegister(){
		goToRegister();
		// register 0
		onView(withId(R.id.edit_email)).perform(typeText("0"+email));
		onView(withId(R.id.edit_password)).perform(typeText(password));
		onView(withId(R.id.edit_confirm_password)).perform(typeText(password));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		
		// Add a small delay to wait for the fragment to load
		try {
			Thread.sleep(500); // Sleep for 0.5 seconds
			// To account for the delay to fetch from firebase
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// log out
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
		onView(withText("Log Out")).perform(click());
		
		// back to register
		goToRegister();
		
		onView(withId(R.id.edit_email)).perform(typeText("0"+email));
		onView(withId(R.id.edit_password)).perform(typeText(password));
		onView(withId(R.id.edit_confirm_password)).perform(typeText(password));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		
		// Verify that the user is still on the register fragment
		onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
	}
	
}