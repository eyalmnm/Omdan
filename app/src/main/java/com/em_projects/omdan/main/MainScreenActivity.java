package com.em_projects.omdan.main;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.em_projects.omdan.R;
import com.em_projects.omdan.config.Constants;
import com.em_projects.omdan.config.Dynamics;
import com.em_projects.omdan.config.Errors;
import com.em_projects.omdan.dialogs.AppExitDialog;
import com.em_projects.omdan.dialogs.LoginDialog;
import com.em_projects.omdan.dialogs.LoginFailedDialog;
import com.em_projects.omdan.dialogs.ServerConnectionDialog;
import com.em_projects.omdan.main.fragments.FindRecordFragment;
import com.em_projects.omdan.main.fragments.FindResultsFragment;
import com.em_projects.omdan.main.fragments.NewRecordFragment;
import com.em_projects.omdan.main.fragments.OpenGaleryFragment;
import com.em_projects.omdan.main.fragments.ShowAllRecordsFragment;
import com.em_projects.omdan.main.fragments.ShowRecordFragment;
import com.em_projects.omdan.main.models.Setting;
import com.em_projects.omdan.network.CommListener;
import com.em_projects.omdan.network.ServerUtilities;
import com.em_projects.omdan.utils.ErrorsUtils;
import com.em_projects.omdan.utils.PreferencesUtils;
import com.em_projects.omdan.utils.StringUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WAKE_LOCK;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

// https://stackoverflow.com/questions/19766402/dialogfragment-remove-transparent-black
// https://developer.android.com/training/implementing-navigation/nav-drawer.html
// http://www.android4devs.com/2014/12/how-to-make-material-design-navigation-drawer.html
// https://www.journaldev.com/9958/android-navigation-drawer-example-tutorial   <-----------<<<-

/**
 * Created by eyalmuchtar on 13/09/2017.
 */

