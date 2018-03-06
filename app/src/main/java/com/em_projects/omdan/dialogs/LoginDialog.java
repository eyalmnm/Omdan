package com.em_projects.omdan.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.omdan.R;
import com.em_projects.omdan.utils.StringUtils;

/**
 * Created by eyalmuchtar on 27/09/2017.
 */

public class LoginDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "LoginDialog";

    public interface OnSetLoginDataListener {
        void onSetLoginDataListener(String usr, String pwd);
    }
    private OnSetLoginDataListener listener;

    private EditText nameEditText;
    private EditText passwordEditText;

    private Button okButton;
    private Button cancelButton;

    private Context context;


    @Override
    public void onAttach(Activity activity) {
        listener = (OnSetLoginDataListener) activity;
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_login, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setCancelable(false);

        nameEditText = (EditText) view.findViewById(R.id.nameEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        okButton = (Button) view.findViewById(R.id.okButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        // For Concept testing only
//        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                    nameEditText.setHint("Enter phone number or mail");
//                } else {
//                    nameEditText.setHint("");
//                }
//            }
//        });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    okButton.performClick();
                    return true;
                }
                return false;
            }
        });

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (R.id.cancelButton == view.getId()) {
            dismiss();
            System.exit(0);
        } else if (null != listener) {
            String usr = nameEditText.getText().toString();
            String pwd = passwordEditText.getText().toString();
            if ((true == StringUtils.isNullOrEmpty(usr)) || (true == StringUtils.isNullOrEmpty(pwd))) {
                Toast.makeText(context, R.string.missing_data_all, Toast.LENGTH_SHORT).show();
            } else {
                if (null != listener) listener.onSetLoginDataListener(usr, pwd);
                dismiss();
            }
        }
    }
}
