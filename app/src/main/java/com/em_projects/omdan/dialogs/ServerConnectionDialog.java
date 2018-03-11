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
 * Created by eyalmuchtar on 3/5/18.
 */

public class ServerConnectionDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "ServerConnectionDialog";
    private OnSetServerConnectionListener listener;
    private EditText serverIpEditText;
    private EditText serverPortEditText;
    private Button okButton;
    private Button cancelButton;
    private Context context;
    private Bundle args;

    @Override
    public void onAttach(Activity activity) {
        listener = (OnSetServerConnectionListener) activity;
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_server_connection, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setCancelable(false);

        serverIpEditText = (EditText) view.findViewById(R.id.serverIpEditText);
        serverPortEditText = (EditText) view.findViewById(R.id.serverPortEditText);
        okButton = (Button) view.findViewById(R.id.okButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        args = getArguments();
        if (null != args) {
            serverIpEditText.setText(String.valueOf(args.getString("serverIp", "")));
            serverPortEditText.setText(String.valueOf(args.getInt("serverPort", -1)));
        }

        serverPortEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
            if (null == args) {
                System.exit(0);
            }
        } else if (R.id.okButton == view.getId()) {
            String ip = serverIpEditText.getText().toString();
            String portStr = serverPortEditText.getText().toString();
            if ((true == StringUtils.isNullOrEmpty(ip)) || (true == StringUtils.isNullOrEmpty(portStr))) {
                Toast.makeText(context, R.string.missing_data_all, Toast.LENGTH_SHORT).show();
            } else {
                int port = Integer.parseInt(portStr);
                if (listener != null) listener.onSetServerConnection(ip, port);
                dismiss();
            }
        }
    }

    public interface OnSetServerConnectionListener {
        void onSetServerConnection(String serverIp, int serverPort);
    }
}
