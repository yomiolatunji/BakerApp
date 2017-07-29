package com.yomiolatunji.bakerapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract;

import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.BASE_URI;
import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.PATH_INGREDIENTS;

//import com.yomiolatunji.bakerapp.R;

/**
 * Created by oluwayomi on 24/07/2017.
 */
public class LinearWidgetService extends RemoteViewsService {
    public static final String KEY_RECIPE_ID = "recipe_id";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int id = intent.getIntExtra(KEY_RECIPE_ID, 0);
        return new LinearRemoteViewsFactory(this.getApplicationContext(), id);
    }
}

class LinearRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int recipeId;
    private Cursor mCursor;

    public LinearRemoteViewsFactory(Context context, int recipeId) {
        mContext = context;
        this.recipeId = recipeId;
        Uri ingredientUri = BASE_URI.buildUpon().appendPath(PATH_INGREDIENTS).appendPath("recipe").appendPath(String.valueOf(recipeId)).build();
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(ingredientUri, null, null, null, null);
        Toast.makeText(context, "mCursor.getCount(): "+mCursor.getCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate() {
        Toast.makeText(mContext, "mCursor.getCount(): "+mCursor.getCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataSetChanged() {
        //Uri ingredientUri = BASE_URI.buildUpon().appendPath(PATH_INGREDIENTS).appendPath("recipe").appendPath(String.valueOf(recipeId)).build();
        //if (mCursor != null) mCursor.close();
        //mCursor = mContext.getContentResolver().query(ingredientUri, null, null, null, null);
        Toast.makeText(mContext, "mCursor.getCount(): "+mCursor.getCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;

        mCursor.moveToPosition(position);
        String name = mCursor.getString(mCursor.getColumnIndex(RecipeContract.IngredientsEntry.COLUMN_INGREDIENT));
        int quantity = mCursor.getInt(mCursor.getColumnIndex(RecipeContract.IngredientsEntry.COLUMN_QUANTITY));
        String measure = mCursor.getString(mCursor.getColumnIndex(RecipeContract.IngredientsEntry.COLUMN_MEASURE));
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_recipe);

        views.setTextViewText(R.id.recipeName, name);
        views.setTextViewText(R.id.recipeServings, String.valueOf(quantity));

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
