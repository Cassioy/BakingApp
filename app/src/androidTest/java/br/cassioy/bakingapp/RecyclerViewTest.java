package br.cassioy.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

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

    @Rule
    public ActivityTestRule<RecipeMainActivity> mActivityRule = new ActivityTestRule<RecipeMainActivity>(RecipeMainActivity.class);


    @Test
    public void checkRecipeItem() throws Exception{

        //Click on 2nd item of the recycler view and check if has a string "Recipe Introduction" in another recyclerView
        onView(ViewMatchers.withId(R.id.recycler_view_main))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        String checkHasRecyclerView = mActivityRule.getActivity().getResources().getString(R.string.recycler_view_testing);
        onView(withText(checkHasRecyclerView)).check(matches(isDisplayed()));
    }
}

