package turni.app.it.turni.view_controller;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import model.Util;
import turni.app.it.turni.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p/>
 * Use the {@link WorkingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkingFragment extends Fragment implements View.OnClickListener {
    // Debug variables
    private static final boolean DEBUG = true;
    /**
     * Indicates that the day is a RECUPERO
     */
    private final static String RECUPERO = "RECRECUPERO";
    /**
     * Indicates that the day you are Reachable
     */
    private final static String REPERIBILE = "REP";
    //Constants representing the work place
    private final static String VERONA = "VR1";
    private final static String BASSONA = "VR2";
    //Constants representing the shifts time
    private final static String MATTINA = "07.00-14.12";
    private final static String POMERIGGIO = "14.10-21.22";
    private final static String NOTTURNO_00 = "00.01-07.13";
    private final static String NOTTURNO_21 = "21.15-04.27";
    private final static String NOTTURNO_FESTIVO = "00.01-08.00";
    private final static String MATTINA_FESTIVO = "08.00-16.00";
    private final static String POMERIGGIO_FESTIVO = "16.00-24.00";

    private static final String SP_CALENDAR_USED = "calendar used";
    private static final String SP_ACCOUNT_USED = "account used";
    private static final String BASSONA_COLOR_DEFAULT = "bassona color default";
    private static final String VERONA_COLOR_DEFAULT = "result color selected";
    /**
     * Constant used in onStartCommand() to start the creation of the events
     */
    private static final String CREATE_EVENTS = "create events";
    private static final CharSequence ASSENZA = "ASSENZA";
    private static final CharSequence ASSENZE = "ASSENZE";
    private static final CharSequence FESTIVO_TARGET = "TARGET";
    private static boolean isCreated;
    private SharedPreferences mSharedPref;
    private static final String TAG = "WORKING FRAGMENT";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static int events = 0;


    private String mText;
    private View mView;
    private boolean isActivityCalled = false;
    private String mSurname;
    private int mCalID;
    private ContentResolver mContentResolver;


    public WorkingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment WorkingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkingFragment newInstance(String param1, String param2) {
        WorkingFragment fragment = new WorkingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get the text to elaborate from the activity
        if (getArguments() != null) {
            mText = getArguments().getString(ARG_PARAM1);
            mSurname = getArguments().getString(ARG_PARAM2);
            if (mText != null && mSurname != null)
                isActivityCalled = true;
        }
        //TODO: implement a exception
        mSharedPref = getActivity().getSharedPreferences(
                getActivity().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        //getActivity().getWindow().setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.enter_ma_dwa));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_working, container, false);

        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isActivityCalled) {
                    createEvent();
                    isActivityCalled = false;
                    mText = null;
                    mSurname = null;
                    getActivity().startPostponedEnterTransition();
                }
            }
        });

        Button sync_cal = (Button) mView.findViewById(R.id.sync);
        sync_cal.setOnClickListener(this);
        Button calendario = (Button) mView.findViewById(R.id.calendar);
        calendario.setOnClickListener(this);

        return mView;
    }

    private final void createEvent() {
        //Put all the string to upper case to avoid errors reading the string
        mText = mText.toUpperCase();
        mSurname = mSurname.toUpperCase();
        Log.d(TAG, "surname = " + mSurname);
        Scanner sc = new Scanner(mText);
        String line = "";
        //Get the calendar ID where the events should be put
        mCalID = Util.getCalendarID(getActivity(), mSharedPref.getString(SP_CALENDAR_USED, null), mSharedPref.getString(SP_ACCOUNT_USED, null));
        long startMillis = 0;
        long endMillis = 0;
        boolean isFullDay;
        boolean hasToCreateEvent;
        boolean hasReachable;
        boolean isVerona, isBassona;
        String titleMatt, titlePom, titleNott1, titleNott2, titleText, titleText_REP, placeText = "";
        Calendar beginTime = null;
        Calendar endTime = null;
        Calendar allday = null;
        hasToCreateEvent = false;
        hasReachable = false;
        isFullDay = false;
        isVerona = isBassona = false;
        Log.d(TAG, "testo dei turni: " + mText);
        int fromIndex = 0;
        int i = 1;
        events = mText.indexOf(mSurname, fromIndex);
        //un indice che permette di iterare (dopo vedi)
        //-1 è quel  valore che dice che non trova la substringa perche ha finito le iterazioni
        //fromIndex all'inizio è zero quindi cerca la substringa dall'inizio del testo

        while (mText.indexOf(mSurname, fromIndex) != -1) {
            Log.d(TAG, "index: " + mText.indexOf(mSurname, fromIndex));
            int surNameIndex = mText.indexOf(mSurname, fromIndex);  //trovo la posizione del primo cognome
            int startLine = surNameIndex - 17;                     //Trovo la posizione dell'inizio della stringa che è 17 posti indietro l'inizio del cognome
            //Per trovare la fine della riga:
            //- trovo la posizione del cognome successivo. Per fare questo dico a indexOf di partire dalla fine del cognome precedente (surNameIndex+surName lenght)
            //- successivamente tolgo 18 posizioni e quindi mi si mette prima della seconda data
            //- faccio una verifica che il cognome che sto verificando ne abbia uno successivo o sia già l'ultimo
            Log.d(TAG, "startline = " + startLine);

            int endLine;
            if (mText.indexOf(mSurname, surNameIndex + mSurname.length()) != -1) {
                endLine = mText.indexOf(mSurname, surNameIndex + mSurname.length()) - 17;
                line = mText.substring(startLine, endLine);
                fromIndex = endLine;
                Log.d(TAG, "valore stringa tirata fuori if " + line);
            } else {
                line = mText.substring(startLine);
                Log.d(TAG, "valore stringa tirata fuori else " + line);
                fromIndex = fromIndex + 100;
            }
            //Log.d(TAG, "valore stringa tirata fuori  " + line);

            //TODO add holiday in calendar event
            titleText = "TURNO LAVORATIVO";
            titleMatt = "TURNO MATTINO";
            titleNott1 = "TURNO NOTTURNO 21";
            titleNott2 = "TURNO NOTTURNO 00";
            titlePom = "TURNO POMERIGGIO";
            titleText_REP = "REPERIBILITA'";
            //Get the date for this event (year,month,day)
            beginTime = Util.getEventDate(line);
            endTime = null;
            //Find the work place

            if (line.contains(RECUPERO) || line.contains(ASSENZA) || line.contains(ASSENZE) || line.contains(FESTIVO_TARGET)) {
                hasToCreateEvent = false;
                Log.d(TAG, "entro in assenza riga " + i);
            }
            //Set the shift hours for each case
            if (line.contains(MATTINA)) {
                beginTime.set(Calendar.HOUR_OF_DAY, 7);
                beginTime.set(Calendar.MINUTE, 0);
                endTime = (Calendar) beginTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, 14);
                endTime.set(Calendar.MINUTE, 12);
                hasToCreateEvent = true;
                isFullDay = false;
                titleText = titleMatt;
            }
            //                    if (DEBUG)
//                        Log.d(TAG, "get timezone  after timemillis"+beginTime.getTimeZone());
//                    if (DEBUG)
//                        Log.d(TAG, "Start hour is " + beginTime);
//                    if (DEBUG)
//                        Log.d(TAG, "finish hour is  " + endTime);
            if (line.contains(POMERIGGIO)) {
                beginTime.set(Calendar.HOUR_OF_DAY, 14);
                beginTime.set(Calendar.MINUTE, 10);
                endTime = (Calendar) beginTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, 21);
                endTime.set(Calendar.MINUTE, 22);
                hasToCreateEvent = true;
                isFullDay = false;
                titleText = titlePom;
            }
            if (line.contains(NOTTURNO_00)) {
                beginTime.set(Calendar.HOUR_OF_DAY, 0);
                beginTime.set(Calendar.MINUTE, 1);
                endTime = (Calendar) beginTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, 7);
                endTime.set(Calendar.MINUTE, 13);
                hasToCreateEvent = true;
                isFullDay = false;
                titleText = titleNott2;
            }
            if (line.contains(NOTTURNO_21)) {
                beginTime.set(Calendar.HOUR_OF_DAY, 21);
                beginTime.set(Calendar.MINUTE, 15);
                endTime = (Calendar) beginTime.clone();
                //This turn ends the next day
                endTime.add(Calendar.DAY_OF_YEAR, 1);
                endTime.set(Calendar.HOUR_OF_DAY, 4);
                endTime.set(Calendar.MINUTE, 27);
                hasToCreateEvent = true;
                isFullDay = false;
                titleText = titleNott1;
            }
            if (line.contains(NOTTURNO_FESTIVO)) {
                beginTime.set(Calendar.HOUR_OF_DAY, 0);
                beginTime.set(Calendar.MINUTE, 1);
                endTime = (Calendar) beginTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, 8);
                endTime.set(Calendar.MINUTE, 0);
                hasToCreateEvent = true;
                isFullDay = false;
                titleText = titleNott2;
            }
            if (line.contains(MATTINA_FESTIVO)) {
                beginTime.set(Calendar.HOUR_OF_DAY, 8);
                beginTime.set(Calendar.MINUTE, 0);
                endTime = (Calendar) beginTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, 16);
                endTime.set(Calendar.MINUTE, 0);
                hasToCreateEvent = true;
                isFullDay = false;
                titleText = titleMatt;
            }
            if (line.contains(POMERIGGIO_FESTIVO)) {
                beginTime.set(Calendar.HOUR_OF_DAY, 16);
                beginTime.set(Calendar.MINUTE, 0);
                endTime = (Calendar) beginTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, 23);
                endTime.set(Calendar.MINUTE, 59);
                hasToCreateEvent = true;
                isFullDay = false;
                titleText = titlePom;
            }
            //TODO controllare evento creato all day
            if (line.contains(REPERIBILE)) {
                beginTime.set(Calendar.HOUR_OF_DAY, 8);
                beginTime.set(Calendar.MINUTE, 0);
                endTime = (Calendar) beginTime.clone();
                endTime.set(Calendar.HOUR_OF_DAY, 23);
                endTime.set(Calendar.MINUTE, 0);
                isFullDay = true;
                hasToCreateEvent = true;
                hasReachable = true;
            }

            if (line.contains(VERONA)) {

                if (DEBUG)
                    Log.d(TAG, "Nella stringa c'è VR1 - Verona");

                isVerona = true;
                placeText = "a San Michele";
            }
            if (line.contains(BASSONA)) {

                if (DEBUG)
                    Log.d(TAG, "Nella stringa c'è VR2 - Bassona");

                isBassona = true;
                placeText = "in Bassona";
            }


            //If useful information has been found in the line create the event
            if (hasToCreateEvent) {

                if (DEBUG)
                    Log.d(TAG, "create event " + beginTime);

                if (DEBUG) {
                    Log.d(TAG, "isVerona = " + isVerona);
                    Log.d(TAG, "isBassona = "+isBassona);
                }

                startMillis = beginTime.getTimeInMillis();
                endMillis = endTime.getTimeInMillis();
                mContentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();
                if (isFullDay) {
                    values.put(CalendarContract.Events.ALL_DAY, true);
                    values.put(CalendarContract.Events.DTSTART, startMillis);
                    values.put(CalendarContract.Events.DTEND, endMillis);
                    if (DEBUG)
                        Log.d(TAG, "Full day SET");
                    if (hasReachable) {
                        values.put(CalendarContract.Events.TITLE, titleText_REP);
                    } else {
                        values.put(CalendarContract.Events.TITLE, titleText);
                    }
                } else {
                    values.put(CalendarContract.Events.TITLE, titleText);
                    values.put(CalendarContract.Events.DTSTART, startMillis);
                    values.put(CalendarContract.Events.DTEND, endMillis);
                }

                //TODO opzione modifica descrizione
                values.put(CalendarContract.Events.DESCRIPTION, "Group workout");

                if (isVerona) {

                    if(DEBUG)
                        Log.d(TAG, "Sono entrato nell'if di verona");

                    values.put(CalendarContract.Events.EVENT_COLOR_KEY, mSharedPref.getInt(VERONA_COLOR_DEFAULT, 1));
                    values.put(CalendarContract.Events.EVENT_LOCATION, "Via Monte Bianco, 18\n" +
                            "37132 Verona VR");
                }
                if (isBassona) {

                    if(DEBUG)
                        Log.d(TAG, "Sono entrato nell'if di bassona");

                    values.put(CalendarContract.Events.EVENT_COLOR_KEY, mSharedPref.getInt(BASSONA_COLOR_DEFAULT, 1));
                    values.put(CalendarContract.Events.EVENT_LOCATION, "Via della Meccanica, 1\n" +
                            "37139 Verona VR");
                }
                values.put(CalendarContract.Events.CALENDAR_ID, mCalID);
                TimeZone tz = TimeZone.getDefault();
                values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
                Uri uri1 = mContentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
                // get the event ID that is the last element in the
                //  Uri  long eventID = Long.parseLong(uri1.getLastPathSegment());
            }
            i++;
        }
        //if(events == -1) {
        //    Toast.makeText(getActivity().getApplicationContext(), "NESSUN EVENTO CREATO", Toast.LENGTH_LONG).show();
        //}
        //else {
        //    Toast.makeText(getActivity().getApplicationContext(), "EVENTI CREATI", Toast.LENGTH_LONG).show();
        // }
    }

    @Override
    public void onClick(View mView) {
        switch (mView.getId()) {
            case R.id.sync:
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
                break;

            case R.id.calendar:
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.calendar");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
                break;
        }

    }

        /*
        Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.calendar");

        if (launchIntent != null)

        {
                startActivity(launchIntent);//null pointer check in case package name was not found
        } */
}