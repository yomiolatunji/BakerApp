package com.yomiolatunji.bakerapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.SimpleIdlingResource;
import com.yomiolatunji.bakerapp.data.DataLoadingCallback;
import com.yomiolatunji.bakerapp.data.dataSources.BakerDataSource;
import com.yomiolatunji.bakerapp.data.entities.Recipe;
import com.yomiolatunji.bakerapp.ui.adapter.FavouriteRecipeWidgetAdapter;
import com.yomiolatunji.bakerapp.ui.adapter.RecipeAdapter;
import com.yomiolatunji.bakerapp.widget.RecipeWidgetService;

import java.util.List;

public class FavouriteRecipeWidgetConfiguration extends AppCompatActivity implements DataLoadingCallback<List<Recipe>>,FavouriteRecipeWidgetAdapter.OnFavouriteSelectedListener {
    private RecyclerView recipesRecyclerView;
    private ProgressBar loadingBar;
    private ImageView noConnectionView;
    private FavouriteRecipeWidgetAdapter adapter;
    @Nullable
    private SimpleIdlingResource mIdlingResource;
    private int mAppWidgetId;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_recipe_widget);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        recipesRecyclerView = (RecyclerView) findViewById(R.id.recipe_list);
        loadingBar = (ProgressBar) findViewById(R.id.loading);
        noConnectionView = (ImageView) findViewById(R.id.no_connection);
        adapter = new FavouriteRecipeWidgetAdapter(FavouriteRecipeWidgetConfiguration.this,this);
        final BakerDataSource bakerDataSource = new BakerDataSource(FavouriteRecipeWidgetConfiguration.this, this);
        getOfflineData(bakerDataSource);
        recipesRecyclerView.setVisibility(View.GONE);
        loadingBar.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.VISIBLE);
        recipesRecyclerView.setAdapter(adapter);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(FavouriteRecipeWidgetConfiguration.this));

    }

    private void getOfflineData(BakerDataSource bakerDataSource) {
        loadingBar.setVisibility(View.VISIBLE);
        recipesRecyclerView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.GONE);
        adapter.clear();
        bakerDataSource.getOfflineData(mIdlingResource);
    }

    @Override
    public void onResponse(List<Recipe> data) {
        if (data != null && data.size() > 0) {
            adapter.addAndResort(data);
            recipesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(FavouriteRecipeWidgetConfiguration.this, R.string.server_error, Toast.LENGTH_SHORT).show();
        }
        loadingBar.setVisibility(View.GONE);

    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(FavouriteRecipeWidgetConfiguration.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void selectedRecipe(int id) {
        SharedPreferences sharedPreferences = getSharedPreferences
                (getString(R.string.recipe_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(String.valueOf(mAppWidgetId),id);
        editor.apply();

        RecipeWidgetService.startActionUpdateRecipeWidgets(this);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
