package br.cassioy.bakingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import br.cassioy.bakingapp.model.Ingredient;
import br.cassioy.bakingapp.model.Recipe;
import br.cassioy.bakingapp.service.RecipeService;
import br.cassioy.bakingapp.support.ItemClickSupport;
import butterknife.BindView;
import butterknife.ButterKnife;
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
    private ArrayList<Recipe> mRecipeList = new ArrayList<>();
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/";
    private String standardActionBarTitle;

    @BindView(R.id.recycler_view_main) RecyclerView mRecyclerView;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        standardActionBarTitle = getResources().getString(R.string.app_name);
        ((RecipeMainActivity) getActivity()).setActionBarTitle(standardActionBarTitle);

        return inflater.inflate(R.layout.fragment_recipe_main, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

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

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                List<Ingredient> ingredients = mRecipeList.get(position).getIngredients();
                List<Ingredient.Step> recipeStep = mRecipeList.get(position).getSteps();
                String recipeName = mRecipeList.get(position).getName();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("ingredients", (ArrayList<Ingredient>) ingredients);
                bundle.putParcelableArrayList("steps", (ArrayList<Ingredient.Step>) recipeStep);
                bundle.putString("recipe name", recipeName);

                RecipeDescriptionFragment frag = new RecipeDescriptionFragment();
                frag.setArguments(bundle);

                // Create new fragment and transaction
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.setCustomAnimations(R.animator.trans_left_in, R.animator.trans_left_out);
                transaction.replace(R.id.recipe_main_fragment, frag);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }
        });
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


