package br.cassioy.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;

import br.cassioy.bakingapp.model.Recipe;

import static br.cassioy.bakingapp.RecipeMainActivity.RECIPE_DATA;
import static br.cassioy.bakingapp.RecipeMainActivity.RECIPE_KEY;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link IngredientWidgetConfigureActivity IngredientWidgetConfigureActivity}
 */
public class IngredientWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, ArrayList<Recipe> recipe,
                                int appWidgetId) {

        CharSequence widgetText = IngredientWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        int widgetId = IngredientWidgetConfigureActivity.loadIndexPref(context, appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);

        Intent intent = new Intent(context, RecipeMainActivity.class);
        intent.putParcelableArrayListExtra(RECIPE_DATA, recipe);
        intent.putExtra(RECIPE_KEY, widgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch (widgetId){
            case 0: views.setImageViewResource(R.id.widget_recipe_image, R.drawable.nutella_pie);
                    views.setTextViewText(R.id.widget_recipe_name, context.getText(R.string.nutella_pie));
                    break;

            case 1: views.setImageViewResource(R.id.widget_recipe_image, R.drawable.brownies);
                views.setTextViewText(R.id.widget_recipe_name, context.getText(R.string.brownies));
                break;

            case 2: views.setImageViewResource(R.id.widget_recipe_image, R.drawable.yellowcake);
                views.setTextViewText(R.id.widget_recipe_name, context.getText(R.string.yellow_cake));
                break;

            case 3: views.setImageViewResource(R.id.widget_recipe_image, R.drawable.cheesecake);
                views.setTextViewText(R.id.widget_recipe_name, context.getText(R.string.cheesecake));
                break;

            default: break;

        }

        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.recipe_widget_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager,IngredientWidgetConfigureActivity.getRecipeArray(), appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            IngredientWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created


    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

