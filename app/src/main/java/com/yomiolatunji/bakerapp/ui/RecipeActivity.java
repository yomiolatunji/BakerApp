package com.yomiolatunji.bakerapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.entities.Recipe;
import com.yomiolatunji.bakerapp.data.entities.RecipeStep;
import com.yomiolatunji.bakerapp.ui.adapter.IngredientAdapter;
import com.yomiolatunji.bakerapp.ui.adapter.StepsAdapter;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeActivity extends AppCompatActivity {

    public static final String RECIPE_KEY = "recipe_key";
    private boolean mTwoPane;
    private RecyclerView ingredientRecyclerView;
    private RecyclerView stepRecyclerView;
    private Recipe recipe = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ingredientRecyclerView = (RecyclerView) findViewById(R.id.item_recipe_ingredient);
        stepRecyclerView = (RecyclerView) findViewById(R.id.item_recipe_step);

        ingredientRecyclerView.setNestedScrollingEnabled(false);
        stepRecyclerView.setNestedScrollingEnabled(false);
        Intent intent = getIntent();
        if (intent.hasExtra(RECIPE_KEY)) {
            recipe = intent.getParcelableExtra(RECIPE_KEY);
        }
        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(RecipeActivity.this));
        stepRecyclerView.setLayoutManager(new LinearLayoutManager(RecipeActivity.this));

        IngredientAdapter ingredientAdapter = new IngredientAdapter(RecipeActivity.this);
        ingredientRecyclerView.setAdapter(ingredientAdapter);
        ingredientAdapter.addAndResort(recipe.getIngredients());

        StepsAdapter stepsAdapter=new StepsAdapter(RecipeActivity.this, new StepsAdapter.OnClickStepListener() {
            @Override
            public void onClick(RecipeStep step) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(ItemDetailFragment.ARG_ITEM_ID, step);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(RecipeActivity.this, RecipeStepActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, step);

                    startActivity(intent);
                }
            }
        });
        stepRecyclerView.setAdapter(stepsAdapter);
        stepsAdapter.addAndResort(recipe.getRecipeSteps());

    }


}
