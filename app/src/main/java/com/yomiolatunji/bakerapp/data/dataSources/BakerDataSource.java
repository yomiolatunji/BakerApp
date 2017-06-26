package com.yomiolatunji.bakerapp.data.dataSources;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.yomiolatunji.bakerapp.data.DataLoadingCallback;
import com.yomiolatunji.bakerapp.data.NetworkUtils;
import com.yomiolatunji.bakerapp.data.entities.Recipe;
import com.yomiolatunji.bakerapp.data.entities.RecipeIngredient;
import com.yomiolatunji.bakerapp.data.entities.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oluwayomi on 24/06/2017.
 */

public class BakerDataSource implements LoaderManager.LoaderCallbacks<List<Recipe>> {
    private static final int ID_RECIPE_LOADER = 536;
    private String Url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    private AppCompatActivity mContext;
    private DataLoadingCallback<List<Recipe>> loadingCallback;

    public BakerDataSource(AppCompatActivity context, DataLoadingCallback<List<Recipe>> loadingCallback) {

        mContext = context;
        this.loadingCallback = loadingCallback;
    }

    public void getData() {


        mContext.getSupportLoaderManager().initLoader(ID_RECIPE_LOADER, null, this);
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
                        loadingCallback.onFailure("Empty list");
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

        loadingCallback.onResponse(data);
    }


    @Override
    public void onLoaderReset(Loader<List<Recipe>> loader) {

    }
}
