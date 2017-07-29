package com.yomiolatunji.bakerapp.ui;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.yomiolatunji.bakerapp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeActivityTest {

    @Rule
    public ActivityTestRule<RecipeActivity> mActivityTestRule = new ActivityTestRule<>(RecipeActivity.class);
    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void recipeActivityTest() {
        ViewInteraction recyclerView4 = onView(
                allOf(withId(R.id.item_recipe_step), isDisplayed()));
        recyclerView4.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction button = onView(withId(R.id.next));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(withId(R.id.previous));
        button2.check(matches(isDisplayed()));

        ViewInteraction textView = onView(allOf(withId(R.id.recipeStep), withText("Recipe Introduction")));
        textView.check(matches(withText("Recipe Introduction")));

    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null)
            Espresso.unregisterIdlingResources(mIdlingResource);
    }
}
