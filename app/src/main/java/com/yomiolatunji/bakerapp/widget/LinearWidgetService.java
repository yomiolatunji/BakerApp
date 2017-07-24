package com.yomiolatunji.bakerapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.provider.RecipeContract;

import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.BASE_URI;
import static com.yomiolatunji.bakerapp.data.provider.RecipeContract.PATH_RECIPES;

/**
 * Created by oluwayomi on 24/07/2017.
 */
public class LinearWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new LinearRemoteViewsFactory(this.getApplicationContext());
    }
}
class LinearRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;

    public LinearRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Uri recipeUri = BASE_URI.buildUpon().appendPath(PATH_RECIPES).build();
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(recipeUri, null, null, null, null);
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
        if(mCursor==null||mCursor.getCount()==0)return null;

        mCursor.moveToPosition(position);
        String name=mCursor.getString(mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_NAME));
        int servings=mCursor.getInt(mCursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_RECIPE_SERVINGS));
        RemoteViews views=new RemoteViews(mContext.getPackageName(), R.layout.item_recipe);

        views.setTextViewText(R.id.recipeName,name);
        views.setTextViewText(R.id.recipeServings,"Servings:"+servings);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
