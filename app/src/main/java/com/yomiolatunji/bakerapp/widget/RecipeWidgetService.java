package com.yomiolatunji.bakerapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

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

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
        SharedPreferences sharedPreferences = getSharedPreferences
                (getString(R.string.recipe_key), Context.MODE_PRIVATE);

        for (int appWidgetId : appWidgetIds) {
            int recipeId = sharedPreferences.getInt(String.valueOf(appWidgetId), 0);
            Uri recipeUri = BASE_URI.buildUpon().appendPath(PATH_RECIPES).appendPath(String.valueOf(recipeId)).build();
            Cursor cursor = getContentResolver().query(recipeUri, null, null, null, null);

            String recipeName = null;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                //recipeId = cursor.getInt(cursor.getColumnIndex(RecipeContract.RecipeEntry._ID));
                recipeName = cursor.getString(cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
                //Log.i(TAG, "cursor.getCount: " + cursor.getCount());
                cursor.close();
            }
            //List<RecipeIngredient> ingredients = new ArrayList<>();
            List<String> ingredientStrings = new ArrayList<>();
            if (recipeId > 0) {

                Uri ingredientUri = BASE_URI.buildUpon().appendPath(PATH_INGREDIENTS).appendPath("recipe").appendPath(String.valueOf(recipeId)).build();
                Cursor ingCursor = getContentResolver().query(ingredientUri, null, null, null, null);
                if(ingCursor!=null)
                for (int i = 0; i < ingCursor.getCount(); i++) {
                    ingCursor.moveToPosition(i);
                    RecipeIngredient ingredient = new RecipeIngredient();
                    ingredient.setIngredient(ingCursor.getString(ingCursor.getColumnIndex(RecipeContract.IngredientsEntry.COLUMN_INGREDIENT)));
                    ingredient.setQuantity(ingCursor.getInt(ingCursor.getColumnIndex(RecipeContract.IngredientsEntry.COLUMN_QUANTITY)));
                    ingredient.setMeasure(ingCursor.getString(ingCursor.getColumnIndex(RecipeContract.IngredientsEntry.COLUMN_MEASURE)));
                    //ingredients.add(ingredient);
                    String ing = (i + 1) + ". " + ingredient.getIngredient() + "(" + ingredient.getQuantity() + " " + ingredient.getMeasure() + ")";
                    ingredientStrings.add(ing);
                }
                ingCursor.close();
            }

            RecipeWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId, ingredientStrings, recipeName);
        }

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
