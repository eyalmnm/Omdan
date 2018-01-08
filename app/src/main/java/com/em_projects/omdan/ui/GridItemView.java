package com.em_projects.omdan.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.em_projects.omdan.R;
import com.em_projects.omdan.utils.DimenUtils;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by eyalmuchtar on 11/9/17.
 */

public class GridItemView extends FrameLayout {
    private static final String TAG = "GridItemView";

    private ImageView theImageView;
    private View rootViewLayout;
    private int selectedBackgroundColor;
    private int normalBackgroundColor;

    private int cellHeight;
    private int cellWidth;

    private Context context;

    public GridItemView(Context context) {
        super(context);
        this.context = context;
        selectedBackgroundColor = context.getResources().getColor(R.color.turquoise);
        normalBackgroundColor = context.getResources().getColor(R.color.transparent);
        LayoutInflater.from(context).inflate(R.layout.item_grid, this);
        theImageView = (ImageView) getRootView().findViewById(R.id.theImageView);
        rootViewLayout = getRootView().findViewById(R.id.rootViewLayout);
        cellHeight = DimenUtils.dpToPx(100);
        cellWidth = DimenUtils.dpToPx(100);
    }

    public void display(String path, boolean isSelected) {
        Picasso.with(context)
                .load("file://" + path)
                .config(Bitmap.Config.RGB_565)
                .fit().centerCrop()
                .placeholder(R.drawable.ic_report_problem_white_18dp)
                .into(theImageView);
        display(isSelected);
    }

    public void display(boolean isSelected) {
        rootViewLayout.setBackgroundColor(isSelected ? selectedBackgroundColor : normalBackgroundColor);
    }
}
