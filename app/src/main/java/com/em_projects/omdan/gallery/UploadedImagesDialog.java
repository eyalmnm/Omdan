package com.em_projects.omdan.gallery;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.em_projects.omdan.R;

import java.util.ArrayList;

public class UploadedImagesDialog extends DialogFragment {

    private String argsName = "filesToUpload";

    private TextView uploadedImagesTitleTextView;
    private ListView uploadedImagesListView;
    private Button okButton;

    private ArrayList<String> fileName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_uploaded_images, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        fileName = args.getStringArrayList(argsName);

        uploadedImagesTitleTextView = view.findViewById(R.id.uploadedImagesTitleTextView);
        uploadedImagesListView = view.findViewById(R.id.uploadedImagesListView);
        okButton = view.findViewById(R.id.okButton);

        uploadedImagesTitleTextView.setText(getString(R.string.dialog_uploaded_images_title, fileName.size()));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, fileName);

        uploadedImagesListView.setAdapter(adapter);

        okButton = view.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}