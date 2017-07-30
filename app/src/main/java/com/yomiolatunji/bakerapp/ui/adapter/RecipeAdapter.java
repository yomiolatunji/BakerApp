package com.yomiolatunji.bakerapp.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.entities.Recipe;
import com.yomiolatunji.bakerapp.ui.RecipeActivity;
import com.yomiolatunji.bakerapp.ui.RecipeStepActivity;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {


    private final Activity activity;
    private final LayoutInflater layoutInflater;
    private List<Recipe> items;

    public RecipeAdapter(Activity hostActivity) {
        this.activity = hostActivity;
        layoutInflater = LayoutInflater.from(activity);
        items = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public RecipeAdapter.RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecipeHolder(
                layoutInflater.inflate(R.layout.item_recipe, parent, false));

    }

    @Override
    public void onBindViewHolder(final RecipeAdapter.RecipeHolder holder, int position) {
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(activity.getResources(), R.drawable.ic_baking_tools, null);
        Recipe recipe = getItem(position);
        if (!TextUtils.isEmpty(recipe.getImage()))
            Picasso.with(activity)
                    .load(recipe.getImage())
                    .placeholder(vectorDrawableCompat)
                    .error(vectorDrawableCompat)
                    .fit()
                    .into(holder.image);
        else
            holder.image.setImageResource(R.drawable.ic_baking_tools);
        holder.name.setText(recipe.getName());
        holder.servings.setText("Servings: "+recipe.getServings());
        holder.recipe = recipe;
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = v.getContext();
                Intent intent = new Intent(context, RecipeActivity.class);
                intent.putExtra(RecipeActivity.RECIPE_KEY, holder.recipe);
                            context.startActivity(intent);

            }
        });
    }


    private Recipe getItem(int position) {
        return items.get(position);
    }


    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAndResort(List<Recipe> movies) {
        deduplicateAndAdd(movies);
        notifyDataSetChanged();
    }


    /**
     * De-dupe as the same item can be returned by multiple feeds
     */
    private void deduplicateAndAdd(List<Recipe> newItems) {
        final int count = getDataItemCount();
        for (Recipe newItem : newItems) {
            boolean add = true;
            for (int i = 0; i < count; i++) {
                Recipe existingItem = getItem(i);
                if (existingItem.equals(newItem)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                add(newItem);
            }
        }
    }

    private void add(Recipe item) {
        items.add(item);
    }


    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public int getItemCount() {
        return getDataItemCount();
    }


    public int getDataItemCount() {
        return items.size();
    }


    class RecipeHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView servings;
        public Recipe recipe;
        ImageView image;
        View mView;

        public RecipeHolder(View itemView) {
            super(itemView);
            mView = itemView;
            image = (ImageView) itemView.findViewById(R.id.recipeImage);
            name = (TextView) itemView.findViewById(R.id.recipeName);
            servings = (TextView) itemView.findViewById(R.id.recipeServings);
        }

    }


}
