package turni.app.it.turni.view_controller;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

import turni.app.it.turni.R;

public class LoadingEvents extends ActionBarActivity implements TaskCallback {

    private static final String TURN_TEXT = "LAUNCH_WORKINGACTIVITY";
    private static final String SURNAME = "SURNAME";
    private static final String CHECKBOX = "CHECKBOX_RECOVERYDAY";
    private static final boolean DEBUG = true;
    private static final String TAG = "LOADINGEVENTS";
    private static String text, surname;
    private static boolean recoveryDay;
    private static final int CODE_OK = 1;
    /**
     * Activity resul code not Ok
     */
    private static final int CODE_NOT_OK = 0;
    public static boolean isFinishing = false;
    private ProgressDialog pDialog;
    private Activity activity;
    private static final String BACKGROUND = "background";
    private static final String SYNC_CALENDAR = "sync calendar";
    private static final String GET_CALENDAR = "get calendar";
    private RelativeLayout mBackground;
    private Button sync_calendar, get_calendar;
    private String mText;
    private String mSurname;
    private boolean isActivityCalled = false;
    private SharedPreferences wSharedPrefs;
    private boolean mRecoveryDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_events);

        text = getIntent().getStringExtra(TURN_TEXT);
        mText = text;
        surname = getIntent().getStringExtra(SURNAME);
        mSurname = surname;
        recoveryDay = getIntent().getBooleanExtra(CHECKBOX, false);
        mRecoveryDay = recoveryDay;

        if (DEBUG) {
            Log.d(TAG, "Cognome passato in LoadingEvents: " + surname);
        }

        final Bundle bundle = new Bundle();
        bundle.putString(TURN_TEXT, text);
        bundle.putString(SURNAME, surname);
        bundle.putBoolean(CHECKBOX, recoveryDay);

        wSharedPrefs = this.getSharedPreferences(getString(R.string.preference_file_key), this.MODE_PRIVATE);

        if (DEBUG) {
            Log.d(TAG, "Cognome all'interno del LoadingEvents: " + mSurname);
            Log.d(TAG, "Valore CheckBox all'interno del LoadingEvents: " + mRecoveryDay);
        }

        if (mText != null && mSurname != null)
            isActivityCalled = true;

        new LoadingEventsTask(this, mText, mSurname, isActivityCalled, this, mRecoveryDay).execute();

        if(DEBUG)
            Log.d(TAG, "isFinishing = "+isFinishing);

    }

    public void done() {
        finish();
    }

    @Override
    public void onBackPressed() {
    }

}
