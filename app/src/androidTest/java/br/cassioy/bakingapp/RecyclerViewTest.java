package br.cassioy.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by cassioimamura on 2/6/18.
 */

@RunWith(JUnit4.class)
@LargeTest
public class RecyclerViewTest {

    private IdlingResource mIdlingResource;

    @Rule
    public ActivityTestRule<RecipeMainActivity> mActivityRule = new ActivityTestRule<RecipeMainActivity>(RecipeMainActivity.class);


    @Before
    public void registerIdlingResource(){
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:

        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void clickRecyclerViewItem() throws Exception {

        //Click on 1st item recycler view main menu and check if has a string "Recipe Introduction" on first item of the steps recyclerView
        onView(ViewMatchers.withId(R.id.recycler_view_main))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        String checkHasRecyclerView = mActivityRule.getActivity().getResources().getString(R.string.recycler_view_testing);

        onView(withText(checkHasRecyclerView)).check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}

