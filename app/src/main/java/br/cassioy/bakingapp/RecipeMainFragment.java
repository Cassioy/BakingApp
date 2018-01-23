package br.cassioy.bakingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.cassioy.bakingapp.model.Recipe;
import br.cassioy.bakingapp.service.RecipeService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeMainFragment extends Fragment {

    private RecipeAdapter mRecipeAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Recipe> mRecipeList = new ArrayList<>();
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/";


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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_main);
        mRecipeAdapter = new RecipeAdapter(mRecipeList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        RecipeService recipeService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RecipeService.class);

        Log.d("RX CALL LOOKUP", "onViewCreated: " + recipeService.toString());

        recipeService.register().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError);


    }

    private void handleError(Throwable error) {
        Toast.makeText(getContext(),"Error "+ error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        Log.d("RX ERROR", "handleError: " + error.getLocalizedMessage());

    }

    private void handleResponse(List<Recipe> recipes) {
        mRecipeList = new ArrayList<>(recipes);
        mRecipeAdapter = new RecipeAdapter(mRecipeList);
        mRecyclerView.setAdapter(mRecipeAdapter);

    }
}


