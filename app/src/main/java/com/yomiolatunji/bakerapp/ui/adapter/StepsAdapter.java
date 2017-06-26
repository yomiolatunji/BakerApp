package com.yomiolatunji.bakerapp.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yomiolatunji.bakerapp.R;
import com.yomiolatunji.bakerapp.data.entities.RecipeStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oluwayomi on 25/06/2017.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {
    private final Context context;
    LayoutInflater layoutInflater;
    OnClickStepListener clickStepListener;
    private List<RecipeStep> steps;

    public StepsAdapter(Context context, OnClickStepListener listener) {
        this.context = context;
        clickStepListener = listener;
        steps = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public StepsAdapter.StepsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StepsViewHolder(layoutInflater.inflate(R.layout.item_recipe_step, parent, false));
    }

    @Override
    public void onBindViewHolder(StepsAdapter.StepsViewHolder holder, final int position) {
        final RecipeStep step = getItem(position);

        holder.shortDescription.setText(step.getShortDescription());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStepListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    private RecipeStep getItem(int position) {
        return steps.get(position);
    }


    public void clear() {
        steps.clear();
        notifyDataSetChanged();
    }

    public void addAndResort(List<RecipeStep> recipeSteps) {
        deduplicateAndAdd(recipeSteps);
        notifyDataSetChanged();
    }


    /**
     * De-dupe as the same item can be returned by multiple feeds
     */
    private void deduplicateAndAdd(List<RecipeStep> newItems) {
        final int count = steps.size();
        for (RecipeStep newItem : newItems) {
            boolean add = true;
            for (int i = 0; i < count; i++) {
                RecipeStep existingItem = getItem(i);
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

    private void add(RecipeStep item) {
        steps.add(item);
    }


    public interface OnClickStepListener {
        void onClick(int pos);
    }

    class StepsViewHolder extends RecyclerView.ViewHolder {
        TextView shortDescription;
        View mView;

        public StepsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            shortDescription = (TextView) itemView.findViewById(R.id.shortDescription);
        }
    }
}