public class MainScreenActivity extends AppCompatActivity implements FindRecordFragment.FindRecordListener,
        ShowAllRecordsFragment.SelectRecordListener, FindResultsFragment.FindResultsListener,
        ServerConnectionDialog.OnSetServerConnectionListener, LoginDialog.OnSetLoginDataListener,
        LoginFailedDialog.OnLoginFailedListener {

    // Setting IDs
    public static final int FIND_RECORD = 100;
    public static final int SHOW_RECORD = 101;
    public static final int NEW_RECORD = 102;
    public static final int SHOW_ALL_RECORDS = 103;
    public static final int OPEN_GALERY = 104;
    public static final int FIND_RESULTS = 105;
    private static final String TAG = "MainScreenActivity";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Context context;

//    private SearchView searchView;

    private PowerManager.WakeLock wakeLock;

    private DrawerLayout settingLayout;
    private ListView left_drawer;
    private ArrayList<Setting> settings;
    private Toolbar toolbar;
    private CharSequence drawerTitle;
    private CharSequence title;
    private android.support.v7.app.ActionBarDrawerToggle drawerToggle;
    private View view;
    private ImageView vailImageView;

    // Horizontal menu buttons
    private Button openArchiveButton;
    private Button openGaleryButton;
    private Button allRecordsButton;
    private Button newRecordButton;

    private ProgressDialog progressDialog;

    // Hold the current RecordId
    private String currentRecordId; // TODO use this to hold the current Record id.

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main_screen);
        title = drawerTitle = getTitle();

        context = this;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        settingLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        left_drawer = (ListView) findViewById(R.id.left_drawer);
        view = settingLayout;

        currentRecordId = Dynamics.getInstance(context).getCurrentRecordId();

        setupToolbar();

        settings = new ArrayList<Setting>();
        settings.add(new Setting(getString(R.string.find_record), R.mipmap.ic_launcher_round, FIND_RECORD));
        settings.add(new Setting(getString(R.string.show_record), R.mipmap.ic_launcher_round, SHOW_RECORD));
        settings.add(new Setting(getString(R.string.new_record), R.mipmap.ic_launcher_round, NEW_RECORD));
        settings.add(new Setting(getString(R.string.show_all), R.mipmap.ic_launcher_round, SHOW_ALL_RECORDS));
        settings.add(new Setting(getString(R.string.open_galery), R.mipmap.ic_launcher_round, OPEN_GALERY));

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        left_drawer.setAdapter(new SettingAdapter());
        left_drawer.setOnItemClickListener(new DrawerItemClickListener());
        setupDrawerToggle();
        settingLayout.setDrawerListener(drawerToggle);

        vailImageView = findViewById(R.id.vailImageView);

        initScreen();

        initHorizontalMenu();

        // Check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions()) {
                requestPermission();
            } else {
                //continueLoading();
            }
        } else {
            //continueLoading();
        }

        // Make sure the view adjust while showing keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void initHorizontalMenu() {
        openArchiveButton = findViewById(R.id.openArchiveButton);
        openGaleryButton = findViewById(R.id.openGaleryButton);
        allRecordsButton = findViewById(R.id.allRecordsButton);
        newRecordButton = findViewById(R.id.newRecordButton);

        openArchiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItem(3, null);
            }
        });

        openGaleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItem(4, null);
            }
        });

        allRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItem(0, null);
            }
        });

        newRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItem(2, null);
            }
        });
    }

    private void initScreen() {
        Fragment fragment = new FindRecordFragment();
        // Insert the fragment by replacing any existing fragment
        // TODO Add Current Record Id
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private boolean checkPermissions() {
        int cameraRes = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int locationRes = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int locationCRes = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int internetRes = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int readContactRes = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int wakeLockRes = ContextCompat.checkSelfPermission(getApplicationContext(), WAKE_LOCK);
        int writeFileRes = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int readFilekRes = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return cameraRes == PackageManager.PERMISSION_GRANTED &&
                locationRes == PackageManager.PERMISSION_GRANTED &&
                locationCRes == PackageManager.PERMISSION_GRANTED &&
                internetRes == PackageManager.PERMISSION_GRANTED &&
                readContactRes == PackageManager.PERMISSION_GRANTED &&
                wakeLockRes == PackageManager.PERMISSION_GRANTED &&
                writeFileRes == PackageManager.PERMISSION_GRANTED &&
                readFilekRes == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET, READ_CONTACTS, WAKE_LOCK, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean cameraRes = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean locationRes = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean locationCRes = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean internetRes = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean contectRes = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean wakeLockRes = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean writeFileRes = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean readFileRes = grantResults[7] == PackageManager.PERMISSION_GRANTED;

                    if (cameraRes && locationRes && locationCRes && internetRes && contectRes &&
                            wakeLockRes && writeFileRes && readFileRes) {
                        Snackbar.make(view, "Permission Granted, Now you can use the application.", Snackbar.LENGTH_LONG).show();
                        //continueLoading();
                    } else {
                        Snackbar.make(view, "Permission Denied, You cannot access the application.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (hasPermissions(context, CAMERA, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET, READ_CONTACTS, WAKE_LOCK, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET, READ_CONTACTS, WAKE_LOCK, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
        }
    }

    private void continueLoading() {
        hideVail();
        Dynamics.serverURL = PreferencesUtils.getInstance(context).getServerConncetionString();
        if (null == Dynamics.serverURL) {
            showServerConnectionDialog();
            return;
        }
        if (true == StringUtils.isNullOrEmpty(Dynamics.uUID)) {
            showLoginDialog();
            return;
        }
    }

    private void showLoginDialog() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showVail();
                FragmentManager fm = getFragmentManager();
                LoginDialog dialog = new LoginDialog();
                try {
                    dialog.show(fm, "LoginDialog");
                } catch (Throwable e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "showLoginDialog");
                    FirebaseCrash.report(e);
                    hideVail();
                }
            }
        }, 500);
    }

    private void showServerConnectionDialog() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showVail();
                FragmentManager fm = getFragmentManager();
                ServerConnectionDialog dialog = new ServerConnectionDialog();
                if (false == StringUtils.isNullOrEmpty(Dynamics.serverURL)) {
                    Bundle args = new Bundle();
                    args.putString("serverIp", Dynamics.getServerIp());
                    args.putInt("serverPort", Dynamics.getServerPort());
                    dialog.setArguments(args);
                }
                try {
                    dialog.show(fm, "ServerConnectionDialog");
                } catch (Throwable e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "ServerConnectionDialog");
                    FirebaseCrash.report(e);
                    hideVail();
                }
            }
        }, 500);
    }

    private void showVail() {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                vailImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideVail() {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                vailImageView.setVisibility(View.GONE);
            }
        });
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        searchView = toolbar.findViewById(R.id.searchView);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // filter by (newText);
//                return false;
//            }
//        });
    }

    void setupDrawerToggle() {
        drawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, settingLayout, toolbar, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        drawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != wakeLock && false == wakeLock.isHeld()) {
            try {
                wakeLock.acquire();
            } catch (SecurityException e) {
                Log.e(TAG, "onResume");
            }
        }
        continueLoading();
    }

    @Override
    protected void onPause() {
        if (null != wakeLock && true == wakeLock.isHeld()) {
            try {
                wakeLock.release();
            } catch (SecurityException e) {
                Log.e(TAG, "onPause");
            }
        }
        super.onPause();
    }

    /**
     * Swaps fragments in the main content view
     * if args is null then show all otherwise show the related data
     */
    private void selectItem(int position , Bundle args) {
        Fragment fragment = null;
        boolean addToBackStack = false;
        switch (settings.get(position).getId()) {
            case FIND_RECORD:
                fragment = new FindRecordFragment();
                break;
            case SHOW_RECORD:
                fragment = new ShowRecordFragment();
                break;
            case NEW_RECORD:
                fragment = new NewRecordFragment();
                break;
            case SHOW_ALL_RECORDS:
                fragment = new ShowAllRecordsFragment();
                break;
            case OPEN_GALERY:
                fragment = new OpenGaleryFragment();
                break;
//            case FIND_RESULTS:
//                fragment = new FindResultsFragment();
//                break;
            default:
                fragment = null;
                break;
        }

        // TODO Add Current Record Id
        if (null != fragment) {
            if (null != args) {
                fragment.setArguments(args);
            }
            showFragment(fragment, addToBackStack);
        }
        settingLayout.closeDrawer(left_drawer);
    }

    private void showFragment(Fragment fragment, boolean addToBackStack) {
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, fragment);
        if (true == addToBackStack) {
            ft.addToBackStack("fragment");
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

    }

    // FindRecordFragment.FindRecordListener
    @Override
    public void findRecordById(String recNumber) {
        Log.d(TAG, "findRecordById " + recNumber + " selected");
        showRecord(recNumber);
    }

    @Override
    public void findRecordByData(Map<String, String> dataMap) {
        Log.d(TAG, "findRecordByData");
        String fileNumber = dataMap.get(Constants.fileNumber);
        if (true == StringUtils.isNullOrEmpty(fileNumber)) return;
        showProgressDialog();
        ServerUtilities.getInstance().findFiles(fileNumber, new CommListener() {
            @Override
            public void newDataArrived(String response) {
                try {
                    Log.d(TAG, "findRecordByData -> newDataArrived response: " + response);
                    if (response.contains(Constants.error)) {
                        int errorNo = ErrorsUtils.getError(response);
                        if (Errors.USER_NOT_LOGGED_IN == errorNo || Errors.USER_NOT_FOUND == errorNo) {
                            showLoginDialog();
                        } else {
                            if (Errors.FILE_NOT_FOUND == errorNo) {
                                showToast(context.getString(R.string.file_not_found));
                            }
                        }
                        return;
                    }
                    JSONArray jsonArray = new JSONArray(response);
                    if (0 == jsonArray.length()) {
                        // Display no results found dialog and stay in find record screen.
                        showToast(context.getString(R.string.file_not_found));
                    } else {
                        Bundle bundle = new Bundle(); // JSONUtils.convertJsonObject2Bundle(jsonObject);
                        bundle.putString("data", response);
                        showFindResults(bundle);
                    }
                } catch (JSONException ex) {
                    Log.e(TAG, "findRecordByData -> newDataArrived response: " + response);
                    FirebaseCrash.logcat(Log.ERROR, TAG, "findRecordByData -> newDataArrived");
                    FirebaseCrash.report(ex);
                    FirebaseCrash.log("response: " + response);
                } finally {
                    hideProgressDialog();
                }
            }

            @Override
            public void exceptionThrown(Throwable throwable) {
                Log.e(TAG, "findRecordByData -> exceptionThrown");
                FirebaseCrash.logcat(Log.ERROR, TAG, "findRecordByData -> newDataArrived");
                FirebaseCrash.report(throwable);
                hideProgressDialog();
                continueLoading();
            }
        });
    }

    private void showFindResults(Bundle args) {
        final FindResultsFragment fragment = new FindResultsFragment();
        if (null != args) {
            fragment.setArguments(args);
        }
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                showFragment(fragment, true);
            }
        });
    }

    // Fragment Navigation Interfaces Implementations

    @Override
    public void findRecordCancelled() {
        Log.d(TAG, "findRecordCancelled " + "Last Record" + " selected");
        showRecord("Last Record");
    }

    // ShowAllRecordsFragment.SelectRecordListener
    public void loadRecord(String recNumber, boolean editable) {
        Log.d(TAG, "loadRecord " + recNumber + " selected");
        // showRecord(recNumber); // TODO Check with Ronen.
        selectItem(1, null);  // TODO add the required args
    }

    // FindResultsFragment.FindResultsListener
    public void loadRecord(String recNumber, Bundle args) {
        Log.d(TAG, "loadRecord " + recNumber + " selected");
        // showRecord(recNumber); // TODO Check with Ronen.
        selectItem(1, args);
    }

    public void cancelCreation() {
        showRecord("Last Record");
    }   // TODO put the real record Id

    private void showNewRecordIdMessage(String recNumber) {
        Toast.makeText(context, getString(R.string.record_created_successfully_message, recNumber), Toast.LENGTH_LONG).show();
    }

    private void showNewRecordData(String recNumber) {
        showRecord(recNumber);
    }

    private void showRecord(String recNumber) {
        selectItem(1, null); // TODO add the required args
    }   // TODO add the required args

    // ServerConnectionDialog.OnSetServerConnectionListener
    @Override
    public void onSetServerConnection(String serverIp, int serverPort) {
        if (false == StringUtils.isNullOrEmpty(Dynamics.getServerIp())) {
            if ((false == Dynamics.getServerIp().equalsIgnoreCase(serverIp)) &&
                    (Dynamics.getServerPort() != serverPort)) {
                Dynamics.uUID = "";
            }
        } else {
            Dynamics.uUID = "";
        }
        PreferencesUtils.getInstance(context).setServerConncetionString(serverIp, serverPort);
        continueLoading();
    }

    // LoginDialog.OnSetLoginDataListener
    @Override
    public void onSetLoginDataListener(String usr, String pwd) {
        showProgressDialog();
        ServerUtilities.getInstance().login(usr, pwd, new CommListener() {
            @Override
            public void newDataArrived(String response) {
                try {
                    if (response.contains(Constants.error)) {
                        int errorNo = ErrorsUtils.getError(response);
                        //showToast(context.getString(R.string.login_failed));
                        showLoginFailedDialog();
                    } else {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has(Constants.uuid)) {
                            Dynamics.uUID = (String) jsonObject.get(Constants.uuid);
                        }
                    }
                } catch (JSONException ex) {
                    Log.e(TAG, "onSetLoginDataListener -> newDataArrived response: " + response);
                    FirebaseCrash.logcat(Log.ERROR, TAG, "onSetLoginDataListener -> newDataArrived");
                    FirebaseCrash.report(ex);
                    FirebaseCrash.log("response: " + response);
                } finally {
                    hideProgressDialog();
                    hideVail();
                    //continueLoading();
                }
            }

            @Override
            public void exceptionThrown(Throwable throwable) {
                Log.e(TAG, "onSetLoginDataListener -> exceptionThrown");
                showToast(context.getString(R.string.login_failed));
                FirebaseCrash.logcat(Log.ERROR, TAG, "onSetLoginDataListener -> newDataArrived");
                FirebaseCrash.report(throwable);
                hideProgressDialog();
                hideVail();
                continueLoading();
            }
        });
    }

    private void showToast(final String message) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoginFailedDialog() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showVail();
                FragmentManager fm = getFragmentManager();
                LoginFailedDialog dialog = new LoginFailedDialog();
                try {
                    dialog.show(fm, "LoginFailedDialog");
                } catch (Throwable e) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "showLoginFailedDialog");
                    FirebaseCrash.report(e);
                    hideVail();
                }
            }
        }, 100);
    }

    // LoginFailedDialog.OnLoginFailedListener
    @Override
    public void okButtonPressed() {
        hideVail();
        continueLoading();
    }

    // LoginDialog.OnSetLoginDataListener
    @Override
    public void settingButtonPressed() {
        showServerConnectionDialog();
    }

    private void hideProgressDialog() {
        if (null != progressDialog) {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void showProgressDialog() {
        if (null == progressDialog || false == progressDialog.isShowing()) {
            progressDialog = ProgressDialog.show(context, "", "Loading...", true);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (0 < fm.getBackStackEntryCount()) {
            fm.popBackStack();
            return;
        }
        Fragment fragment = fm.findFragmentById(R.id.container);
        Log.d(TAG, "onBackPressed fragment: " + fragment.toString());
        if (true == StringUtils.containsIgnureCase(fragment.toString(), "showrecordfragment")) {
            showAppExitDialog();
        } else {
            selectItem(1, null); // TODO add the required args
        }
    }

    private void showAppExitDialog() {
        FragmentManager fm = getFragmentManager();
        AppExitDialog dialog = new AppExitDialog();
        dialog.show(fm, "AppExitDialog");
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position, null);
        }
    }

    private class SettingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return settings.size();
        }

        @Override
        public Object getItem(int i) {
            return settings.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (null == view) {
                view = LayoutInflater.from(context).inflate(R.layout.setting_item, viewGroup, false);
            }

            // Lookup view for data population
            ImageView img = (ImageView) view.findViewById(R.id.img);
            TextView stng = (TextView) view.findViewById(R.id.stng);

            // Populate the data into the template view using the data object
            Setting setting = settings.get(i);
            img.setImageResource(setting.getIconId());
            stng.setText(setting.getName());
            view.setId(setting.getId());

            return view;
        }
    }

    private class DrawerSettingItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position, null);
        }
    }
}
