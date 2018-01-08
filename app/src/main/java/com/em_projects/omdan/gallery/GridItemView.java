package com.em_projects.omdan.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.em_projects.omdan.R;
import com.squareup.picasso.Picasso;

/**
 * Created by eyalmuchtar on 11/9/17.
 */

// Ref: https://stackoverflow.com/questions/17823451/set-android-shape-color-programmatically

public class GridItemView extends FrameLayout {
    private static final String TAG = "GridItemView";

    private ImageView theImageView;
    private ImageView selectionIndicatorImageView;
    private TextView fileNameTextView;

    private boolean isDirectory;
    private String fileName;
    private String fullPath;
    private int normalBackgroundColor;
    private int selectedBackgroundColor;

    private Context context;

    public GridItemView(Context context) {
        super(context);
        this.context = context;
        normalBackgroundColor = context.getResources().getColor(R.color.transparent);
        selectedBackgroundColor = context.getResources().getColor(R.color.turquoise);
        LayoutInflater.from(context).inflate(R.layout.item_grid, this);
        theImageView = (ImageView) getRootView().findViewById(R.id.theImageView);
        selectionIndicatorImageView = (ImageView) getRootView().findViewById(R.id.selectionIndicatorImageView);
        fileNameTextView = (TextView) getRootView().findViewById(R.id.fileNameTextView);
    }

    public void display(String path, String fileName, boolean isDirectory, boolean inSelectionMode, boolean isSelected) {
        if (true == isDirectory) {   // Picasso does not support in load(condition ? resId : "file://"+ path)
            Picasso.with(context)
                    .load(R.drawable.ic_folder_white_18dp)
                    .config(Bitmap.Config.RGB_565)
                    .fit().centerCrop()
                    .placeholder(R.drawable.ic_folder_white_18dp)
                    .into(theImageView);;
        } else {
            Picasso.with(context)
                    .load("file://" + path)
                    .config(Bitmap.Config.RGB_565)
                    .fit().centerCrop()
                    .placeholder(R.drawable.ic_report_problem_white_18dp)
                    .into(theImageView);
        }
        this.isDirectory = isDirectory;
        this.fileName = fileName;
        this.fullPath = path;
        display(isSelected);
        selectionIndicatorImageView.setVisibility(inSelectionMode ? VISIBLE : INVISIBLE);
        fileNameTextView.setText(String.valueOf(fileName));
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void display(boolean isSelected) {
        GradientDrawable gd = (GradientDrawable) selectionIndicatorImageView.getBackground();
        gd.setColor(isSelected ? selectedBackgroundColor : normalBackgroundColor);
    }
}
