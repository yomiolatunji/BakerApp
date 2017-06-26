package com.yomiolatunji.bakerapp.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Oluwayomi on 5/27/2016.
 */
public class CustomNestScrollView extends NestedScrollView {
    public CustomNestScrollView(Context context) {
        super(context);
    }

    public CustomNestScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNestScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        //super.requestChildFocus(child, focused);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        //return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
        return false;
    }
}
