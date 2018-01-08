package com.em_projects.omdan.main.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.omdan.R;
import com.em_projects.omdan.dialogs.CreateNewRecordDialog;
import com.em_projects.omdan.gallery.ImageGalleryActivity;

/**
 * Created by eyal muchtar on 15/09/2017.
 */

// Ref: https://stackoverflow.com/questions/44164170/android-edittext-with-different-floating-label-and-placeholder
// Ref: https://www.tutorialspoint.com/android/android_camera.htm
// Ref: https://developer.android.com/training/camera/photobasics.html
// Ref: https://stackoverflow.com/questions/42859260/how-to-detect-when-back-button-pressed-in-fragment-android

public class NewRecordFragment extends Fragment implements TextWatcher {
    private static final String TAG = "NewRecordFragment";

    private Context context;

    // Animated Action Buttons
    private TextView recordTitleTextView;
    private boolean FAB_Status = false;
    private View fabButtonsLayout;
    private Button fab;
    private Button cameraButton;
    private Button recordImagesButton;
    private Button archiveImagesButton;
    private View view;

    // UI Components
    private EditText data_1_EditText;
    private EditText data_2_EditText;
    private EditText data_3_EditText;
    private EditText data_4_EditText;
    private EditText data_5_EditText;
    private EditText data_6_EditText;
    private EditText data_7_EditText;
    private EditText data_8_EditText;
    private EditText data_9_EditText;
    private EditText data_10_EditText;
    private EditText data_11_EditText;
    private EditText data_12_EditText;
    private EditText data_13_EditText;
    private EditText data_14_EditText;
    private EditText data_15_EditText;

    private Button okButton;
    private Button cancelButton;

    private boolean applyAutoSave = false;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_new_record, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recordTitleTextView = view.findViewById(R.id.recordTitleTextView);
        initFabButtons(view);
        applyAutoSave = false;
        data_1_EditText = (EditText) view.findViewById(R.id.data_1_EditText);
        data_2_EditText = (EditText) view.findViewById(R.id.data_2_EditText);
        data_3_EditText = (EditText) view.findViewById(R.id.data_3_EditText);
        data_4_EditText = (EditText) view.findViewById(R.id.data_4_EditText);
        data_5_EditText = (EditText) view.findViewById(R.id.data_5_EditText);
        data_6_EditText = (EditText) view.findViewById(R.id.data_6_EditText);
        data_7_EditText = (EditText) view.findViewById(R.id.data_7_EditText);
        data_8_EditText = (EditText) view.findViewById(R.id.data_8_EditText);
        data_9_EditText = (EditText) view.findViewById(R.id.data_9_EditText);
        data_10_EditText = (EditText) view.findViewById(R.id.data_10_EditText);
        data_11_EditText = (EditText) view.findViewById(R.id.data_11_EditText);
        data_12_EditText = (EditText) view.findViewById(R.id.data_12_EditText);
        data_13_EditText = (EditText) view.findViewById(R.id.data_13_EditText);
        data_14_EditText = (EditText) view.findViewById(R.id.data_14_EditText);
        data_15_EditText = (EditText) view.findViewById(R.id.data_15_EditText);
        data_15_EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                okButton.performClick();
                return true;
            }
        });

        this.view = view;

        data_1_EditText.addTextChangedListener(this);
        data_2_EditText.addTextChangedListener(this);
        data_3_EditText.addTextChangedListener(this);
        data_4_EditText.addTextChangedListener(this);
        data_5_EditText.addTextChangedListener(this);
        data_6_EditText.addTextChangedListener(this);
        data_7_EditText.addTextChangedListener(this);
        data_8_EditText.addTextChangedListener(this);
        data_9_EditText.addTextChangedListener(this);
        data_10_EditText.addTextChangedListener(this);
        data_11_EditText.addTextChangedListener(this);
        data_12_EditText.addTextChangedListener(this);
        data_13_EditText.addTextChangedListener(this);
        data_14_EditText.addTextChangedListener(this);
        data_15_EditText.addTextChangedListener(this);

        Bundle args = getArguments();
        if (null != args && args.containsKey("record")) {
            Toast.makeText(context, "טוען תיק", Toast.LENGTH_SHORT).show();
        } else {
            showCreateNewRecordDialog();
        }
    }

    public void createNewRecord() {
        // TODO Implement this method.
        recordTitleTextView.setText(getString(R.string.new_record_id_title, "1000"));
    }

    public void cancelCreation() {
        cleanAllData();
    }

    private void initFabButtons(View view) {
        fabButtonsLayout = view.findViewById(R.id.fabButtonsLayout);
        fab = (Button) view.findViewById(R.id.fab);
        cameraButton = view.findViewById(R.id.fab_camera);
        recordImagesButton = view.findViewById(R.id.fab_rec_images);
        archiveImagesButton = view.findViewById(R.id.fab_archive_images);

        // Init Animations
//        showButtonsAnimation = new TranslateAnimation(
//                Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 1.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f);
//        showButtonsAnimation.setDuration(500);
//        showButtonsAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                fabButtonsLayout.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

//        hideButtonsAnimation = new TranslateAnimation(
//                Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 1.0f);
//        hideButtonsAnimation.setDuration(500);
//        hideButtonsAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                fabButtonsLayout.setVisibility(View.GONE);
////                animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
////                animation.setDuration(1);
////                fabButtonsLayout.startAnimation(animation);
//                fabButtonsLayout.clearAnimation();
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

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
                intent.putExtra("data", getRecordId());
                intent.putExtra("showAsGallery", false);
                startActivity(intent);
            }
        });

        recordImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFab();
                Intent intent = new Intent(context, ImageGalleryActivity.class);
                intent.putExtra("data", getRecordId());
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
    }

    private String getRecordId() {
        return "1000";  // TODO
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

    private void showCreateNewRecordDialog() {
        FragmentManager fm = getChildFragmentManager();
        CreateNewRecordDialog dialog = new CreateNewRecordDialog();
        dialog.setParentFragment(this);
        dialog.show(fm, "CreateNewRecordDialog");
    }

    private void cleanAllData() {
        data_1_EditText.setText("");
        data_2_EditText.setText("");
        data_3_EditText.setText("");
        data_4_EditText.setText("");
        data_5_EditText.setText("");
        data_6_EditText.setText("");
        data_7_EditText.setText("");
        data_8_EditText.setText("");
        data_9_EditText.setText("");
        data_10_EditText.setText("");
        data_11_EditText.setText("");
        data_12_EditText.setText("");
        data_13_EditText.setText("");
        data_14_EditText.setText("");
        data_15_EditText.setText("");
    }

    public void cancelCamera() {
        // TODO
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

    // **************************   TextWatcher methods   ***************************
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        applyAutoSave = true;
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
// **************************   TextWatcher methods   ***************************


    @Override
    public void onPause() {
        super.onPause();
        if (true == applyAutoSave) {
            // TODO
        }
        applyAutoSave = false;
        cleanAllData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
