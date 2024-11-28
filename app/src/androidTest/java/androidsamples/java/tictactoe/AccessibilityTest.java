package androidsamples.java.tictactoe;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AccessibilityTest {
	
	@Rule
	public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
	
	@Before
	public void setUp() {
		// Enable accessibility checks
		AccessibilityChecks.enable().setRunChecksFromRootView(true);
	}
	
	@Test
	public void testClickabilityOnLoginFragment(){
		
		// Verify login fragment elements are clickable
		onView(withId(R.id.edit_email)).check(matches(isClickable()));
		onView(withId(R.id.edit_password)).check(matches(isClickable()));
		onView(withId(R.id.btn_log_in)).check(matches(isClickable()));
		onView(withId(R.id.txt_dont_have_account)).check(matches(isClickable()));
	}
	
	@Test
	public void testClickabilityOnRegisterFragment(){
		
		// go to register
		onView(withId(R.id.txt_dont_have_account)).perform(click());
		
		// Verify register fragment elements are clickable
		onView(withId(R.id.edit_email)).check(matches(isClickable()));
		onView(withId(R.id.edit_password)).check(matches(isClickable()));
		onView(withId(R.id.edit_confirm_password)).check(matches(isClickable()));
		onView(withId(R.id.btn_register)).check(matches(isClickable()));
		onView(withId(R.id.txt_have_account)).check(matches(isClickable()));
	}
	
	@Test
	public void testLoginFragmentAccessibility() {
		
		// Verify login fragment elements are accessible
		onView(withId(R.id.btn_log_in)).perform(click());
		onView(withId(R.id.edit_email)).perform(click());
		closeSoftKeyboard();
		onView(withId(R.id.edit_password)).perform(click());
		closeSoftKeyboard();
		onView(withId(R.id.txt_dont_have_account)).perform(click());
	}
	
//	@Test
//	public void testInvalidEmailLogin(){
//
//		onView(withId(R.id.edit_email)).perform(typeText("invalid email"));
//		closeSoftKeyboard();
//		onView(withId(R.id.edit_password)).perform(typeText("password"));
//		closeSoftKeyboard();
//		onView(withId(R.id.btn_log_in)).perform(click());
//
//		// check whether still in login fragment
//		onView(withId(R.id.btn_log_in)).check(matches(isDisplayed()));
//	}
	
	@Test
	public void testRegisterFragmentAccessibility() {
		
		// go to register
		onView(withId(R.id.txt_dont_have_account)).perform(click());
		
		// check if register elements are accessible
		onView(withId(R.id.edit_email)).perform(typeText("invalid email"));
		closeSoftKeyboard();
		onView(withId(R.id.edit_password)).perform(typeText("password"));
		closeSoftKeyboard();
		onView(withId(R.id.edit_confirm_password)).perform(typeText("password"));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		
		onView(withId(R.id.txt_have_account)).perform(click());
	}
	
	@Test
	public void testRegisterFragmentInvalidPassword(){
		// go to register
		onView(withId(R.id.txt_dont_have_account)).perform(click());
		
		// check if register elements are accessible
		onView(withId(R.id.edit_email)).perform(typeText("valid@email.com"));
		closeSoftKeyboard();
		onView(withId(R.id.edit_password)).perform(typeText("weak"));
		closeSoftKeyboard();
		onView(withId(R.id.edit_confirm_password)).perform(typeText("weak"));
		closeSoftKeyboard();
		onView(withId(R.id.btn_register)).perform(click());
		
		onView(withId(R.id.txt_have_account)).perform(click());
	}
	
	@Test
	public void testLoginNoEmail(){
		onView(withId(R.id.edit_password)).perform(typeText("password"));
		closeSoftKeyboard();
		onView(withId(R.id.btn_log_in)).perform(click());
		
		onView(withId(R.id.btn_log_in)).check(matches(isDisplayed()));
	}
	
}
