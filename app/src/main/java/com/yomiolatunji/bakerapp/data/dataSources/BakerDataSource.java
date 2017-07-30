package com.yomiolatunji.bakerapp.data.dataSources;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.SimpleIdlingResource;
import com.yomiolatunji.bakerapp.data.DataLoadingCallback;
import com.yomiolatunji.bakerapp.data.NetworkUtils;
import com.yomiolatunji.bakerapp.data.entities.Recipe;
import com.yomiolatunji.bakerapp.data.entities.RecipeIngredient;
import com.yomiolatunji.bakerapp.data.entities.RecipeStep;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract.IngredientsEntry;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract.RecipeEntry;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract.StepEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.BASE_URI;
import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.PATH_INGREDIENTS;
import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.PATH_RECIPES;
import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.PATH_STEPS;

/**
 * Created by oluwayomi on 24/06/2017.
 */

public class BakerDataSource implements LoaderManager.LoaderCallbacks<List<Recipe>> {
    private static final int ID_RECIPE_LOADER = 536;
    private static final int ID_OFFLINE_RECIPE_LOADER = 345;
    private static final int ID_OFFLINE_STEP_LOADER = 346;
    private static final int ID_OFFLINE_INGREDIENT_LOADER = 347;
    private String Url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private AppCompatActivity mContext;
    private DataLoadingCallback<List<Recipe>> loadingCallback;
    @Nullable
    private SimpleIdlingResource idlingResource;
    LoaderManager.LoaderCallbacks<Cursor> offlineLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader;
            Uri RECIPE_URI = BASE_URI.buildUpon().appendPath(PATH_RECIPES).build();
            cursorLoader = new CursorLoader(mContext, RECIPE_URI, null,
                    null, null, null);
//            switch (id) {
//                case ID_OFFLINE_RECIPE_LOADER:
//                    break;
//                case ID_OFFLINE_INGREDIENT_LOADER:
//                    Uri INGREDIENT_URI = BASE_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();
//                    cursorLoader = new CursorLoader(mContext, INGREDIENT_URI, null,
//                            null, null, null);
//                    break;
//                case ID_OFFLINE_STEP_LOADER:
//                    Uri STEP_URI = BASE_URI.buildUpon().appendPath(PATH_STEPS).build();
//                    cursorLoader = new CursorLoader(mContext, STEP_URI, null,
//                            null, null, null);
//                    break;
//                default:
//                    throw new UnsupportedOperationException("Unknown id: " + String.valueOf(id));
//            }
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            List<Recipe> data = new ArrayList<>();

//            cursor.moveToFirst();
//            if (cursor.getColumnIndex(RecipeEntry.COLUMN_RECIPE_NAME) >= 0 && cursor.getColumnIndex(RecipeEntry.COLUMN_RECIPE_SERVINGS) >= 0) {
                getRecipeFromCursor(cursor, data);
//            } else if (cursor.getColumnIndex(StepEntry.COLUMN_DESCRIPTION) >= 0 && cursor.getColumnIndex(StepEntry.COLUMN_SHORT_DESCRIPTION) >= 0) {
//                getStepFromCursor(cursor, data);
//            } else if (cursor.getColumnIndex(IngredientsEntry.COLUMN_INGREDIENT) >= 0 && cursor.getColumnIndex(IngredientsEntry.COLUMN_MEASURE) >= 0) {
//                getIngredientFromCursor(cursor, data);
//            }
            loadingCallback.onResponse(data);

