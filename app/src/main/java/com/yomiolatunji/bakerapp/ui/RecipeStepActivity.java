package com.yomiolatunji.bakerapp.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.entities.Recipe;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeActivity}.
 */
public class RecipeStepActivity extends AppCompatActivity implements RecipeStepFragment.ChangeStepListener {

    private Recipe recipe;
    private View container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    container=findViewById(R.id.item_detail_container);
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            int position = getIntent().getIntExtra(RecipeStepFragment.ARG_STEP_POS, 0);
            recipe = getIntent().getParcelableExtra(RecipeStepFragment.ARG_RECIPE);
            initializeFragment(position);
        }
    }

    private void initializeFragment(int position) {
        Bundle arguments = new Bundle();
        arguments.putInt(RecipeStepFragment.ARG_STEP_POS,
                position);
        arguments.putParcelable(RecipeStepFragment.ARG_RECIPE,
                recipe);
        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNext(int currentPos) {
        if (currentPos < recipe.getRecipeSteps().size()-1) {
            initializeFragment(currentPos + 1);
        } else {
            Snackbar.make(container,R.string.last_step_reached,Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPrevious(int currentPos) {
        if (currentPos > 0) {
            initializeFragment(currentPos - 1);
        } else {
            Snackbar.make(container,R.string.already_at_first_step,Snackbar.LENGTH_LONG).show();
            //Toast.makeText(this, R.string.already_at_first_step, Toast.LENGTH_SHORT).show();
        }
    }
}
