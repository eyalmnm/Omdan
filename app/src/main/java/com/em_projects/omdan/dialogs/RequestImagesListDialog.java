package com.em_projects.omdan.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.em_projects.omdan.R;

public class RequestImagesListDialog extends DialogFragment implements View.OnClickListener {

    private OnImagesListRequestListener listener;
    private Button okButton;
    private Button cancelButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_images_list_request, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setCancelable(false);

        okButton = (Button) view.findViewById(R.id.okButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    public void setListener(OnImagesListRequestListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (R.id.okButton == view.getId()) {
            if (null != listener) {
                listener.onImagesListRequest();
            }
        }
        dismiss();
    }

    public interface OnImagesListRequestListener {
        void onImagesListRequest();
    }
}
