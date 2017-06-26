package com.yomiolatunji.bakerapp.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by oluwayomi on 24/06/2017.
 */

public class RecipeIngredient implements Parcelable {
    public static final Creator<RecipeIngredient> CREATOR = new Creator<RecipeIngredient>() {
        @Override
        public RecipeIngredient createFromParcel(Parcel in) {
            return new RecipeIngredient(in);
        }

        @Override
        public RecipeIngredient[] newArray(int size) {
            return new RecipeIngredient[size];
        }
    };

    public RecipeIngredient() {
    }

    private int quantity;
    private String measure;
    private String ingredient;

    protected RecipeIngredient(Parcel in) {
        quantity = in.readInt();
        measure = in.readString();
        ingredient = in.readString();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(quantity);
        dest.writeString(measure);
        dest.writeString(ingredient);
    }
}
