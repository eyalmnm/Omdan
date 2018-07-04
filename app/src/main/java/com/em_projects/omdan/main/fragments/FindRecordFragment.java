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
import com.em_projects.omdan.config.Dynamics;
import com.em_projects.omdan.utils.StringUtils;
import com.em_projects.omdan.utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;


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
    private TextView creationDateStartTextView;
    private TextView creationDateEndTextView;
    private ImageButton searchButton;
    private ImageButton clearAllButton;

    // UI Helper
    private Calendar creationDateStartCalendar = null;
    private Calendar creationDateEndCalendar = null;


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
        insuredNameEditText = (EditText) view.findViewById(R.id.insuredNameEditText);
        customerEditText = (EditText) view.findViewById(R.id.customerEditText);
        employeeEditText = (EditText) view.findViewById(R.id.employeeEditText);
        suitNumberEditText = (EditText) view.findViewById(R.id.suitNumberEditText);
        fileStatusEditText = (EditText) view.findViewById(R.id.fileStatusEditText);
        creationDateStartTextView = (TextView) view.findViewById(R.id.creationDateStartTextView);
        creationDateEndTextView = (TextView) view.findViewById(R.id.creationDateEndTextView);
        restoreData(Dynamics.saveSearchCriteria);

        searchButton = (ImageButton) view.findViewById(R.id.searchButton);
        clearAllButton = (ImageButton) view.findViewById(R.id.clearAllButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSearchCriteria();
                String fileNumber = fileNumberEditText.getText().toString();
                String insuredName = insuredNameEditText.getText().toString();
                String customerName = customerEditText.getText().toString();
                String employeeName = employeeEditText.getText().toString();
                String suitNumber = suitNumberEditText.getText().toString();
                String fileStatus = fileStatusEditText.getText().toString();
                String creationDateStart = creationDateStartTextView.getText().toString();
                String creationDateEnd = creationDateEndTextView.getText().toString();

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (false == StringUtils.isNullOrEmpty(fileNumber) ||
                        false == StringUtils.isNullOrEmpty(insuredName) ||
                        false == StringUtils.isNullOrEmpty(customerName) ||
                        false == StringUtils.isNullOrEmpty(employeeName) ||
                        false == StringUtils.isNullOrEmpty(suitNumber) ||
                        false == StringUtils.isNullOrEmpty(fileStatus) ||
                        false == StringUtils.isNullOrEmpty(creationDateStart) ||
                        false == StringUtils.isNullOrEmpty(creationDateEnd)) {
                    if (null != listener) {
                        listener.findRecordByData(fileNumber, insuredName, customerName, employeeName, suitNumber, fileStatus,
                                (null == creationDateStartCalendar) ? "" : String.valueOf(creationDateStartCalendar.getTimeInMillis()),
                                (null == creationDateEndCalendar) ? "" : String.valueOf(creationDateEndCalendar.getTimeInMillis()));
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
                creationDateStartTextView.setText(null);
                creationDateEndTextView.setText(null);
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

        creationDateStartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog(true);
            }
        });

        creationDateEndTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog(false);
            }
        });
    }

    private void restoreData(Bundle saveSearchCriteria) {
        if (null != saveSearchCriteria) {
            fileNumberEditText.setText(saveSearchCriteria.getString("fileNumber"));
            insuredNameEditText.setText(saveSearchCriteria.getString("insuredName"));
            customerEditText.setText(saveSearchCriteria.getString("customerName"));
            employeeEditText.setText(saveSearchCriteria.getString("employeeName"));
            suitNumberEditText.setText(saveSearchCriteria.getString("suitNumber"));
            fileStatusEditText.setText(saveSearchCriteria.getString("fileStatus"));
            creationDateStartTextView.setText(saveSearchCriteria.getString("creationDateStart"));
            creationDateEndTextView.setText(saveSearchCriteria.getString("creationDateEnd"));
            long creationDateStartL = saveSearchCriteria.getLong("creationDateStartL");
            long creationDateEndL = saveSearchCriteria.getLong("creationDateEndL");
            if (0 < creationDateStartL) {
                creationDateStartCalendar = Calendar.getInstance();
                creationDateStartCalendar.setTimeInMillis(creationDateStartL);
            }
            if (0 < creationDateEndL) {
                creationDateEndCalendar = Calendar.getInstance();
                creationDateEndCalendar.setTimeInMillis(creationDateEndL);
            }
        }
        Dynamics.saveSearchCriteria = null;
    }

    public void saveSearchCriteria() {
        Bundle outState = new Bundle();
        outState.putString("fileNumber", fileNumberEditText.getText().toString());
        outState.putString("insuredName", insuredNameEditText.getText().toString());
        outState.putString("customerName", customerEditText.getText().toString());
        outState.putString("employeeName", employeeEditText.getText().toString());
        outState.putString("suitNumber", suitNumberEditText.getText().toString());
        outState.putString("fileStatus", fileStatusEditText.getText().toString());
        outState.putString("creationDateStart", creationDateStartTextView.getText().toString());
        outState.putString("creationDateEnd", creationDateEndTextView.getText().toString());
        outState.putLong("creationDateStartL", null == creationDateStartCalendar ? 0 : creationDateStartCalendar.getTimeInMillis());
        outState.putLong("creationDateEndL", null == creationDateEndCalendar ? 0 : creationDateEndCalendar.getTimeInMillis());
        Dynamics.saveSearchCriteria = outState;
    }

    private void clearData() {
        fileNumberEditText.setText(null);
        insuredNameEditText.setText(null);
        customerEditText.setText(null);
        employeeEditText.setText(null);
        suitNumberEditText.setText(null);
        fileStatusEditText.setText(null);
        creationDateStartTextView.setText(null);
        creationDateEndTextView.setText(null);
    }


    private void openDatePickerDialog(final boolean isStartDate) {
//        FragmentManager fragmentManager = getFragmentManager();
//        DatePickerDialog datePickerDialog = new DatePickerDialog();
//        Bundle args = new Bundle();
//        args.putBoolean("isStartDate", isStartDate);
//        datePickerDialog.setArguments(args);
//        datePickerDialog.show(fragmentManager, "DatePickerDialog");
        Calendar tempCal = Calendar.getInstance();
        tempCal.setTimeInMillis(System.currentTimeMillis());
        int year = tempCal.get(Calendar.YEAR);
        int month = tempCal.get(Calendar.MONTH);
        int day = tempCal.get(Calendar.DAY_OF_MONTH);
        if (true == isStartDate && null != creationDateStartCalendar) {
            year = creationDateStartCalendar.get(Calendar.YEAR);
            month = creationDateStartCalendar.get(Calendar.MONTH);
            day = creationDateStartCalendar.get(Calendar.DAY_OF_MONTH);
        } else if (null != creationDateEndCalendar) {
            year = creationDateEndCalendar.get(Calendar.YEAR);
            month = creationDateEndCalendar.get(Calendar.MONTH);
            day = creationDateEndCalendar.get(Calendar.DAY_OF_MONTH);
        }
        com.android.datetimepicker.date.DatePickerDialog.newInstance(
                new com.android.datetimepicker.date.DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(com.android.datetimepicker.date.DatePickerDialog dialog,
                                          int year, int monthOfYear, int dayOfMonth) {
                        if (isStartDate) {
                            if (null == creationDateStartCalendar)
                                creationDateStartCalendar = Calendar.getInstance();
                            creationDateStartCalendar.set(year, monthOfYear, dayOfMonth);
                            creationDateStartTextView.setText(TimeUtils.getDateStr(creationDateStartCalendar.getTime()));
                        } else {
                            if (null == creationDateEndCalendar)
                                creationDateEndCalendar = Calendar.getInstance();
                            creationDateEndCalendar.set(year, monthOfYear, dayOfMonth);
                            creationDateEndTextView.setText(TimeUtils.getDateStr(creationDateEndCalendar.getTime()));
                        }
                    }
                }, year, month, day).show(getFragmentManager(), "datePicker");
    }

    // DatePickerDialog.OnDatePickedListener implementation
    @Override
    public void onDatePicked(Date date, boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (true == isStartDate) {
            if (null == creationDateStartCalendar)
                creationDateStartCalendar = Calendar.getInstance();
            creationDateStartCalendar.setTime(date);
            creationDateStartTextView.setText(TimeUtils.getDateStr(date));
        } else {
            if (null == creationDateEndCalendar) creationDateEndCalendar = Calendar.getInstance();
            creationDateEndCalendar.setTime(date);
            creationDateEndTextView.setText(TimeUtils.getDateStr(date));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface FindRecordListener {
        void findRecordById(String recNumber);

        void findRecordByData(String fileNumber, String insuredName, String customer, String employee,
                              String suitNumber, String fileStatus, String creationDateStart, String creationDateEnd);

        void findRecordCancelled();
    }

}
