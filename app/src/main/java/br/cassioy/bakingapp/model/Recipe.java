package br.cassioy.bakingapp.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cassioimamura on 1/13/18.
 */

public class Recipe {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("ingredients")
    @Expose
    private List<Ingredient> ingredients = null;
    @SerializedName("steps")
    @Expose
    private List<Ingredient.Step> steps = null;
    @SerializedName("servings")
    @Expose
    private Double servings;
    @SerializedName("image")
    @Expose
    private String image;

    public Recipe() {};

    public Recipe(String recipeName){
        this.name = recipeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Ingredient.Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Ingredient.Step> steps) {
        this.steps = steps;
    }

    public Double getServings() {
        return servings;
    }

    public void setServings(Double servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private int lastRecipeId = 0;



//    public static ArrayList<Recipe> createRecipesList(int numRecipes) {
//        int lastRecipeId = 0;
//
//        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
//
//        for (int i = 1; i <= numRecipes; i++) {
           //TODO Create Recipe Constructor
//            recipes.add(new Recipe());
//            lastRecipeId++;
//        }
//
//        return recipes;
//    }
}
