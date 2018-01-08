package com.em_projects.omdan.dialogs;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.em_projects.omdan.R;
import com.em_projects.omdan.main.fragments.NewRecordFragment;

/**
 * Created by eyalmuchtar on 03/10/2017.
 */

public class CreateNewRecordDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "CreateNewRecordDialog";

    private NewRecordFragment parent;

    private AppCompatTextView captionTextView;
    private Button okButton;
    private Button cancelButton;


    public void setParentFragment(NewRecordFragment parent) {
        this.parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_create_new_record, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //setCancelable(false);

        captionTextView = (AppCompatTextView) view.findViewById(R.id.captionTextView);
        okButton = (Button) view.findViewById(R.id.okButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        String recId = "1000";
        //Bundle args = getArguments();               // TODO
        //String recId = args.getString("data"); // TODO

        captionTextView.setText(getString(R.string.create_new_record_msg, recId));
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    // ref: https://stackoverflow.com/questions/9033019/removing-a-fragment-from-the-back-stack
    // ref: https://stackoverflow.com/a/13074955
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        //trans.remove(this);
        //trans.commit();
        manager.popBackStack();
    }

    @Override
    public void onClick(View view) {
        if (R.id.okButton == view.getId()) {
            if (null != parent) {
                parent.createNewRecord();
            }
        } else if (R.id.cancelButton == view.getId()) {
            if (null != parent) {
                parent.cancelCreation();
            }
        }
        dismiss();
    }
}
