package com.em_projects.omdan.main.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.omdan.R;
import com.em_projects.omdan.config.Constants;
import com.em_projects.omdan.config.Dynamics;
import com.em_projects.omdan.gallery.ImageGalleryActivity;
import com.em_projects.omdan.utils.FileUtils;
import com.em_projects.omdan.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by eyalmuchtar on 15/09/2017.
 */

// Ref: https://stackoverflow.com/questions/25280662/how-to-highlight-selected-gridview-items
// Ref: http://www.java2s.com/Code/Android/UI/ThisdemoillustratestheuseofCHOICEMODEMULTIPLEMODALakaselectionmodeonGridView.htm


// Ref: http://www.devexchanges.info/2016/01/gridview-with-multiple-selection-in.html     <-------------<<<-

public class OpenGaleryFragment extends Fragment {
    private static final String TAG = "OpenGaleryFragment";

    private ListView galeriesListView;
    private ArrayList<String> directories = new ArrayList<>();
    private GalleryListAdapter adapter;

    private File baseDir;
    private Context context;

//    private boolean fromStart = false;

//    @Override
//    public void onAttach(Context context) {
//        Log.d(TAG, "onAttach");
//        super.onAttach(context);
//        fromStart = true;
//        callGalleryActivity();
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        context = getActivity();
        return inflater.inflate(R.layout.fragment_open_galery, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        galeriesListView = view.findViewById(R.id.galleriesListView);
        adapter = new GalleryListAdapter();
        galeriesListView.setAdapter(adapter);
        galeriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Intent intent = new Intent(getActivity(), ImageGalleryActivity.class);
                    intent.putExtra("data", Dynamics.getInstance(context).getCurrentRecordId() + directories.get(i));
                    intent.putExtra("showAsGallery", true);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "onItemClick", e);
                }
            }
        });

        String currentRecord = Dynamics.getInstance(context).getCurrentRecordId();
        if (false == StringUtils.isNullOrEmpty(currentRecord)) {
            currentRecord = File.separator + currentRecord;
        } else {
            currentRecord = "";
        }
        baseDir = new File(Constants.BASE_PATH + currentRecord);
        if (true == baseDir.exists()) {
            directoriesLoader();
        } else {
            baseDir.mkdirs();
            Toast.makeText(getActivity(), R.string.empty_archive_msg, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ImageGalleryActivity.class);
            intent.putExtra("data", currentRecord);
            intent.putExtra("showAsGallery", true);
            startActivity(intent);
        }
    }

//    private void callGalleryActivity() {
//        Log.d(TAG, "callGalleryActivity");
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "callGalleryActivity - run");
//                Intent intent = new Intent(getActivity(), ImageGalleryActivity.class);
//                intent.putExtra("data", "");
//                intent.putExtra("isFromGallery", true);
//                startActivity(intent);
//            }
//        });
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume fromStart: " + fromStart);
//        if(true == fromStart) {
//            fromStart = false;
//            return;
//        } else {
//            getActivity().onBackPressed();
//        }
//    }

    public void refresh() {
        Log.d(TAG, "refresh");
        directoriesLoader();
    }

    private void directoriesLoader() {
        Log.d(TAG, "DirectoriesLoader");
        final Activity parentActivity = getActivity();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ProgressDialog dialog = ProgressDialog.show(context, "", "טוען...", true, false);
                try {
                    FileUtils.findDirectories(baseDir, "", directories);
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (RuntimeException e) {
                    Log.e(TAG, "DirectoriesLoader", e);
                }
                if (null != dialog) {
                    dialog.dismiss();
                }
            }
        });
    }

    private class GalleryListAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public GalleryListAdapter() {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return directories.size();
        }

        @Override
        public Object getItem(int i) {
            return directories.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (null == view) {
                view = inflater.inflate(R.layout.simple_list_item_1, null);
            }
            TextView theTextView = view.findViewById(R.id.theTextView);
            theTextView.setText(Dynamics.getInstance(context).getCurrentRecordId() + directories.get(i).trim() + "");
            return view;
        }
    }
}
