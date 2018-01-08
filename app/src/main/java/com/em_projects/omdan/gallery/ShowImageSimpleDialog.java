package com.em_projects.omdan.gallery;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.em_projects.omdan.R;
import com.em_projects.omdan.utils.DimenUtils;
import com.em_projects.omdan.utils.FileUtils;

import java.io.File;

/**
 * Created by eyalmuchtar on 12/4/17.
 */

public class ShowImageSimpleDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "ShowImageDialog";

    private ImageView imageImageView;
    private ImageButton closeImageButton;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_show_image_simple, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        imageImageView = (ImageView) view.findViewById(R.id.imageImageView);
        closeImageButton = (ImageButton) view.findViewById(R.id.closeImageButton);
        closeImageButton.setEnabled(false);

        context = getActivity();
        closeImageButton.setOnClickListener(this);

        Bundle args = getArguments();
        String imagePath = args.getString("data");

        new ShowImageSimpleDialog.ImageLoader().execute(imagePath);
    }

    @Override
    public void onClick(View view) {
        if (R.id.closeImageButton == view.getId()) {
            dismiss();
        }
    }

    private class ImageLoader extends AsyncTask<String, Void, Bitmap> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (null != dialog) {
                dialog.dismiss();
            }
            dialog = ProgressDialog.show(context, "", "טוען...", true, false);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            File file = new File(strings[0]);
            int size = DimenUtils.dpToPx(200);
            Bitmap bitmap = null;
            if (true == FileUtils.isImageFile(file)) {
                try {
                    bitmap = FileUtils.decodeFile(file, size);
                } catch (Exception e) {
                    Log.e(TAG, "doInBackground", e);
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageImageView.setImageBitmap(bitmap);
            if (null != dialog) {
                dialog.dismiss();
                dialog = null;
            }
            closeImageButton.setEnabled(true);
        }

    }

}
