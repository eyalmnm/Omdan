package com.em_projects.omdan.main.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.omdan.R;
import com.em_projects.omdan.config.Constants;
import com.em_projects.omdan.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by eyalmuchtar on 15/09/2017.
 */

public class FindRecordFragment extends Fragment {
    public static final String TAG = "FindRecordFragment";

    private Context context;
    private FindRecordListener listener;
    // UI Components
    private EditText fileNumberEditText;
    private EditText data_2_EditText;
    private EditText data_3_EditText;
    private EditText data_4_EditText;
    private EditText data_5_EditText;
    private ImageButton searchButton;
    private ImageButton clearAllButton;

    @Override
    public void onAttach(Activity activity) {
        listener = (FindRecordListener) activity;
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_find_record, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fileNumberEditText = (EditText) view.findViewById(R.id.fileNumberEditText);
        data_2_EditText = (EditText) view.findViewById(R.id.data_2_EditText);
        data_3_EditText = (EditText) view.findViewById(R.id.data_3_EditText);
        data_4_EditText = (EditText) view.findViewById(R.id.data_4_EditText);
        data_5_EditText = (EditText) view.findViewById(R.id.data_5_EditText);

        searchButton = (ImageButton) view.findViewById(R.id.searchButton);
        clearAllButton = (ImageButton) view.findViewById(R.id.clearAllButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> dataMap = getAllData();

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (null != dataMap && false == dataMap.isEmpty()) {

                    if (null != listener) {
                        listener.findRecordByData(dataMap);
                    }
                    // clearData();
                } else {
                    Toast.makeText(context, R.string.missing_data, Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileNumberEditText.setText(null);
                data_2_EditText.setText(null);
                data_3_EditText.setText(null);
                data_4_EditText.setText(null);
                data_5_EditText.setText(null);
            }
        });

        fileNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });

        data_2_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });

        data_3_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });

        data_4_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });

        data_5_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });
    }

    private void clearData() {
        fileNumberEditText.setText(null);
        data_2_EditText.setText(null);
        data_3_EditText.setText(null);
        data_4_EditText.setText(null);
        data_5_EditText.setText(null);
    }

    private Map<String, String> getAllData() {
        String fileNumber = fileNumberEditText.getText().toString();
        String data_2 = data_2_EditText.getText().toString();
        String data_3 = data_3_EditText.getText().toString();
        String data_4 = data_4_EditText.getText().toString();
        String data_5 = data_5_EditText.getText().toString();

        if (true == StringUtils.isNullOrEmpty(fileNumber) &&
                true == StringUtils.isNullOrEmpty(data_2) &&
                true == StringUtils.isNullOrEmpty(data_3) &&
                true == StringUtils.isNullOrEmpty(data_4) &&
                true == StringUtils.isNullOrEmpty(data_5)) {
            return null;
        }

        HashMap dataMap = new HashMap(5);
        dataMap.put(Constants.fileNumber, fileNumber);
        dataMap.put("data_2", data_2);
        dataMap.put("data_3", data_3);
        dataMap.put("data_4", data_4);
        dataMap.put("data_5", data_5);

        return dataMap;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface FindRecordListener {
        public void findRecordById(String recNumber);
        public void findRecordByData(Map<String, String> dataMap);
        public void findRecordCancelled();
    }

}
