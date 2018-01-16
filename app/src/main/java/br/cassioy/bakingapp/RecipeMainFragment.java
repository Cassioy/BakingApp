package br.cassioy.bakingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeMainFragment extends Fragment {

    private RecipeAdapter mRecipeAdapter;
    private ArrayList<Recipe> recipeList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_main, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_main);

        mRecipeAdapter = new RecipeAdapter(recipeList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        Recipe recipe1 = new Recipe("Nutella Pie");
        Recipe recipe2 = new Recipe("Brownies");
        Recipe recipe3 = new Recipe("Yellow Cake");
        Recipe recipe4 = new Recipe("Cheesecake");

        recipeList.add(recipe1);
        recipeList.add(recipe2);
        recipeList.add(recipe3);
        recipeList.add(recipe4);

        mRecipeAdapter.notifyDataSetChanged();


        mRecyclerView.setAdapter(mRecipeAdapter);

    }
}


