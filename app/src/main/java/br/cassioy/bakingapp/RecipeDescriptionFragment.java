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

import br.cassioy.bakingapp.idlingresource.CustomIdlingResource;
import br.cassioy.bakingapp.model.Ingredient;
import br.cassioy.bakingapp.support.ItemClickSupport;
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

    @BindView(R.id.recipe_step_ingredients_rv) RecyclerView mRecyclerViewSteps;
    @BindView(R.id.ingredient_cardview) CardView ingredientsCardView;
    @BindView(R.id.recipe_ingredients) TextView recipeIngredients;
    @BindView(R.id.ingredient_title) TextView ingredientsCardTitle;
    @BindView(R.id.scroll_indicator) ImageView scrollIndicator;
    @BindView(R.id.ingredient_list_view) ViewGroup ingredientLayout;

    @Nullable
    private CustomIdlingResource mIdlingResource;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingredient_step, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRecipeIngredients = bundle.getParcelableArrayList("ingredients");
            mRecipeStep = bundle.getParcelableArrayList("steps");
            recipeActionBarTitle = bundle.getString("recipe name");
        }

        ((RecipeMainActivity) getActivity()).getSupportActionBar().setTitle(recipeActionBarTitle);
        ((RecipeMainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Getting Ingredients list to a TextView
        listOfIngredients = "";
        for(Ingredient ingredients: mRecipeIngredients){
            if(!ingredients.getIngredient().isEmpty()){
                listOfIngredients += ingredients.getQuantity().toString() + " " + ingredients.getMeasure() + " of " +ingredients.getIngredient() + "\n";
                //Log.d("Ingredients list", "onViewCreated: "+ listOfIngredients);
            }
        }

        ingredientsCardTitle.setText("Ingredients List");
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

        //For every click create a bundle
        ItemClickSupport.addTo(mRecyclerViewSteps).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                Bundle bundleStepDetail = new Bundle();
                bundleStepDetail.putParcelableArrayList("step", mRecipeStep);
                //Log.d("Checking Arraylist", "onItemClicked: " + mRecipeStep.get(position));
                bundleStepDetail.putInt("position", position);
                bundleStepDetail.putString("recipe name", recipeActionBarTitle);


                StepDetailsFragment stepDetailsFragment = new StepDetailsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                stepDetailsFragment.setArguments(bundleStepDetail);

                transaction.setCustomAnimations(R.animator.trans_left_in, R.animator.trans_left_out, R.animator.trans_left_in, R.animator.trans_left_out);

                boolean tabletSize = getResources().getBoolean(R.bool.is_tablet);

                if(tabletSize){
                    transaction.replace(R.id.tablet_step_details, stepDetailsFragment);
                    transaction.addToBackStack("details");

                    // Commit the transaction
                    transaction.commit();

                }else {
                    transaction.replace(R.id.recipe_main_fragment, stepDetailsFragment);
                    transaction.addToBackStack("details");

                    // Commit the transaction
                    transaction.commit();
                }
            }
        });
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
