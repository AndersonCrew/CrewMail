package com.dazone.crewemail.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.LruCache;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;


public class RobotoTextView extends AppCompatTextView {

    private final static String NAME = "Regular";
    private static LruCache<String, Typeface> sTypefaceCache = new LruCache<>(12);

    public RobotoTextView(Context context) {
        super(context);
        init();

    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {

        Typeface typeface = sTypefaceCache.get(NAME);

        if (typeface == null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
            sTypefaceCache.put(NAME, typeface);
        }
        setTypeface(typeface);

    }

    @Override
    public void setTypeface(Typeface tf, int style) {

        Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface boldTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Bold.ttf");
        Typeface italicTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Italic.ttf");
        Typeface boldItalicTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-BoldItalic.ttf");

        if (style == Typeface.BOLD) {
            super.setTypeface(boldTypeface/*, -1*/);
        } else if (style == Typeface.ITALIC) {
            super.setTypeface(italicTypeface/*, -1*/);
        } else if (style == Typeface.BOLD_ITALIC) {
            super.setTypeface(boldItalicTypeface/*, -1*/);
        } else {
            super.setTypeface(normalTypeface/*, -1*/);
        }
    }
}
