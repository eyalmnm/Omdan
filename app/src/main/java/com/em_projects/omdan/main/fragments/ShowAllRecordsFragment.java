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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.omdan.R;
import com.em_projects.omdan.config.Dynamics;
import com.em_projects.omdan.main.models.HistoryDataHolder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by eyalmuchtar on 15/09/2017.
 */

// Ref: https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property

public class ShowAllRecordsFragment extends Fragment {
    private static final String TAG = "ShowAllRecordsFragment";

    private Context context;
    private SelectRecordListener listener;

    // UI Components
    private ListView historyListView;

    // History list components
    private HistoryListAdapter adapter;
    private ArrayList<HistoryDataHolder> historyArrayList;

    // Sorting components
    private Spinner sortingBySpinner;
    private ArrayList<String> categoriesList;
    private ArrayAdapter<String> categoriesAdapter;
    private int currentCategory;

    @Override
    public void onAttach(Activity activity) {
        listener = (SelectRecordListener) activity;
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_show_all_records, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        historyListView = view.findViewById(R.id.historyListView);
//        historyListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        initCategoriesSpinner(view);

        // Init History List View
        historyArrayList = new ArrayList<>();
        adapter = new HistoryListAdapter();
        historyListView.setAdapter(adapter);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick " + i + " is pressed " + historyListView.getCheckedItemPosition());
                try {
                    Dynamics.getInstance(context).setCurrentRecordId(historyArrayList.get(i).getRecord()); // TODO
                } catch (Exception e) {
                    Log.e(TAG, "setOnItemClickListener", e);
                }
            }
        });

        // loadRecordsHistory();   // TODO
    }

    private void initCategoriesSpinner(View view) {
        sortingBySpinner = view.findViewById(R.id.sortingBySpinner);
        categoriesList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.sorting_categories)));
        categoriesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_category_spinner_item, categoriesList);
//        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortingBySpinner.setAdapter(categoriesAdapter);
        currentCategory = 0;
        sortingBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int category = i;
                if (false == (currentCategory == category)) {
                    currentCategory = category;
                    sortByCategory(currentCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-property
    private void sortByCategory(int position) {
        Toast.makeText(getActivity(), "Sorting by: " + categoriesList.get(position), Toast.LENGTH_SHORT).show();
    }

//    private void loadRecordsHistory() {
//        // Load History from db
//        historyArrayList.add(new HistoryDataHolder("1001001", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001002", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001003", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001004", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001005", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001006", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001007", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001008", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001009", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001010", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001011", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001012", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001013", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001014", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001015", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001016", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001017", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001018", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001019", new Date()));
//        historyArrayList.add(new HistoryDataHolder("1001020", new Date()));
//        adapter.notifyDataSetInvalidated();
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface SelectRecordListener {
        public void loadRecord(String recNumber, boolean editable);
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
                viewHolder.recordNumberTextView = view.findViewById(R.id.recordNumberTextView);
                viewHolder.recordDateTextView = view.findViewById(R.id.recordDateTextView);
                viewHolder.recordTimeTextView = view.findViewById(R.id.recordTimeTextView);
                viewHolder.insuredListNameTextView = view.findViewById(R.id.insuredListNameTextView);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            HistoryDataHolder data = historyArrayList.get(i);
            viewHolder.recordNumberTextView.setText(data.getRecord());
            viewHolder.recordDateTextView.setText(data.getDateStr());
            viewHolder.recordTimeTextView.setText(data.getTimeStr());
            viewHolder.insuredListNameTextView.setText(data.getInsuredListName());

            return view;
        }
    }

    public class ViewHolder {
        TextView recordNumberTextView;
        TextView recordDateTextView;
        TextView recordTimeTextView;
        TextView insuredListNameTextView;
    }
}
