package br.cassioy.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

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

    @BindView(R.id.recipe_step_ingredients_rv) RecyclerView mRecyclerViewSteps;
    @BindView(R.id.ingredient_cardview) CardView ingredientsCardView;
    @BindView(R.id.recipe_ingredients) TextView recipeIngredients;
    @BindView(R.id.ingredient_title) TextView ingredientsCardTitle;




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
        }

        ingredientsCardTitle.setText("Ingredients List");
        recipeIngredients.setText("Contrary to popular belief, Lorem Ipsum is not simply random text.");
        ingredientsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (recipeIngredients.getVisibility()){
                    case View.VISIBLE: recipeIngredients.setVisibility(View.GONE);
                    break;
                    case View.GONE: recipeIngredients.setVisibility(View.VISIBLE);
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
                Log.d("Checking Arraylist", "onItemClicked: " + mRecipeStep.get(position));
                bundleStepDetail.putInt("position", position);

                StepDetailsFragment stepDetailsFragment = new StepDetailsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                stepDetailsFragment.setArguments(bundleStepDetail);

                transaction.replace(R.id.recipe_main_fragment, stepDetailsFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }
        });
    }
}
