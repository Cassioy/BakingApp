package br.cassioy.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.cassioy.bakingapp.model.Ingredient;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cassioimamura on 1/29/18.
 */

public class RecipeDescriptionAdapter extends RecyclerView.Adapter<RecipeDescriptionAdapter.RecipeDescriptionViewHolder> {

    private ArrayList<Ingredient> mIngredients;
    private ArrayList<Ingredient.Step> mRecipeStep;


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class RecipeDescriptionViewHolder extends RecyclerView.ViewHolder{

        //@BindView(R.id.ingredients_list) TextView ingredientsName;
        @BindView(R.id.steps_list_item) TextView stepsName;
        @BindView(R.id.videoplayer_icon) ImageView videoPlayer;


        public RecipeDescriptionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

    public RecipeDescriptionAdapter(ArrayList<Ingredient> ingredientDescription, ArrayList<Ingredient.Step> stepDescription) {
        this.mIngredients = ingredientDescription;
        this.mRecipeStep = stepDescription;
    }


    @Override
    public RecipeDescriptionAdapter.RecipeDescriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ingredients_step_list, parent,false);

        return new RecipeDescriptionAdapter.RecipeDescriptionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeDescriptionViewHolder holder, int position) {
        //holder.ingredientsName.setText(mIngredients.toString());
        if(!mRecipeStep.get(position).getVideoURL().isEmpty()){
            holder.videoPlayer.setVisibility(View.VISIBLE);
        }
        holder.stepsName.setText(position + ". "  + mRecipeStep.get(position).getShortDescription());

    }

    @Override
    public int getItemCount() {
        return mRecipeStep.size();
    }
}
