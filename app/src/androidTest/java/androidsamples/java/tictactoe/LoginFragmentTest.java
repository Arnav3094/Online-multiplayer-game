package androidsamples.java.tictactoe;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class LoginFragmentTest {
	
	@Rule
	public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
	
	@Test
	public void testSuccessfulLogin() {
		String email = "test@test.test";
		String password = "testtest";
		
		// Simulate entering valid credentials
		onView(withId(R.id.edit_email)).perform(typeText(email));
		onView(withId(R.id.edit_password)).perform(typeText(password));
		
		// Click login button
		onView(withId(R.id.btn_log_in)).perform(click());
		
		// Add a small delay to wait for the fragment to load
		try {
			Thread.sleep(2000); // Sleep for 2 seconds
			// To account for the delay to fetch from firebase
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Verify navigation to the dashboard fragment
		onView(withId(R.id.fab_new_game)).check(matches(isDisplayed()));
	}
	

}