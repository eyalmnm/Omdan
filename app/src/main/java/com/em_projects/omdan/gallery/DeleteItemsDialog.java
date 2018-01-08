package com.em_projects.omdan.gallery;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.em_projects.omdan.R;

/**
 * Created by eyalmuchtar on 11/28/17.
 */

public class DeleteItemsDialog extends DialogFragment {
    private static final String TAG = "DeleteItemsDialog";
    private OnDeleteConfirmListener listener;
    private Button okDialog;
    private Button cancelDialog;
    private Context context;

    @Override
    public void onAttach(Activity activity) {
        listener = (OnDeleteConfirmListener) activity;
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_delete_confirmation, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int selectedFiles = getArguments().getInt("data");
        String title = getString(R.string.delete_dialog_title, selectedFiles);
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        Button okButton = (Button) view.findViewById(R.id.okButton);
        titleTextView.setText(title);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnDeleteConfirm(false);
                dismiss();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnDeleteConfirm(true);
                dismiss();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnDeleteConfirmListener {
        public void OnDeleteConfirm(boolean confirm);
    }
}
