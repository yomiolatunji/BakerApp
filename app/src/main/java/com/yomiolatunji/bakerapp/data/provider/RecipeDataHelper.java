package com.yomiolatunji.bakerapp.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yomiolatunji.bakerapp.data.provider.RecipeContract.IngredientsEntry;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract.RecipeEntry;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract.StepEntry;

/**
 * Created by oluwayomi on 23/07/2017.
 */

public class RecipeDataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recipe.db";
    private static final int DATABASE_VERSION = 2;

    public RecipeDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRecipeTableSql = "CREATE TABLE " + RecipeEntry.TABLE_NAME + " ( " +
                RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RecipeEntry.COLUMN_RECIPE_NAME + " TEXT NOT NULL, " +
                RecipeEntry.COLUMN_RECIPE_SERVINGS + " INTEGER, " +
                RecipeEntry.COLUMN_RECIPE_IMAGE + " TEXT," +
                " UNIQUE (" + RecipeEntry.COLUMN_RECIPE_NAME + ") ON CONFLICT REPLACE);";
        db.execSQL(createRecipeTableSql);

        String createIngredientTableSql = "CREATE TABLE " + IngredientsEntry.TABLE_NAME + " ( " +
                IngredientsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IngredientsEntry.COLUMN_INGREDIENT + " TEXT NOT NULL, " +
                IngredientsEntry.COLUMN_MEASURE + " TEXT, " +
                IngredientsEntry.COLUMN_QUANTITY + " INTEGER, " +
                IngredientsEntry.COLUMN_RECIPE_ID + " INTEGER, " +
                " FOREIGN KEY (" + IngredientsEntry.COLUMN_RECIPE_ID + ") REFERENCES " +
                RecipeEntry.TABLE_NAME + " (" + RecipeEntry._ID + ") , " +
                " UNIQUE (" + IngredientsEntry.COLUMN_RECIPE_ID + ", " +
                IngredientsEntry.COLUMN_INGREDIENT + ") ON CONFLICT REPLACE);";
        db.execSQL(createIngredientTableSql);

        String createStepTableSql = "CREATE TABLE " + StepEntry.TABLE_NAME + " ( " +
                StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StepEntry.COLUMN_NUMBER + " INTEGER, " +
                StepEntry.COLUMN_RECIPE_ID + " INTEGER, " +
                StepEntry.COLUMN_DESCRIPTION + " TEXT, " +
                StepEntry.COLUMN_SHORT_DESCRIPTION + " TEXT," +
                StepEntry.COLUMN_THUMBNAIL_URL + " TEXT, " +
                StepEntry.COLUMN_VIDEO_URL + " TEXT, " +
                "FOREIGN KEY (" + StepEntry.COLUMN_RECIPE_ID + ") REFERENCES " +
                RecipeEntry.TABLE_NAME + " (" + RecipeEntry._ID + ") , " +
                " UNIQUE (" + StepEntry.COLUMN_RECIPE_ID + ", " +
                StepEntry.COLUMN_SHORT_DESCRIPTION + ") ON CONFLICT REPLACE);";
        db.execSQL(createStepTableSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StepEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + IngredientsEntry.TABLE_NAME);

        onCreate(db);
    }
}
