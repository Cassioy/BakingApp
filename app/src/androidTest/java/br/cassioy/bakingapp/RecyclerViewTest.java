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
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    //Test only works if you have internet
    @Test
    public void clickRecyclerViewItem() throws Exception {

        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
        onView(ViewMatchers.withId(R.id.no_internet_layout)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        IdlingRegistry.getInstance().unregister(mIdlingResource);


        //Some old devices takes longer to make asynchronous request, setting up a minimum wait time
        Thread.sleep(1000);

        //Click on 1st item recycler view main menu and check if has a string "Recipe Introduction" on first item of the steps recyclerView
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
            onView(ViewMatchers.withId(R.id.recycler_view_main))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            IdlingRegistry.getInstance().unregister(mIdlingResource);

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

