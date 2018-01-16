package br.cassioy.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cassioimamura on 1/13/18.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private ArrayList<Recipe> mRecipes;

    public RecipeAdapter(ArrayList<Recipe> recipes) {
        this.mRecipes = recipes;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class RecipeViewHolder extends RecyclerView.ViewHolder{

        final TextView recipeName;

        public RecipeViewHolder(View view) {
            super(view);
            recipeName = (TextView) view.findViewById(R.id.recipe_main_item);
        }
    }


    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list, parent,false);

        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);
        holder.recipeName.setText(recipe.getName());
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }
}
