package com.em_projects.omdan.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.em_projects.omdan.R;
import com.em_projects.omdan.config.Constants;
import com.em_projects.omdan.utils.JSONUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

// Ref: https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView

public class ImagesListDialog extends DialogFragment {
    private static final String TAG = "ImagesListDialog";

    private ArrayAdapter<String> imagesAdapter = null;
    private ListView imagesListView;

    private ArrayList<String> items = new ArrayList<>();
    private Button closeButton;

    private Context context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        context = getActivity();
        return inflater.inflate(R.layout.dialog_show_images_list, null);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setCancelable(false);
        imagesListView = view.findViewById(R.id.imagesListView);
        closeButton = view.findViewById(R.id.closeButton);

        items = getImagesArray(getArguments());
        if (null != items) {
            imagesAdapter = new ArrayAdapter<String>(context, R.layout.simple_list_item_1, items);
            imagesListView.setAdapter(imagesAdapter);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private ArrayList<String> getImagesArray(Bundle arguments) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(arguments.getString("data"));
            String directory = JSONUtils.getStringValue(jsonObject, Constants.directory);
            String filesListStr = JSONUtils.getStringValue(jsonObject, Constants.filesLists);
            String[] fileList = filesListStr.split(",");
            ArrayList<String> retArray = new ArrayList<String>(Arrays.asList(fileList));
            return retArray;
        } catch (Exception e) {
            Log.e(TAG, "getImagesArray", e);
            FirebaseCrash.logcat(Log.ERROR, TAG, "getImagesArray");
            FirebaseCrash.report(e);
            FirebaseCrash.log("respones: " + jsonObject.toString());
        }
        return null;
    }
}
