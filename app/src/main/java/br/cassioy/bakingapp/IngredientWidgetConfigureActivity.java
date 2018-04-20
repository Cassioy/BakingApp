package br.cassioy.bakingapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.cassioy.bakingapp.model.Ingredient;
import br.cassioy.bakingapp.model.Recipe;
import br.cassioy.bakingapp.service.RecipeService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The configuration screen for the {@link IngredientWidget IngredientWidget} AppWidget.
 */
public class IngredientWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "br.cassioy.bakingapp.IngredientWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String PREF_ID_PREFIX_KEY = "appwidget_id_";
    private ArrayList<Recipe> mRecipeList;
    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/";
    private ArrayList<String> mRecipeNameWidget = new ArrayList<>();
    private Spinner spinner;
    private Button addButton;
    private RelativeLayout noInternetWidgetLayout;

    public HashMap recipeDictionary = new HashMap();

    public String widgetText;
    public int widgetId;



    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = IngredientWidgetConfigureActivity.this;

            // When the button is clicked, store the string and index locally
            switch (spinner.getSelectedItemPosition()){
                case 0: widgetText = getIngredientsList(0);
                        widgetId = 1;
                break;

                case 1: widgetText = getIngredientsList(1);
                        widgetId = 2;
                break;

                case 2: widgetText = getIngredientsList(2);
                        widgetId = 3;
                break;

                case 3: widgetText = getIngredientsList(3);
                        widgetId =4;
                break;

            }

            //String widgetText = mAppWidgetText.getText().toString();
            saveTitlePref(context, mAppWidgetId, widgetText);
            saveIndexPref(context, mAppWidgetId, widgetId);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            IngredientWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public IngredientWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);

        prefs.apply();
    }

    static void saveIndexPref(Context context, int appWidgetId, int id) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_ID_PREFIX_KEY + appWidgetId, id);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static int loadIndexPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int indexValue = prefs.getInt(PREF_ID_PREFIX_KEY + appWidgetId, 0);
        return indexValue;
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_ID_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);


        recipeDictionary.put("CUP", "cup(s)");
        recipeDictionary.put("TBLSP", "tablespoon");
        recipeDictionary.put("TSP", "teaspoon");
        recipeDictionary.put("K", "kilogram(s)");
        recipeDictionary.put("G", "gram(s)");
        recipeDictionary.put("OZ", "ounce(s)");
        recipeDictionary.put("UNIT", "unit(s)");

        RecipeService recipeService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(RecipeService.class);

        //Log.d("RX CALL LOOKUP", "onViewCreated: " + recipeService.toString());

        recipeService.register().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError);

        setContentView(R.layout.ingredient_widget_configure);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        //mAppWidgetText.setText(loadTitlePref(IngredientWidgetConfigureActivity.this, mAppWidgetId));


    }
    private void handleError(Throwable error) {

        if(!isInternetOn()){
            Toast.makeText(this,getResources().getString(R.string.no_internet_alert), Toast.LENGTH_SHORT).show();
            noInternetWidgetLayout = (RelativeLayout) findViewById(R.id.no_internet_layout_widget);
            noInternetWidgetLayout.setVisibility(View.VISIBLE);
        }else{

            Toast.makeText(this,"Error "+ error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleResponse(List<Recipe> recipes) {

        noInternetWidgetLayout = (RelativeLayout) findViewById(R.id.no_internet_layout_widget);
        noInternetWidgetLayout.setVisibility(View.GONE);

        mRecipeList = new ArrayList<>(recipes);

            for(Recipe recipe: mRecipeList) {
                mRecipeNameWidget.add(recipe.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,mRecipeNameWidget);

        spinner = (Spinner)findViewById(R.id.appwidget_spinner_config);
        addButton = (Button)findViewById(R.id.add_button);

        spinner.setAdapter(adapter);
        addButton.setOnClickListener(mOnClickListener);

    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = connec.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private String getIngredientsList(int position){

        String listOfIngredients = "";

        List<Ingredient> ingredient = mRecipeList.get(position).getIngredients();

        for(Ingredient ingredients: ingredient) {

            if (!ingredients.getIngredient().isEmpty()) {
                listOfIngredients += ingredients.getQuantity()
                        .toString() + " " + recipeDictionary
                        .get(ingredients.getMeasure()) + " of " + ingredients
                        .getIngredient() + "\n";
            }
        }

        return listOfIngredients;
    }

}



