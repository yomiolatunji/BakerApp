package com.yomiolatunji.bakerapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.DataLoadingCallback;
import com.yomiolatunji.bakerapp.data.NetworkUtils;
import com.yomiolatunji.bakerapp.data.dataSources.BakerDataSource;
import com.yomiolatunji.bakerapp.data.entities.Recipe;
import com.yomiolatunji.bakerapp.ui.adapter.RecipeAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DataLoadingCallback<List<Recipe>> {

    private RecyclerView recipesRecyclerView;
    private ProgressBar loadingBar;
    private ImageView noConnectionView;
    private RecipeAdapter adapter;
    private GridLayoutManager layoutManager;
    private boolean isLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isLarge = getResources().getBoolean(R.bool.large_screen);
        recipesRecyclerView = (RecyclerView) findViewById(R.id.recipe_list);
        loadingBar = (ProgressBar) findViewById(R.id.loading);
        noConnectionView = (ImageView) findViewById(R.id.no_connection);
        adapter = new RecipeAdapter(MainActivity.this);
        final BakerDataSource bakerDataSource = new BakerDataSource(MainActivity.this, this);
        if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {
            getData(bakerDataSource);
        } else {
            getOfflineData(bakerDataSource);
            recipesRecyclerView.setVisibility(View.GONE);
            loadingBar.setVisibility(View.GONE);
            noConnectionView.setVisibility(View.VISIBLE);
        }
        recipesRecyclerView.setAdapter(adapter);
        if (isLarge) {

            recipesRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        } else {

            recipesRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        }

    }

    private void getOfflineData(BakerDataSource bakerDataSource) {
        loadingBar.setVisibility(View.VISIBLE);
        recipesRecyclerView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.GONE);
        adapter.clear();
        bakerDataSource.getOfflineData();
    }

    private void getData(BakerDataSource bakerDataSource) {
        loadingBar.setVisibility(View.VISIBLE);
        recipesRecyclerView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.GONE);
        adapter.clear();
        bakerDataSource.getData();
    }

    @Override
    public void onResponse(List<Recipe> data) {
        if (data != null && data.size() > 0) {
            adapter.addAndResort(data);
            recipesRecyclerView.setVisibility(View.VISIBLE);
        }
        loadingBar.setVisibility(View.GONE);

    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }


}
