package androidsamples.java.tictactoe;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
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
	public void testLoginFragmentAccessibility() {
		
		// Verify login fragment elements are accessible
		
		
		onView(withId(R.id.btn_log_in)).perform(click());
		onView(withId(R.id.edit_email)).perform(click());
		closeSoftKeyboard();
		onView(withId(R.id.edit_password)).perform(click());
		closeSoftKeyboard();
		onView(withId(R.id.txt_dont_have_account)).perform(click());
	}
	
	@Test
	public void testInvalidEmailLogin(){
		
		onView(withId(R.id.edit_email)).perform(typeText("invalid email"));
		closeSoftKeyboard();
		onView(withId(R.id.edit_password)).perform(typeText("password"));
		closeSoftKeyboard();
		onView(withId(R.id.btn_log_in)).perform(click());
		
		// check whether still in login fragment
		onView(withId(R.id.btn_log_in)).check(matches(isDisplayed()));
	}
	
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
	
//
//	@Test
//	public void testDashboardAccess(){
//		String email = "test@test.test";
//		String pass = "testtest";
//
//		// login
//		onView(withId(R.id.edit_email)).perform(typeText(email));
//		closeSoftKeyboard();
//		onView(withId(R.id.edit_password)).perform(typeText(pass));
//		closeSoftKeyboard();
//
//		onView(withId(R.id.btn_log_in)).perform(click());
//
//		new Handler().postDelayed(()->{
//			onView(withId(R.id.txt_user)).check(matches(isDisplayed()));
//			onView(withId(R.id.txt_stats)).check(matches(isDisplayed()));
//			onView(withId(R.id.list)).check(matches(isDisplayed()));
//			onView(withId(R.id.txt_join_or_create)).check(matches(isDisplayed()));
//			onView(withId(R.id.txt_open_games)).check(matches(isDisplayed()));
//
//			// fab
//			onView(withId(R.id.fab_new_game)).perform(click());
//			onView(withText("One-Player")).check(matches(isDisplayed()));
//		}, 2000);
//
//
//	}
	
//	@Test
//	public void testDashboardAccessibility() {
//		// Mock Firebase authentication to return true (signed in)
//		when(mockFM.isSignedIn()).thenReturn(true);
//
//		activityRule.getScenario().onActivity(activity -> {
//			FragmentManager fragmentManager = activity.getSupportFragmentManager();
//
//			// Print navigation graph information
//			NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
//			System.out.println("Current Destination: " +
//					(navController.getCurrentDestination() != null
//							? navController.getCurrentDestination().getLabel()
//							: "No current destination"));
//		});
//
//		// Wait a bit
//		SystemClock.sleep(2000);
//
//		// Try different view checks
//		try {
//			onView(withId(R.id.txt_user))
//					.check(matches(isDisplayed()));
//		} catch (NoMatchingViewException e) {
//			System.out.println("Error finding txt_user: " + e.getMessage());
//
//			// Dump the entire view hierarchy
//			onView(isRoot()).perform(new ViewAction() {
//				@Override
//				public Matcher<View> getConstraints() {
//					return isRoot();
//				}
//
//				@Override
//				public String getDescription() {
//					return "Dump View Hierarchy";
//				}
//
//				@Override
//				public void perform(UiController uiController, View view) {
//					printViewHierarchy(view, 0);
//				}
//			});
//		}
//	}
//
//	// Utility method to print view hierarchy
//	private void printViewHierarchy(View view, int depth) {
//		StringBuilder indent = new StringBuilder();
//		for (int i = 0; i < depth; i++) {
//			indent.append("  ");
//		}
//
//		System.out.println(indent + "View: " + view.getClass().getSimpleName()
//				+ ", ID: " + (view.getId() != View.NO_ID ? view.getId() : "NO_ID"));
//
//		if (view instanceof ViewGroup) {
//			ViewGroup viewGroup = (ViewGroup) view;
//			for (int i = 0; i < viewGroup.getChildCount(); i++) {
//				printViewHierarchy(viewGroup.getChildAt(i), depth + 1);
//			}
//		}
//	}
}
