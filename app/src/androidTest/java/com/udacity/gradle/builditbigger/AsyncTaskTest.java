package com.udacity.gradle.builditbigger;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.isEmptyString;

//The following should be checked before running the AsyncTaskTest: 1) uninstall the app
// or clear data storage; 2) start the appengine.
// Note this test can be run with project-level gradle task 'runAsyncTest'.
// Appengine must be in stop position before starting the 'runAsyncTest' task.

@RunWith(AndroidJUnit4.class)
public class AsyncTaskTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    // Register the MainActivity as a resource that needs to be synchronized with Espresso before
    // the test is run.
    @Before
    public void registerIdlingResource() {
        //Instantiate the IdlingResource and link to the MenuActivity
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        //Register the IdlingResource
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    // Test that the MainActivity launches, that clicking on the button triggers an
    //asynchronous, background task that opens the Android Activity with a non-empty textview, and
    //that clicking on the button in the AndroidActivity results in the display of a joke.
    @Test
    public void idlingResourceTest_checkAndroidActivityLaunch() {
        //1. Get the view
        //2. Perform an action on the view
        //3. Check that the action produces the result expected

        // Find the "Tell Joke" button in the MainActivity display and click on it
        onView((withId(R.id.button))).perform(click());

        //Check for launch of Android Activity with correct text
        onView(withId(R.id.joke_tv))
                .check(matches(withText("Press the button to hear a funny joke")));

        //Get a reference to the "Tell Joke" button in the AndroidActivity and click on it
        onView(withId(R.id.button_next_joke)).perform(click());

        //Check that a non-empty string is returned
        ViewInteraction view = onView(withId(R.id.joke_tv));
        assertThat(view.toString(), CoreMatchers.not(isEmptyString()));
    }

    // Unregister resources when test finishes
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