            if (idlingResource != null) {
                idlingResource.setIdleState(true);
            }
        }

        private void getIngredientFromCursor(Cursor cursor, List<Recipe> recipes) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                RecipeIngredient ingredient = new RecipeIngredient();

                ingredient.setIngredient(cursor.getString(cursor.getColumnIndex(IngredientsEntry.COLUMN_INGREDIENT)));
                ingredient.setMeasure(cursor.getString(cursor.getColumnIndex(IngredientsEntry.COLUMN_MEASURE)));
                ingredient.setQuantity(cursor.getInt(cursor.getColumnIndex(IngredientsEntry.COLUMN_QUANTITY)));
                ingredient.setRecipeId(cursor.getInt(cursor.getColumnIndex(IngredientsEntry.COLUMN_RECIPE_ID)));
                for (int j = 0; j < recipes.size(); j++) {
                    if (ingredient.getRecipeId() == recipes.get(j).getId())
                        recipes.get(j).getIngredients().add(ingredient);
                }
            }
        }

        private void getStepFromCursor(Cursor cursor, List<Recipe> recipes) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                RecipeStep step = new RecipeStep();

                step.setDescription(cursor.getString(cursor.getColumnIndex(StepEntry.COLUMN_DESCRIPTION)));
                step.setShortDescription(cursor.getString(cursor.getColumnIndex(StepEntry.COLUMN_SHORT_DESCRIPTION)));
                step.setThumbnailUrl(cursor.getString(cursor.getColumnIndex(StepEntry.COLUMN_THUMBNAIL_URL)));
                step.setVideoUrl(cursor.getString(cursor.getColumnIndex(StepEntry.COLUMN_VIDEO_URL)));
                step.setId(cursor.getInt(cursor.getColumnIndex(StepEntry._ID)));
                step.setRecipeId(cursor.getInt(cursor.getColumnIndex(StepEntry.COLUMN_RECIPE_ID)));
                for (int j = 0; j < recipes.size(); j++) {
                    if (step.getRecipeId() == recipes.get(j).getId())
                        recipes.get(j).getRecipeSteps().add(step);
                }
            }
        }

        private void getRecipeFromCursor(Cursor cursor, List<Recipe> data) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                Recipe recipe = new Recipe();

                recipe.setImage(cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_RECIPE_IMAGE)));
                recipe.setName(cursor.getString(cursor.getColumnIndex(RecipeEntry.COLUMN_RECIPE_NAME)));
                recipe.setId(cursor.getInt(cursor.getColumnIndex(RecipeEntry._ID)));
                recipe.setServings(cursor.getInt(cursor.getColumnIndex(RecipeEntry.COLUMN_RECIPE_SERVINGS)));
                data.add(recipe);
            }
