package com.yomiolatunji.bakerapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.yomiolatunji.bakerapp.R;

import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.BASE_URI;
import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.PATH_RECIPES;

/**
 * Created by oluwayomi on 23/07/2017.
 */

public class RecipeWidgetService extends IntentService {
    public static final String ACTION_UPDATE_RECIPE_WIDGET = "com.yomiolatunji.bakerapp.action.update_recipe_widgets";

    public RecipeWidgetService() {
        super("RecipeWidgetService");
    }

    public static void startActionUpdateRecipeWidgets(Context context) {
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.setAction(ACTION_UPDATE_RECIPE_WIDGET);
        context.startService(intent);
    }

    public void handleUpdateRecipeWidgets() {
        Uri recipeUri = BASE_URI.buildUpon().appendPath(PATH_RECIPES).build();
        //Cursor cursor = getContentResolver().query(recipeUri, null, null, null, null);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_recipe_list);

        RecipeWidgetProvider.updateAppWidgets(this, appWidgetManager, appWidgetIds);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_UPDATE_RECIPE_WIDGET.equals(action)) {
                handleUpdateRecipeWidgets();
            }
        }
    }
}
