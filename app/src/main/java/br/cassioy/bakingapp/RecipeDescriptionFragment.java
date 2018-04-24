package br.cassioy.bakingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import br.cassioy.bakingapp.idlingresource.CustomIdlingResource;
import br.cassioy.bakingapp.model.Ingredient;
import br.cassioy.bakingapp.support.ItemClickSupport;
import br.cassioy.bakingapp.utils.DictionaryUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cassioimamura on 1/29/18.
 */

public class RecipeDescriptionFragment extends Fragment {

    private RecipeDescriptionAdapter mRecipeDescriptionAdapter;
    private ArrayList<Ingredient.Step> mRecipeStep = new ArrayList<>();
    private ArrayList<Ingredient> mRecipeIngredients = new ArrayList<>();
    private String listOfIngredients;
    private String recipeActionBarTitle;
    private boolean fragmentInstatiated;
    private boolean tabletSize;

    private static final String BUNDLE_INGREDIENTS = "br.cassioy.bakingapp.bundle_ingredients";
    private static final String BUNDLE_STEP = "br.cassioy.bakingapp.bundle_step";
    private static final String BUNDLE_NAME = "br.cassioy.bakingapp.bundle_name";
    private static final String BUNDLE_POSITION = "br.cassioy.bakingapp.bundle_position";

    private static final String BUNDLE_FRAGMENT_INSTANTIATE = "br.cassioy.bakingapp.bundle_fragment_boolean";


    private static final String BACKSTACK_STEP_DETAILS = "br.cassioy.bakingapp.backstack_details";


    @BindView(R.id.recipe_step_ingredients_rv) RecyclerView mRecyclerViewSteps;
    @BindView(R.id.ingredient_cardview) CardView ingredientsCardView;
    @BindView(R.id.recipe_ingredients) TextView recipeIngredients;
    @BindView(R.id.ingredient_title) TextView ingredientsCardTitle;
    @BindView(R.id.scroll_indicator) ImageView scrollIndicator;

    @Nullable
    @BindView(R.id.empty_recipe_details) TextView emptyRecipeDescription;

    @Nullable
    private CustomIdlingResource mIdlingResource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingredient_step, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this, view);

        if(savedInstanceState != null) {

            mRecipeStep = savedInstanceState.getParcelableArrayList(BUNDLE_STEP);
            mRecipeIngredients = savedInstanceState.getParcelableArrayList(BUNDLE_INGREDIENTS);
            recipeActionBarTitle = savedInstanceState.getString(BUNDLE_NAME);
            fragmentInstatiated = savedInstanceState.getBoolean(BUNDLE_FRAGMENT_INSTANTIATE);

        }else{

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                mRecipeIngredients = bundle.getParcelableArrayList(BUNDLE_INGREDIENTS);
                mRecipeStep = bundle.getParcelableArrayList(BUNDLE_STEP);
                recipeActionBarTitle = bundle.getString(BUNDLE_NAME);
            }
        }

        ((RecipeMainActivity) getActivity()).getSupportActionBar().setTitle(recipeActionBarTitle);
        ((RecipeMainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Dictionary for ingredients measures
        HashMap recipeDictionary = new HashMap();
        recipeDictionary = DictionaryUtils.parseStringArray(getContext(), R.array.recipe_measures_array);


        //Getting Ingredients list to a TextView
        listOfIngredients = "";
        for(Ingredient ingredients: mRecipeIngredients){
            if(!ingredients.getIngredient().isEmpty()){
                listOfIngredients += ingredients.getQuantity().toString() + " " + recipeDictionary.get(ingredients.getMeasure()) + " of " +ingredients.getIngredient() + "\n";
                //Log.d("Ingredients list", "onViewCreated: "+ listOfIngredients);
            }
        }

        ingredientsCardTitle.setText(getResources().getString(R.string.appwidget_text));
        recipeIngredients.setText(listOfIngredients);


        ingredientsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (recipeIngredients.getVisibility()){
                    case View.VISIBLE:  recipeIngredients.setVisibility(View.GONE);
                                        scrollIndicator.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down));
                                        break;

                    case View.GONE: recipeIngredients.setVisibility(View.VISIBLE);
                                    scrollIndicator.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up));
                                    break;
                }

            }
        });

        mRecipeDescriptionAdapter = new RecipeDescriptionAdapter(mRecipeIngredients, mRecipeStep);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getContext().getApplicationContext());
        mRecyclerViewSteps.setLayoutManager(mLayoutManager2);

        mRecyclerViewSteps.setAdapter(mRecipeDescriptionAdapter);

        //keep Actual state
        if(fragmentInstatiated){
            emptyRecipeDescription.setVisibility(View.GONE);
        }


        //For every click create a bundle
        ItemClickSupport.addTo(mRecyclerViewSteps).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                Bundle bundleStepDetail = new Bundle();
                bundleStepDetail.putParcelableArrayList(BUNDLE_STEP, mRecipeStep);
                bundleStepDetail.putInt(BUNDLE_POSITION, position);
                bundleStepDetail.putString(BUNDLE_NAME, recipeActionBarTitle);


                StepDetailsFragment stepDetailsFragment = new StepDetailsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                stepDetailsFragment.setArguments(bundleStepDetail);

                transaction.setCustomAnimations(R.animator.trans_left_in, R.animator.trans_left_out, R.animator.trans_left_in, R.animator.trans_left_out);

                tabletSize = getResources().getBoolean(R.bool.is_tablet);

                if(tabletSize){

                    emptyRecipeDescription.setVisibility(View.GONE);

                    transaction.replace(R.id.tablet_step_details, stepDetailsFragment);
                    transaction.addToBackStack(BACKSTACK_STEP_DETAILS);

                    // Commit the transaction
                    transaction.commit();

                    fragmentInstatiated = true;

                }else {
                    transaction.replace(R.id.recipe_main_fragment, stepDetailsFragment);
                    transaction.addToBackStack(BACKSTACK_STEP_DETAILS);

                    // Commit the transaction
                    transaction.commit();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(BUNDLE_INGREDIENTS, (ArrayList<Ingredient>) mRecipeIngredients);
        outState.putParcelableArrayList(BUNDLE_STEP, (ArrayList<Ingredient.Step>) mRecipeStep);
        outState.putString(BUNDLE_NAME, recipeActionBarTitle);
        outState.putBoolean(BUNDLE_FRAGMENT_INSTANTIATE, fragmentInstatiated);

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
