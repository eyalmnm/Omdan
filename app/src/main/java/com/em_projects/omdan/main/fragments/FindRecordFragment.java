package com.em_projects.omdan.main.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
import com.em_projects.omdan.utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by eyalmuchtar on 15/09/2017.
 */

public class FindRecordFragment extends Fragment implements DatePickerDialog.OnDatePickedListener {
    public static final String TAG = "FindRecordFragment";

    private Context context;
    private FindRecordListener listener;
    // UI Components
    private EditText fileNumberEditText;
    private EditText insuredNameEditText;
    private EditText customerEditText;
    private EditText employeeEditText;
    private EditText suitNumberEditText;
    private EditText fileStatusEditText;
    private TextView creationDateTextView;
    private ImageButton searchButton;
    private ImageButton clearAllButton;

    // UI Helper
    private long creationDate = 0;


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
        long now = System.currentTimeMillis();
        fileNumberEditText = (EditText) view.findViewById(R.id.fileNumberEditText);
        insuredNameEditText = (EditText) view.findViewById(R.id.insuredNameEditText);
        customerEditText = (EditText) view.findViewById(R.id.customerEditText);
        employeeEditText = (EditText) view.findViewById(R.id.employeeEditText);
        suitNumberEditText = (EditText) view.findViewById(R.id.suitNumberEditText);
        fileStatusEditText = (EditText) view.findViewById(R.id.fileStatusEditText);
        creationDateTextView = (TextView) view.findViewById(R.id.creationDateTextView);

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
                } else {
                    Toast.makeText(context, R.string.missing_data, Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileNumberEditText.setText(null);
                insuredNameEditText.setText(null);
                customerEditText.setText(null);
                employeeEditText.setText(null);
                suitNumberEditText.setText(null);
                fileStatusEditText.setText(null);
                creationDateTextView.setText(null);
            }
        });

        fileNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });

        insuredNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });

        customerEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });

        employeeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });

        suitNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });

        fileStatusEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchButton.performClick();
                return true;
            }
        });

        creationDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });
    }

    private void clearData() {
        fileNumberEditText.setText(null);
        insuredNameEditText.setText(null);
        customerEditText.setText(null);
        employeeEditText.setText(null);
        suitNumberEditText.setText(null);
        fileStatusEditText.setText(null);
        creationDateTextView.setText(null);
    }

    private Map<String, String> getAllData() {
        String fileNumber = fileNumberEditText.getText().toString();
        String insuredName = insuredNameEditText.getText().toString();
        String customerName = customerEditText.getText().toString();
        String employeeName = employeeEditText.getText().toString();
        String suitNumber = suitNumberEditText.getText().toString();
        String fileStatus = fileStatusEditText.getText().toString();
        String creationDate = creationDateTextView.getText().toString();

        if (true == StringUtils.isNullOrEmpty(fileNumber) &&
                true == StringUtils.isNullOrEmpty(insuredName) &&
                true == StringUtils.isNullOrEmpty(customerName) &&
                true == StringUtils.isNullOrEmpty(employeeName) &&
                true == StringUtils.isNullOrEmpty(suitNumber) &&
                true == StringUtils.isNullOrEmpty(fileStatus) &&
                true == StringUtils.isNullOrEmpty(creationDate)) {
            return null;
        }

        HashMap dataMap = new HashMap(6);
        dataMap.put(Constants.fileNumber, fileNumber);
        dataMap.put(Constants.insuredName, insuredName);
        dataMap.put(Constants.customer, customerName);
        dataMap.put(Constants.employee, employeeName);
        dataMap.put(Constants.suitNumber, suitNumber);
        dataMap.put(Constants.fileStatus, fileStatus);
        dataMap.put(Constants.creationDate, (0 >= this.creationDate) ? "" : String.valueOf(this.creationDate));

        return dataMap;
    }

    private void openDatePickerDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        DatePickerDialog datePickerDialog = new DatePickerDialog();
        datePickerDialog.show(fragmentManager, "DatePickerDialog");
    }

    // DatePickerDialog.OnDatePickedListener implementation
    @Override
    public void onDatePicked(Date date) {
        creationDateTextView.setText(TimeUtils.getDateStr(date));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        creationDate = date.getTime();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface FindRecordListener {
        void findRecordById(String recNumber);

        void findRecordByData(Map<String, String> dataMap);

        void findRecordCancelled();
    }

}
