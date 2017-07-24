package com.yomiolatunji.bakerapp.data.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by oluwayomi on 23/07/2017.
 */

public class RecipeContract {
    public static final String AUTHORITY = "com.yomiolatunji.bakerapp";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_RECIPES = "recipes";
    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String PATH_STEPS = "steps";

    public static final class RecipeEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_RECIPES).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + PATH_RECIPES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + AUTHORITY + "/" + PATH_RECIPES;

        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_RECIPE_NAME = "name";
        public static final String COLUMN_RECIPE_SERVINGS = "servings";
        public static final String COLUMN_RECIPE_IMAGE = "image";

        public static Uri buildRecipesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class IngredientsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + AUTHORITY + "/" + PATH_INGREDIENTS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + AUTHORITY + "/" + PATH_INGREDIENTS;


        public static final String TABLE_NAME = "ingredients";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_INGREDIENT = "ingredient";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_MEASURE = "measure";
    }

    public static final class StepEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_STEPS).build();


        public static final String TABLE_NAME = "steps";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_SHORT_DESCRIPTION = "short_description";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VIDEO_URL = "video_url";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";

    }
}
