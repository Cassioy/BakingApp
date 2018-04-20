package br.cassioy.bakingapp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.cassioy.bakingapp.idlingresource.CustomIdlingResource;
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

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeMainFragment extends Fragment {

    private RecipeAdapter mRecipeAdapter;
    private ArrayList<Recipe> mRecipeList;

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/";
    private static final String RECIPE_LIST = "recipeList";

    private String standardActionBarTitle;
    private List<Ingredient> ingredients;
    private List<Ingredient.Step> recipeStep;
    private String recipeName;

    @BindView(R.id.recycler_view_main) RecyclerView mRecyclerView;
    @BindView(R.id.no_internet_layout) View noInternetLayout;
    @BindView(R.id.no_internet_textview) TextView noInternetTextView;
    @BindView(R.id.retry_button) Button noInternetRetryButton;

    @Nullable
    private CustomIdlingResource mIdlingResource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //set title and navigation on ActionBar
        standardActionBarTitle = getResources().getString(R.string.app_name);
        ((RecipeMainActivity) getActivity()).getSupportActionBar().setTitle(standardActionBarTitle);
        ((RecipeMainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_main, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        if(savedInstanceState != null) {
            mRecipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST);
            mRecipeAdapter = new RecipeAdapter(mRecipeList);
            mRecyclerView.setAdapter(mRecipeAdapter);
        }

        boolean tabletSize = getResources().getBoolean(R.bool.is_tablet);

        if(savedInstanceState == null){

            //Asynchronous request Rx
            RecipeService recipeService = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(RecipeService.class);


            recipeService.register().observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::handleResponse,this::handleError);
        }

        //Check if it's Tablet or Phone layout
        if (tabletSize) {
            int n = 1;
            int orientation = getResources().getConfiguration().orientation;

            switch (orientation){
                case ORIENTATION_LANDSCAPE: n = 3;
                break;

                case ORIENTATION_PORTRAIT: n = 2;
                break;

                default: break;
            }

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext().getApplicationContext(), n);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            // do something else
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                ingredients = mRecipeList.get(position).getIngredients();
                recipeStep = mRecipeList.get(position).getSteps();
                recipeName = mRecipeList.get(position).getName();

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
                transaction.setCustomAnimations(R.animator.trans_left_in, R.animator.trans_left_out, R.animator.trans_left_in, R.animator.trans_left_out);
                transaction.replace(R.id.recipe_main_fragment, frag);
                transaction.addToBackStack("description");

                // Commit the transaction
                transaction.commit();

            }
        });

        setRetainInstance( true );
    }

    private void handleError(Throwable error) {

        if(!isInternetOn()){
            noInternetLayout.setVisibility(View.VISIBLE);
            noInternetRetryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RecipeMainFragment firstFragment = new RecipeMainFragment();
                    // Add the fragment to the 'fragment_container' FrameLayout
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.recipe_main_fragment, firstFragment).commit();
                    noInternetLayout.setVisibility(View.GONE);
                }
            });
        }else{

            Toast.makeText(getContext(),"Error "+ error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleResponse(List<Recipe> recipes) {
        mRecipeList = new ArrayList<>(recipes);
        mRecipeAdapter = new RecipeAdapter(mRecipeList);
        mRecyclerView.setAdapter(mRecipeAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList(RECIPE_LIST, mRecipeList);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = connec.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
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