//            mContext.getSupportLoaderManager().initLoader(ID_OFFLINE_INGREDIENT_LOADER, null, offlineLoaderCallbacks);
//            mContext.getSupportLoaderManager().initLoader(ID_OFFLINE_STEP_LOADER, null, offlineLoaderCallbacks);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    public BakerDataSource(AppCompatActivity context, DataLoadingCallback<List<Recipe>> loadingCallback) {

        mContext = context;
        this.loadingCallback = loadingCallback;

    }

    public void getData(@Nullable final SimpleIdlingResource idlingResource) {
        this.idlingResource = idlingResource;
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }
        mContext.getSupportLoaderManager().initLoader(ID_RECIPE_LOADER, null, this);
    }

    public void getOfflineData(@Nullable final SimpleIdlingResource idlingResource) {
        this.idlingResource = idlingResource;
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }
        mContext.getSupportLoaderManager().initLoader(ID_OFFLINE_RECIPE_LOADER, null, offlineLoaderCallbacks);
    }

    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Recipe>>(mContext) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public List<Recipe> loadInBackground() {
                List<Recipe> recipeList = new ArrayList<>();
                try {
                    String response = NetworkUtils.newInstance(mContext).get(Url);
                    JSONArray recipeJsonArray = new JSONArray(response);
                    if (recipeJsonArray.length() == 0) {
                        loadingCallback.onFailure(mContext.getString(R.string.empty_list));
                        return null;
                    }
                    for (int i = 0; i < recipeJsonArray.length(); i++) {
                        JSONObject recipeObject = recipeJsonArray.getJSONObject(i);
                        Recipe recipe = new Recipe();
                        if (recipeObject.has("id"))
                            recipe.setId(recipeObject.getInt("id"));
                        if (recipeObject.has("name"))
                            recipe.setName(recipeObject.getString("name"));

                        if (recipeObject.has("ingredients")) {
                            JSONArray ingredientArray = recipeObject.getJSONArray("ingredients");
                            List<RecipeIngredient> ingredients = new ArrayList<>();
                            for (int j = 0; j < ingredientArray.length(); j++) {
                                RecipeIngredient ingredient = new RecipeIngredient();
                                JSONObject ingredientObject = ingredientArray.getJSONObject(j);
                                if (ingredientObject.has("quantity"))
                                    ingredient.setQuantity(ingredientObject.getInt("quantity"));
                                if (ingredientObject.has("measure"))
                                    ingredient.setMeasure(ingredientObject.getString("measure"));
                                if (ingredientObject.has("ingredient"))
                                    ingredient.setIngredient(ingredientObject.getString("ingredient"));

                                ingredients.add(ingredient);
                            }
                            recipe.setIngredients(ingredients);
                        }
                        if (recipeObject.has("steps")) {
                            JSONArray stepArray = recipeObject.getJSONArray("steps");
                            List<RecipeStep> steps = new ArrayList<>();
                            for (int j = 0; j < stepArray.length(); j++) {
                                RecipeStep step = new RecipeStep();
                                JSONObject stepObject = stepArray.getJSONObject(j);
                                if (stepObject.has("id"))
                                    step.setId(stepObject.getInt("id"));
                                if (stepObject.has("shortDescription"))
                                    step.setShortDescription(stepObject.getString("shortDescription"));
                                if (stepObject.has("description"))
                                    step.setDescription(stepObject.getString("description"));
                                if (stepObject.has("videoURL"))
                                    step.setVideoUrl(stepObject.getString("videoURL"));
                                if (stepObject.has("thumbnailURL"))
                                    step.setThumbnailUrl(stepObject.getString("thumbnailURL"));

                                steps.add(step);
                            }
                            recipe.setRecipeSteps(steps);
                        }
                        if (recipeObject.has("servings"))
                            recipe.setServings(recipeObject.getInt("servings"));
                        if (recipeObject.has("image"))
                            recipe.setImage(recipeObject.getString("image"));

                        recipeList.add(recipe);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return recipeList;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Recipe>> loader, List<Recipe> data) {

        swapOfflineData(data);
        loadingCallback.onResponse(data);
        if (idlingResource != null) {
            idlingResource.setIdleState(true);
        }
    }

    private void swapOfflineData(List<Recipe> data) {
        Uri RecipeUri = RecipeContract.BASE_URI.buildUpon().appendPath(PATH_RECIPES).build();
        Uri StepUri = RecipeContract.BASE_URI.buildUpon().appendPath(PATH_STEPS).build();
        Uri IngredientUri = RecipeContract.BASE_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();
//        mContext.getContentResolver().delete(RecipeUri, null, null);
//        mContext.getContentResolver().delete(StepUri, null, null);
//        mContext.getContentResolver().delete(IngredientUri, null, null);
        Cursor cursor = mContext.getContentResolver().query(RecipeUri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0)
            for (Recipe recipe :
                    data) {
                int recipeId = insertRecipe(RecipeUri, recipe);

                insertSteps(StepUri, recipe, recipeId);
                insertIngredients(IngredientUri, recipe, recipeId);

            }
        cursor.close();
    }

    private void insertIngredients(Uri ingredientUri, Recipe recipe, int recipeId) {
        for (RecipeIngredient ingredient :
                recipe.getIngredients()) {

            ContentValues cv = new ContentValues();
            cv.put(IngredientsEntry.COLUMN_INGREDIENT, ingredient.getIngredient());
            cv.put(IngredientsEntry.COLUMN_MEASURE, ingredient.getMeasure());
            cv.put(IngredientsEntry.COLUMN_QUANTITY, ingredient.getQuantity());
            cv.put(IngredientsEntry.COLUMN_RECIPE_ID, recipeId);
            mContext.getContentResolver().insert(ingredientUri, cv);
        }
    }

    private void insertSteps(Uri stepUri, Recipe recipe, int recipeId) {
        for (RecipeStep step :
                recipe.getRecipeSteps()) {

            ContentValues cv = new ContentValues();
            cv.put(StepEntry.COLUMN_DESCRIPTION, step.getDescription());
            cv.put(StepEntry.COLUMN_NUMBER, step.getId());
            cv.put(StepEntry.COLUMN_SHORT_DESCRIPTION, step.getShortDescription());
            cv.put(StepEntry.COLUMN_VIDEO_URL, step.getVideoUrl());
            cv.put(StepEntry.COLUMN_THUMBNAIL_URL, step.getThumbnailUrl());
            cv.put(StepEntry.COLUMN_RECIPE_ID, recipeId);
            mContext.getContentResolver().insert(stepUri, cv);
        }
    }

    private int insertRecipe(Uri recipeUri, Recipe recipe) {
        ContentValues cv = new ContentValues();
        cv.put(RecipeEntry.COLUMN_RECIPE_NAME, recipe.getName());
        cv.put(RecipeEntry.COLUMN_RECIPE_IMAGE, recipe.getImage());
        cv.put(RecipeEntry.COLUMN_RECIPE_SERVINGS, recipe.getServings());
        Uri retUri = mContext.getContentResolver().insert(recipeUri, cv);
        return Integer.parseInt(retUri.getPathSegments().get(1));
    }

    @Override
    public void onLoaderReset(Loader<List<Recipe>> loader) {

    }
}
