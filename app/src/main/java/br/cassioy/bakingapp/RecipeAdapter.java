package br.cassioy.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.cassioy.bakingapp.model.Recipe;
import butterknife.BindView;
import butterknife.ButterKnife;

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

        @BindView(R.id.recipe_main_item) TextView recipeName;
        @BindView(R.id.recipe_card_image) ImageView recipeCardImage;


        public RecipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list, parent,false);

        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.recipeName.setText(mRecipes.get(position).getName());

        //set imageview drawable based on recipe name
        switch (mRecipes.get(position).getName()){
            case "Nutella Pie":
                holder.recipeCardImage.setImageResource(R.drawable.nutella_pie);
                break;

            case "Brownies":
                holder.recipeCardImage.setImageResource(R.drawable.brownies);
                break;

            case "Yellow Cake":
                holder.recipeCardImage.setImageResource(R.drawable.yellowcake);
                break;

            case "Cheesecake":
                holder.recipeCardImage.setImageResource(R.drawable.cheesecake);
                break;

            default: break;
        }
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }
}
