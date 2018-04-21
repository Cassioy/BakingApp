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


@RunWith(JUnit4.class)
@LargeTest
public class ExoplayerTest {

    private IdlingResource mIdlingResource;

    @Rule
    public ActivityTestRule<RecipeMainActivity> mActivityRule = new ActivityTestRule<RecipeMainActivity>(RecipeMainActivity.class);


    @Before
    public void registerIdlingResource(){
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:

        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    //Testing interactions with Recyclerview check if video is displayed and check if next button is working
    @Test
    public void checkVideo() throws Exception {

        //Some old devices takes longer to make asynchronous request, setting up a minimum wait time
        Thread.sleep(1000);

        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
        //Click on 1st item recycler view main menu
        onView(ViewMatchers.withId(R.id.recycler_view_main))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        IdlingRegistry.getInstance().unregister(mIdlingResource);

        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
        //Click on Introduction step
        onView(ViewMatchers.withId(R.id.recipe_step_ingredients_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        IdlingRegistry.getInstance().unregister(mIdlingResource);

        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
        //Check if video is displayed
        onView(ViewMatchers.withId(R.id.step_video_view)).check(matches(isDisplayed()));
        IdlingRegistry.getInstance().unregister(mIdlingResource);

        //Tablet versions don't have next button
        boolean tabletSize = mActivityRule.getActivity().getResources().getBoolean(R.bool.is_tablet);
        if(!tabletSize) {

            mIdlingResource = mActivityRule.getActivity().getIdlingResource();
            IdlingRegistry.getInstance().register(mIdlingResource);
            onView(ViewMatchers.withId(R.id.step_next)).perform(click());
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
