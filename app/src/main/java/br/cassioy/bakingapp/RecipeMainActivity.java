package br.cassioy.bakingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import br.cassioy.bakingapp.idlingresource.CustomIdlingResource;
import br.cassioy.bakingapp.model.Ingredient;
import br.cassioy.bakingapp.model.Recipe;

public class RecipeMainActivity extends AppCompatActivity {

    @Nullable
    private CustomIdlingResource mIdlingResource;

    public static final String RECIPE_KEY = "br.cassioy.bakingapp.extra.RECIPE_KEY";
    public static final String RECIPE_DATA = "br.cassioy.bakingapp.extra.RECIPE_DATA";
    private static final String BACKSTACK_STEP_HOME = "br.cassioy.bakingapp.backstack_home";
    private static final String BACKSTACK_STEP_DETAILS = "br.cassioy.bakingapp.backstack_details";
    private static final String BACKSTACK_STEP_DESCRIPTION = "br.cassioy.bakingapp.backstack_description";
    private static final String BUNDLE_INGREDIENTS = "br.cassioy.bakingapp.bundle_ingredients";
    private static final String BUNDLE_STEP = "br.cassioy.bakingapp.bundle_step";
    private static final String BUNDLE_NAME = "br.cassioy.bakingapp.bundle_name";



    private ArrayList<Recipe> mRecipeData;
    private int mRecipeKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.recipe_toolbar);
        setSupportActionBar(toolbar);

        ActionBar a = getSupportActionBar();

        a.setDisplayHomeAsUpEnabled(false);

        mRecipeData = getIntent().getParcelableArrayListExtra(RECIPE_DATA);
        mRecipeKey = getIntent().getIntExtra(RECIPE_KEY, -1);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            //Check if pendingIntent data is valid
            if(mRecipeData != null && mRecipeKey != -1){

                List<Ingredient> ingredients = mRecipeData.get(mRecipeKey).getIngredients();
                List<Ingredient.Step> recipeStep = mRecipeData.get(mRecipeKey).getSteps();
                String recipeName = mRecipeData.get(mRecipeKey).getName();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(BUNDLE_INGREDIENTS, (ArrayList<Ingredient>) ingredients);
                bundle.putParcelableArrayList(BUNDLE_STEP, (ArrayList<Ingredient.Step>) recipeStep);
                bundle.putString(BUNDLE_NAME, recipeName);

                RecipeDescriptionFragment frag = new RecipeDescriptionFragment();
                frag.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().addToBackStack(BACKSTACK_STEP_DESCRIPTION)
                        .replace(R.id.recipe_main_fragment, frag).commit();

            }else {

                // Create a new Fragment to be placed in the activity layout
                RecipeMainFragment firstFragment = new RecipeMainFragment();


                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction().addToBackStack(BACKSTACK_STEP_HOME)
                        .add(R.id.recipe_main_fragment, firstFragment).commit();
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Get Current Fragment by id
        android.support.v4.app.Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.recipe_main_fragment);
        String fragTag = currentFrag.toString();

        //Set backstack accordingly to fragments flow
        if(id == android.R.id.home){

            if(fragTag.contains("RecipeDescriptionFragment")){

                RecipeMainFragment firstFragment = new RecipeMainFragment();
                getSupportFragmentManager().beginTransaction().addToBackStack(BACKSTACK_STEP_HOME)
                        .replace(R.id.recipe_main_fragment, firstFragment).commit();
            }

            if(fragTag.contains("StepDetailsFragment")){
                    getSupportFragmentManager().popBackStack(BACKSTACK_STEP_DESCRIPTION, 0);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        android.support.v4.app.Fragment currentFrag = getSupportFragmentManager().findFragmentById(R.id.recipe_main_fragment);
        if (currentFrag != null) {

            String fragTag = currentFrag.toString();

            if(fragTag.contains("RecipeDescriptionFragment")){
                getSupportFragmentManager().popBackStack(BACKSTACK_STEP_HOME, 0);
            }

            if(fragTag.contains("StepDetailsFragment")) {
                getSupportFragmentManager().popBackStack(BACKSTACK_STEP_DESCRIPTION, 0);
            }

        } else {

            finish();
        }
    }

    /**
     * Only called from test, creates and returns a new {@link CustomIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new CustomIdlingResource();
        }
        return mIdlingResource;
    }
}
