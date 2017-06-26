package com.yomiolatunji.bakerapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yomiolatunji.bakerapp.ItemDetailActivity;
import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.entities.RecipeIngredient;
import com.yomiolatunji.bakerapp.ui.ItemDetailFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oluwayomi on 25/06/2017.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    LayoutInflater layoutInflater;
    private List<RecipeIngredient> ingredients;

    public IngredientAdapter(Context context) {
        ingredients = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public IngredientAdapter.IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IngredientViewHolder(layoutInflater.inflate(R.layout.item_ingredient, parent, false));
    }

    @Override
    public void onBindViewHolder(IngredientAdapter.IngredientViewHolder holder, int position) {
        RecipeIngredient ingredient = getItem(position);

        holder.ingredient.setText(ingredient.getIngredient());
        holder.measure.setText(ingredient.getMeasure());
        holder.quantity.setText(String.valueOf(ingredient.getQuantity()));
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mTwoPane) {
//                    Bundle arguments = new Bundle();
//                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//                    ItemDetailFragment fragment = new ItemDetailFragment();
//                    fragment.setArguments(arguments);
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.item_detail_container, fragment)
//                            .commit();
//                } else {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, ItemDetailActivity.class);
//                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//
//                    context.startActivity(intent);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    private RecipeIngredient getItem(int position) {
        return ingredients.get(position);
    }


    public void clear() {
        ingredients.clear();
        notifyDataSetChanged();
    }

    public void addAndResort(List<RecipeIngredient> recipeIngredients) {
        deduplicateAndAdd(recipeIngredients);
        notifyDataSetChanged();
    }


    /**
     * De-dupe as the same item can be returned by multiple feeds
     */
    private void deduplicateAndAdd(List<RecipeIngredient> newItems) {
        final int count = ingredients.size();
        for (RecipeIngredient newItem : newItems) {
            boolean add = true;
            for (int i = 0; i < count; i++) {
                RecipeIngredient existingItem = getItem(i);
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

    private void add(RecipeIngredient item) {
        ingredients.add(item);
    }


    class IngredientViewHolder extends RecyclerView.ViewHolder {
        TextView ingredient;
        TextView quantity;
        TextView measure;
View mView;
        public IngredientViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            ingredient = (TextView) itemView.findViewById(R.id.ingredient);
            quantity = (TextView) itemView.findViewById(R.id.quantity);
            measure = (TextView) itemView.findViewById(R.id.measure);
        }
    }
}
