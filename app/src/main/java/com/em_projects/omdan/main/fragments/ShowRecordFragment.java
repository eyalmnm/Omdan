package com.em_projects.omdan.main.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.omdan.R;
import com.em_projects.omdan.config.Constants;
import com.em_projects.omdan.config.Dynamics;
import com.em_projects.omdan.config.Errors;
import com.em_projects.omdan.dialogs.ImagesListDialog;
import com.em_projects.omdan.dialogs.RequestImagesListDialog;
import com.em_projects.omdan.gallery.ImageGalleryActivity;
import com.em_projects.omdan.main.MainScreenActivity;
import com.em_projects.omdan.main.models.HistoryDataHolder;
import com.em_projects.omdan.network.CommListener;
import com.em_projects.omdan.network.ServerUtilities;
import com.em_projects.omdan.utils.ErrorsUtils;
import com.em_projects.omdan.utils.JSONUtils;
import com.em_projects.omdan.utils.StringUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONObject;

/**
 * Created by eyalmuchtar on 15/09/2017.
 */

// Ref: https://stackoverflow.com/questions/42859260/how-to-detect-when-back-button-pressed-in-fragment-android

public class ShowRecordFragment extends Fragment implements RequestImagesListDialog.OnImagesListRequestListener {
    private static final String TAG = "ShowRecordFragment";

    private Context context;

    private HistoryDataHolder recData;

    // Form Fields
    private TextView recordTitleTextView;
    private TextView insuredListNameTextView;
    private TextView recordDateTextView;
    private TextView recordTimeTextView;
    private TextView customerNameTextView;
    private TextView employeeNameTextView;
    private TextView suitNameTextView;
    private TextView fileStatusNameTextView;

    // Animated Action Buttons
    private boolean FAB_Status = false;
    private View fabButtonsLayout;
    private Button cameraButton;
    private Button recordImagesButton;
    private Button archiveImagesButton;
    private Button fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_show_record, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        recordTitleTextView = view.findViewById(R.id.recordTitleTextView);
        insuredListNameTextView = view.findViewById(R.id.insuredListNameTextView);
        recordDateTextView = view.findViewById(R.id.recordDateTextView);
        recordTimeTextView = view.findViewById(R.id.recordTimeTextView);
        customerNameTextView = view.findViewById(R.id.customerNameTextView);
        employeeNameTextView = view.findViewById(R.id.employeeNameTextView);
        suitNameTextView = view.findViewById(R.id.suitNameTextView);
        fileStatusNameTextView = view.findViewById(R.id.fileStatusNameTextView);

        fabButtonsLayout = view.findViewById(R.id.fabButtonsLayout);
        fab = (Button) view.findViewById(R.id.fab);
        cameraButton = view.findViewById(R.id.fab_camera);
        recordImagesButton = view.findViewById(R.id.fab_rec_images);
        archiveImagesButton = view.findViewById(R.id.fab_archive_images);

        recordTitleTextView.setText(getString(R.string.new_record_id_title, getRecordId()));

        Bundle args = getArguments();
        if (null != args) {
            recData = (HistoryDataHolder) args.getSerializable("record");
            insuredListNameTextView.setText(recData.getInsuredListName());
            recordDateTextView.setText(recData.getDateStr());
            recordTimeTextView.setText(recData.getTimeStr());
            customerNameTextView.setText(recData.getCustomersName());
            employeeNameTextView.setText(recData.getEmployeeListName());
            suitNameTextView.setText(recData.getSuitNumber());
            fileStatusNameTextView.setText(recData.getFileStatusName());
            Log.d(TAG, "Load Record Data succss");
        } else {
            Log.e(TAG, "Load Record Data Failed");  // TODO Implement this method
        }

