package com.yomiolatunji.bakerapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.entities.Recipe;
import com.yomiolatunji.bakerapp.data.entities.RecipeStep;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link RecipeActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepActivity}
 * on handsets.
 */
public class RecipeStepFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_STEP_POS = "step_pos";
    public static final String ARG_RECIPE = "recipe";

    Recipe recipe;
    int currentPosition;
    RecipeStep currentStep;

    public RecipeStepFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_STEP_POS)) {
            currentPosition = getArguments().getInt(ARG_STEP_POS);
            recipe = getArguments().getParcelable(ARG_RECIPE);
            currentStep = recipe.getRecipeSteps().get(currentPosition);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(currentStep.getShortDescription());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);


        return rootView;
    }
}
