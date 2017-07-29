package com.yomiolatunji.bakerapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.entities.RecipeIngredient;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract;

import java.util.ArrayList;
import java.util.List;

import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.BASE_URI;
import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.PATH_INGREDIENTS;
import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.PATH_RECIPES;

/**
 * Created by oluwayomi on 23/07/2017.
 */

public class RecipeWidgetService extends IntentService {
    public static final String ACTION_UPDATE_RECIPE_WIDGET = "com.yomiolatunji.bakerapp.action.update_recipe_widgets";
    private static final String TAG = "RecipeWidgetService";

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
        Cursor cursor = getContentResolver().query(recipeUri, null, null, null, null);
        int recipeId = 0;
        String recipeName = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            recipeId = cursor.getInt(cursor.getColumnIndex(RecipeContract.RecipeEntry._ID));
            recipeName = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
            Log.i(TAG, "cursor.getCount: " + cursor.getCount());
            cursor.close();
        }
        //List<RecipeIngredient> ingredients = new ArrayList<>();
        List<String> ingredientStrings=new ArrayList<>();
        if (recipeId > 0) {

            Uri ingredientUri = BASE_URI.buildUpon().appendPath(PATH_INGREDIENTS).appendPath("recipe").appendPath(String.valueOf(recipeId)).build();
            Cursor ingCursor = getContentResolver().query(ingredientUri, null, null, null, null);
            for (int i = 0; i < cursor.getCount(); i++) {
                ingCursor.moveToPosition(i);
                RecipeIngredient ingredient = new RecipeIngredient();
                ingredient.setIngredient(ingCursor.getString(ingCursor.getColumnIndex(RecipeContract.IngredientsEntry.COLUMN_INGREDIENT)));
                ingredient.setQuantity(ingCursor.getInt(ingCursor.getColumnIndex(RecipeContract.IngredientsEntry.COLUMN_QUANTITY)));
                ingredient.setMeasure(ingCursor.getString(ingCursor.getColumnIndex(RecipeContract.IngredientsEntry.COLUMN_MEASURE)));
                //ingredients.add(ingredient);
                String ing=(i+1)+". "+ingredient.getIngredient()+"("+ingredient.getQuantity()+" "+ingredient.getMeasure()+")";
                ingredientStrings.add(ing);
            }

        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));

        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_recipe_list);

        RecipeWidgetProvider.updateAppWidgets(this, appWidgetManager, appWidgetIds, ingredientStrings, recipeName);
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
