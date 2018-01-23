package br.cassioy.bakingapp.service;

import java.util.List;

import br.cassioy.bakingapp.model.Recipe;
import io.reactivex.Observable;
import retrofit2.http.GET;



/**
 * Created by cassioimamura on 1/22/18.
 */

public interface RecipeService {

    @GET("2017/May/59121517_baking/baking.json")
    Observable<List<Recipe>> register();

}
