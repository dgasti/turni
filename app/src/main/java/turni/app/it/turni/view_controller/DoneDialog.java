package turni.app.it.turni.view_controller;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import turni.app.it.turni.R;

public class DoneDialog extends ActionBarActivity {

    private static final String LAUNCH_ACTIVITY = "LAUNCH_WORKINGACTIVITY";
    private static final String SURNAME = "SURNAME";
    private static final boolean DEBUG = true;
    private static final String TAG = "WORKINGDIALOG";
    private static String text, surname;
    private static final int CODE_OK = 1;
    /**
     * Activity resul code not Ok
     */
    private static final int CODE_NOT_OK = 0;
    public static boolean isFinishing = false;
    private ProgressDialog pDialog;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_dialog);
        //text = getIntent().getStringExtra(LAUNCH_ACTIVITY);
        //surname = getIntent().getStringExtra(SURNAME);

        /*if (DEBUG) {
            Log.d(TAG, "testo turni passato in DoneDialog: " + text);
            Log.d(TAG, "Cognome passato in DoneDialog: " + surname);
        }

        final Bundle bundle = new Bundle();
        bundle.putString(LAUNCH_ACTIVITY, text);
        bundle.putString(SURNAME, surname);*/

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
            Fragment newFragment = new PlaceholderFragment();
            //newFragment.setArguments(bundle);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.container, newFragment).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_working, menu);
        return true;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        private static final boolean DEBUG = true;
        private static final String TAG = "WORKING FRAGMENT";
        private static final String BACKGROUND = "background";
        private static final String SYNC_CALENDAR = "sync calendar";
        private static final String GET_CALENDAR = "get calendar";
        private RelativeLayout mBackground;
        private Button sync_calendar, get_calendar;
        private String mText;
        private String mSurname;
        private boolean isActivityCalled = false;
        private SharedPreferences wSharedPrefs;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_working_dialog, container, false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.enter_ma_da));
            }

            mBackground = (RelativeLayout) rootView.findViewById(R.id.working_dialog_fragment_background);
            sync_calendar = (Button) rootView.findViewById(R.id.sync_button);
            get_calendar = (Button) rootView.findViewById(R.id.calendar_button);

            mBackground.setTag(BACKGROUND);
            sync_calendar.setTag(SYNC_CALENDAR);
            get_calendar.setTag(GET_CALENDAR);

            mBackground.setOnClickListener(this);
            sync_calendar.setOnClickListener(this);
            get_calendar.setOnClickListener(this);

            return rootView;
        }


        @Override
        public void onClick(View view) {
            String tag = (String) view.getTag();
            if (BACKGROUND.equals(tag)) {
                getActivity().setResult(CODE_NOT_OK, getActivity().getIntent());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().finishAfterTransition();
                }
            }
            if (SYNC_CALENDAR.equals(tag)) {
                AccountManager manager = AccountManager.get(getActivity());
                Account[] accounts = manager.getAccountsByType("com.google");
                String accountName = "";
                String accountType = "";
                for (Account account : accounts) {
                    accountName = account.name;
                    accountType = account.type;
                    break;
                }
                Account a = new Account(accountName, accountType);
                ContentResolver.addPeriodicSync(a, "com.android.calendar", new Bundle(), 10);

                Log.d(TAG, "SYNC EFFETTUATA");

                Toast.makeText(getActivity().getApplicationContext(), "Sincronizzazione effettuata", Toast.LENGTH_SHORT).show();
            }
            if (GET_CALENDAR.equals(tag)) {
                if (Build.VERSION.SDK_INT >= 8) {
                    Intent i = new Intent();
                    ComponentName cn = new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
                    i.setComponent(cn);
                    try {
                        startActivity(i);
                    }catch (ActivityNotFoundException e) {
                        PackageManager packmngr = getActivity().getApplicationContext().getPackageManager();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        List<ResolveInfo> list = packmngr.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
                        ResolveInfo Resolvebest = null;
                        for (final ResolveInfo info : list) {
                            if (info.activityInfo.packageName.endsWith(".calendar"))
                                Resolvebest = info;
                        }
                        if (Resolvebest != null) {
                            intent.setClassName(Resolvebest.activityInfo.packageName, Resolvebest.activityInfo.name);
                            startActivity(intent);
                        }
                    }

                } else {
                    PackageManager packmngr = getActivity().getApplicationContext().getPackageManager();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    List<ResolveInfo> list = packmngr.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);
                    ResolveInfo Resolvebest = null;
                    for (final ResolveInfo info : list) {
                        if (info.activityInfo.packageName.endsWith(".calendar"))
                            Resolvebest = info;
                    }
                    if (Resolvebest != null) {
                        intent.setClassName(Resolvebest.activityInfo.packageName, Resolvebest.activityInfo.name);
                        startActivity(intent);
                    } else {
                        Intent i = new Intent();
                        ComponentName cn = new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
                        i.setComponent(cn);
                        startActivity(i);
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getActivity().finishAfterTransition();
                        }
                        else {
                            getActivity().finish();
                        }
                    }
                }, 250);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(CODE_NOT_OK, getIntent());
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
