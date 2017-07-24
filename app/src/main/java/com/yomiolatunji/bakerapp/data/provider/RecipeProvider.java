package com.yomiolatunji.bakerapp.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yomiolatunji.bakerapp.data.provider.RecipeContract.IngredientsEntry;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract.RecipeEntry;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract.StepEntry;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by oluwayomi on 23/07/2017.
 */

public class RecipeProvider extends ContentProvider {

    public static final int RECIPES = 100;
    public static final int RECIPES_WITH_ID = 101;
    public static final int STEPS = 200;
    public static final int STEP_WITH_ID = 201;
    public static final int STEP_WITH_RECIPE_ID = 202;
    public static final int INGREDIENTS = 300;
    public static final int INGREDIENTS_WITH_RECIPE_ID = 301;

    private static final UriMatcher sURI_MATCHER = buildUriMatcher();
    private RecipeDataHelper mRecipeDataHelper;

    public static UriMatcher buildUriMatcher() {
        // Initialize a UriMatcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add URI matches
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPES, RECIPES);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPES + "/#", RECIPES_WITH_ID);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_STEPS, STEPS);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_STEPS + "/#", STEP_WITH_ID);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_STEPS + "/recipe/#", STEP_WITH_RECIPE_ID);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_INGREDIENTS, INGREDIENTS);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_INGREDIENTS + "/recipe/#", INGREDIENTS_WITH_RECIPE_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mRecipeDataHelper = new RecipeDataHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mRecipeDataHelper.getWritableDatabase();

        int match = sURI_MATCHER.match(uri);
        Cursor retCursor;
        switch (match) {
            case RECIPES: {
                retCursor = db.query(RecipeEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            case RECIPES_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                retCursor = db.query(RecipeEntry.TABLE_NAME, projection,
                        RecipeEntry._ID + "=?",
                        new String[]{id},
                        null, null,
                        sortOrder);
                break;
            }
            case INGREDIENTS: {
                retCursor = db.query(IngredientsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case INGREDIENTS_WITH_RECIPE_ID: {
                String recipeId = uri.getPathSegments().get(2);
                retCursor = db.query(IngredientsEntry.TABLE_NAME,
                        projection,
                        IngredientsEntry.COLUMN_RECIPE_ID + "=?",
                        new String[]{recipeId},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case STEPS: {
                retCursor = db.query(StepEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder);
                break;
            }
            case STEP_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                retCursor = db.query(StepEntry.TABLE_NAME,
                        projection,
                        StepEntry._ID + "=?",
                        new String[]{id},
                        null, null,
                        sortOrder);
                break;
            }
            case STEP_WITH_RECIPE_ID: {
                String recipeId = uri.getPathSegments().get(2);
                retCursor = db.query(StepEntry.TABLE_NAME,
                        projection,
                        StepEntry.COLUMN_RECIPE_ID + "=?",
                        new String[]{recipeId},
                        null, null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mRecipeDataHelper.getWritableDatabase();
        int match = sURI_MATCHER.match(uri);
        Uri returnUri;
        switch (match) {
            case RECIPES: {
                long id = db.insert(RecipeEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(RecipeEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case INGREDIENTS: {
                long id = db.insert(IngredientsEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(IngredientsEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case STEPS: {
                long id = db.insert(StepEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(StepEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mRecipeDataHelper.getWritableDatabase();

        int match = sURI_MATCHER.match(uri);
        int deleteRecipe;

        switch (match) {
            case RECIPES: {
                deleteRecipe = db.delete(RecipeEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case RECIPES_WITH_ID: {
                if (selection == null) selection = RecipeEntry._ID + "=?";
                else selection += " AND " + RecipeEntry._ID + "=?";
                String id = uri.getPathSegments().get(1);
                if (selectionArgs == null) selectionArgs = new String[]{id};
                else {
                    ArrayList<String> selectionArgsList = new ArrayList<String>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(id);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }

                deleteRecipe = db.delete(RecipeEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case INGREDIENTS: {
                deleteRecipe = db.delete(IngredientsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case INGREDIENTS_WITH_RECIPE_ID: {
                if (selection == null) selection = IngredientsEntry._ID + "=?";
                else selection += " AND " + IngredientsEntry.COLUMN_RECIPE_ID + "=?";
                String recipeId = uri.getPathSegments().get(2);
                if (selectionArgs == null) selectionArgs = new String[]{recipeId};
                else {
                    ArrayList<String> selectionArgsList = new ArrayList<String>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(recipeId);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }
                deleteRecipe = db.delete(IngredientsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case STEPS: {
                deleteRecipe = db.delete(StepEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case STEP_WITH_ID: {
                if (selection == null) selection = StepEntry._ID + "=?";
                else selection += " AND " + StepEntry._ID + "=?";
                String id = uri.getPathSegments().get(1);
                if (selectionArgs == null) selectionArgs = new String[]{id};
                else {
                    ArrayList<String> selectionArgsList = new ArrayList<String>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(id);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }
                deleteRecipe = db.delete(StepEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case STEP_WITH_RECIPE_ID: {
                if (selection == null) selection = StepEntry._ID + "=?";
                else selection += " AND " + StepEntry.COLUMN_RECIPE_ID + "=?";
                String recipeId = uri.getPathSegments().get(2);
                if (selectionArgs == null) selectionArgs = new String[]{recipeId};
                else {
                    ArrayList<String> selectionArgsList = new ArrayList<String>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(recipeId);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }
                deleteRecipe = db.delete(StepEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        if (deleteRecipe > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteRecipe;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mRecipeDataHelper.getWritableDatabase();

        int match = sURI_MATCHER.match(uri);
        int updatedId;
        switch (match) {
            case RECIPES: {
                updatedId = db.update(RecipeEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case RECIPES_WITH_ID: {
                if (selection == null) selection = RecipeEntry._ID + "=?";
                else selection += " AND " + RecipeEntry._ID + "=?";
                String id = uri.getPathSegments().get(1);
                if (selectionArgs == null) selectionArgs = new String[]{id};
                else {
                    ArrayList<String> selectionArgsList = new ArrayList<String>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(id);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }

                updatedId = db.update(RecipeEntry.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;
            }
            case INGREDIENTS: {
                updatedId = db.update(IngredientsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case INGREDIENTS_WITH_RECIPE_ID: {
                if (selection == null) selection = IngredientsEntry._ID + "=?";
                else selection += " AND " + IngredientsEntry.COLUMN_RECIPE_ID + "=?";
                String recipeId = uri.getPathSegments().get(2);
                if (selectionArgs == null) selectionArgs = new String[]{recipeId};
                else {
                    ArrayList<String> selectionArgsList = new ArrayList<String>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(recipeId);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }
                updatedId = db.update(IngredientsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case STEPS: {
                updatedId = db.update(StepEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case STEP_WITH_ID: {
                if (selection == null) selection = StepEntry._ID + "=?";
                else selection += " AND " + StepEntry._ID + "=?";
                String id = uri.getPathSegments().get(1);
                if (selectionArgs == null) selectionArgs = new String[]{id};
                else {
                    ArrayList<String> selectionArgsList = new ArrayList<String>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(id);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }
                updatedId = db.update(StepEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case STEP_WITH_RECIPE_ID: {
                if (selection == null) selection = StepEntry._ID + "=?";
                else selection += " AND " + StepEntry.COLUMN_RECIPE_ID + "=?";
                String recipeId = uri.getPathSegments().get(2);
                if (selectionArgs == null) selectionArgs = new String[]{recipeId};
                else {
                    ArrayList<String> selectionArgsList = new ArrayList<String>();
                    selectionArgsList.addAll(Arrays.asList(selectionArgs));
                    selectionArgsList.add(recipeId);
                    selectionArgs = selectionArgsList.toArray(new String[selectionArgsList.size()]);
                }
                updatedId = db.update(StepEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (updatedId != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedId;
    }
}
