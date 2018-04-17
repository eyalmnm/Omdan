package com.em_projects.omdan.dialogs;

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

import com.em_projects.omdan.R;

/**
 * Created by eyalmuchtar on 4/10/18.
 */

// Ref: https://stackoverflow.com/questions/43389607/how-to-add-line-break-to-a-rtl-string-in-strings-xml-in-android

public class LoginFailedDialog extends DialogFragment implements View.OnClickListener {

    private OnLoginFailedListener listener;
    private Button okButton;
    private Context context;

    @Override
    public void onAttach(Activity activity) {
        listener = (OnLoginFailedListener) activity;
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setStyle(R.style.CustomDialogTheme, android.R.style.Theme_Dialog);
        return inflater.inflate(R.layout.dialog_login_failed, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        okButton = view.findViewById(R.id.okButton);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (null != listener) {
            listener.okButtonPressed();
        }
    }

    public interface OnLoginFailedListener {
        void okButtonPressed();
    }
}
