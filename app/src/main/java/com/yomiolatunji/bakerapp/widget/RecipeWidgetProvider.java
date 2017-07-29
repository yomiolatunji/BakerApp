package com.yomiolatunji.bakerapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.StringBuilderPrinter;
import android.widget.RemoteViews;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.entities.RecipeIngredient;
import com.yomiolatunji.bakerapp.ui.MainActivity;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, List<String> ingredients, String recipeName) {

        // Construct the RemoteViews object
        //RemoteViews views = getRecipeRemoteViews(context, recipeId);
        StringBuilder stringBuilder=new StringBuilder();
        for (String ingredient :
                ingredients) {
            stringBuilder.append(ingredient);
            stringBuilder.append("\n\r");
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);

        views.setTextViewText(R.id.widget_recipe_title,recipeName);
        views.setTextViewText(R.id.widget_recipe_ingredient, stringBuilder.toString());
        //new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widget_recipe_ingredient, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                        int[] appWidgetIds, List<String> ingredients, String recipeName) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,ingredients,recipeName);
        }
    }

    public static RemoteViews getRecipeRemoteViews(Context context,int recipeId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);

//        Intent intent = new Intent(context, LinearWidgetService.class);
//        intent.putExtra(LinearWidgetService.KEY_RECIPE_ID,recipeId);
//        views.setRemoteAdapter(R.id.widget_recipe_list, intent);
//
//        Intent appIntent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        views.setPendingIntentTemplate(R.id.widget_recipe_list, pendingIntent);

        return views;

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
        RecipeWidgetService.startActionUpdateRecipeWidgets(context);
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

