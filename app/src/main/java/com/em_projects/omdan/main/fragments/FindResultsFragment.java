package com.em_projects.omdan.main.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.em_projects.omdan.R;
import com.em_projects.omdan.config.Dynamics;
import com.em_projects.omdan.main.models.HistoryDataHolder;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by eyalmuchtar on 15/09/2017.
 */

public class FindResultsFragment extends Fragment {
    public static final String TAG = "FindResultsFragment";

    private Context context;
    private FindResultsListener listener;
    // UI Components
    private ListView historyListView;
    // History list components
    private HistoryListAdapter adapter;
    private ArrayList<HistoryDataHolder> historyArrayList;

    @Override
    public void onAttach(Activity activity) {
        listener = (FindResultsListener) activity;
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_find_results, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        historyListView = (ListView) view.findViewById(R.id.historyListView);

        // Init History List View
        historyArrayList = new ArrayList<>();
        Bundle args = getArguments();
        try {
            String data = (String) args.get("data");
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                historyArrayList.add(new HistoryDataHolder(jsonObject.getString("FileNumber")
                        , jsonObject.getString("CreationDate")
                        , jsonObject.getString("Customers.Name")));
            }
        } catch (Exception ex) {
            Log.e(TAG, "onViewCreated", ex);
            FirebaseCrash.logcat(Log.ERROR, TAG, "onViewCreated");
            FirebaseCrash.report(ex);
            FirebaseCrash.log("data: " + args.get("data"));
        }
        adapter = new HistoryListAdapter();
        historyListView.setAdapter(adapter);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                loadRecord(i);
            }
        });

        if (1 == historyArrayList.size()) {
            loadRecord(0);
        }
    }

    private void loadRecord(int position) {
        if (null != listener) {
            Bundle args = new Bundle();
            args.putSerializable("record", historyArrayList.get(position));
            listener.loadRecord(historyArrayList.get(position).getRecord(), args);
            try {
                Dynamics.getInstance(context).setCurrentRecordId(historyArrayList.get(position).getRecord());
            } catch (Exception e) {
                Log.e(TAG, "setOnItemClickListener", e);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface FindResultsListener {
        public void loadRecord(String recNumber, Bundle args);
    }

    private class HistoryListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return historyArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return historyArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (null == view) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.layout_history_item, null);
                viewHolder.recordNumberTextView = (TextView) view.findViewById(R.id.recordNumberTextView);
                viewHolder.recordDateTextView = (TextView) view.findViewById(R.id.recordDateTextView);
                viewHolder.recordTimeTextView = (TextView) view.findViewById(R.id.recordTimeTextView);
                viewHolder.descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            HistoryDataHolder data = historyArrayList.get(i);
            viewHolder.recordNumberTextView.setText(data.getRecord());
            viewHolder.recordDateTextView.setText(data.getDateStr());
            viewHolder.recordTimeTextView.setText(data.getTimeStr());
            viewHolder.descriptionTextView.setText(data.getDescription());


            return view;
        }
    }

    public class ViewHolder {
        public TextView recordNumberTextView;
        public TextView recordDateTextView;
        public TextView recordTimeTextView;
        public TextView descriptionTextView;
    }
}