        fabButtonsLayout.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFab();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFab();
                Intent intent = new Intent(context, ImageGalleryActivity.class);
                intent.putExtra("data", getRecordId()); // recData.getRecordId()); // TODO
                intent.putExtra("showAsGallery", false);
                startActivity(intent);
            }
        });

        recordImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFab();
                Intent intent = new Intent(context, ImageGalleryActivity.class);
                intent.putExtra("data", getRecordId()); // recData.getRecordId()); // TODO
                intent.putExtra("showAsGallery", true);
                startActivity(intent);
            }
        });

        archiveImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Get images from server for current record", Toast.LENGTH_LONG).show();
            }
        });

        showImagesListRequestDialog();
    }

    private void showImagesListRequestDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        RequestImagesListDialog dialog = new RequestImagesListDialog();
        dialog.setListener(this);
        dialog.show(fragmentManager, "RequestImagesListDialog");
    }

    @Override
    public void onImagesListRequest() {
        showImagesList();
    }

    public void showImagesList() {
        ServerUtilities.getInstance().getAllFiles(getRecordId(), new CommListener() {
            @Override
            public void newDataArrived(String response) {
                try {
                    Log.d(TAG, "showImagesList -> newDataArrived response: " + response);
                    if (response.contains(Constants.error)) {
                        int errorNo = ErrorsUtils.getError(response);
                        if (Errors.USER_NOT_LOGGED_IN == errorNo || Errors.USER_NOT_FOUND == errorNo) {
                            ((MainScreenActivity) getActivity()).showLoginDialog();
                            return;
                        } else {
                            if (Errors.TARGET_DIRECTORY_NOT_FOUND == errorNo) {
                                showToast(context.getString(R.string.directory_not_found));
                            }
                            return;
                        }
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        String fileListStr = JSONUtils.getStringValue(jsonObject, Constants.filesLists);
                        if (true == StringUtils.isNullOrEmpty(fileListStr)) {
                            // Display no results found dialog and stay in find record screen.
                            showToast(context.getString(R.string.directory_contains_no_images));
                        } else {
                            Bundle bundle = new Bundle(); // JSONUtils.convertJsonObject2Bundle(jsonObject);
                            bundle.putString("data", response);
                            showImagesListDialog(bundle);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "showImagesList -> newDataArrived", e);
                    FirebaseCrash.logcat(Log.ERROR, TAG, "showImagesList -> newDataArrived");
                    FirebaseCrash.log("response: " + response);
                    FirebaseCrash.report(e);
                }
            }

            @Override
            public void exceptionThrown(Throwable throwable) {
                Log.e(TAG, "showImagesList -> exceptionThrown", throwable);
                FirebaseCrash.logcat(Log.ERROR, TAG, "showImagesList -> exceptionThrown");
                FirebaseCrash.report(throwable);
            }
        });
    }

    private void showImagesListDialog(Bundle bundle) {
        FragmentManager fragmentManager = getFragmentManager();
        ImagesListDialog dialog = new ImagesListDialog();
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, "ImagesListDialog");
    }

    private void showToast(final String message) {
        new Handler(getActivity().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getRecordId() {
        return Dynamics.getInstance(context).getCurrentRecordId();
    }

    @Override
    //Pressed return button - returns to the results menu
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (true == FAB_Status) {
                        fab.performClick();
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
    }

    private void toggleFab() {
        Log.d(TAG, "toggleFab");
        if (FAB_Status == false) {
            //Display FAB menu
            expandFAB();
            FAB_Status = true;
        } else {
            //Close FAB menu
            hideFAB();
            FAB_Status = false;
        }
    }

    private void expandFAB() {
        Log.d(TAG, "expandFAB");
        fabButtonsLayout.setVisibility(View.VISIBLE);
//        fabButtonsLayout.startAnimation(showButtonsAnimation);
    }

    public void hideFAB() {
        Log.d(TAG, "hideFAB");
        fabButtonsLayout.setVisibility(View.GONE);
//        fabButtonsLayout.startAnimation(hideButtonsAnimation);
    }
}
