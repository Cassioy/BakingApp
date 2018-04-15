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

import br.cassioy.bakingapp.idlingresource.CustomIdlingResource;

public class RecipeMainActivity extends AppCompatActivity {

    @Nullable
    private CustomIdlingResource mIdlingResource;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.recipe_toolbar);
        setSupportActionBar(toolbar);

        ActionBar a = getSupportActionBar();

        a.setDisplayHomeAsUpEnabled(false);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.recipe_main_fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            RecipeMainFragment firstFragment = new RecipeMainFragment();


            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().addToBackStack("home")
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
                getSupportFragmentManager().popBackStack("home", 0);
            }

            if(fragTag.contains("StepDetailsFragment")){
                    getSupportFragmentManager().popBackStack("description", 0);
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
                getSupportFragmentManager().popBackStack("home", 0);
            }

            if(fragTag.contains("StepDetailsFragment")) {
                getSupportFragmentManager().popBackStack("description", 0);
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
