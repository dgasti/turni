package turni.app.it.turni.view_controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import model.Util;
import turni.app.it.turni.R;

import static turni.app.it.turni.view_controller.DoneDialog.isFinishing;

/**
 * Created by rufy on 23/09/16.
 */

public class LoadingEventsTask extends AsyncTask<Void, Void, Void> {

    private Calendar calendar;
    private String text;
    private String surname;
    private Activity activity;
    Context context;
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
    private static final String TAG = "LOADINGEVENTSTASK";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static int events = 0;
    private static final String BACKGROUND = "background";
    private static final String SYNC_CALENDAR = "sync calendar";
    private static final String GET_CALENDAR = "get calendar";
    private RelativeLayout mBackground;
    private Button sync_calendar, get_calendar;
    private TextView sync_calendar_text, get_calendar_text, done_text;
    private int mCalID;
    private ContentResolver mContentResolver;
    private SharedPreferences wSharedPrefs;
    private boolean isActivityCalled = false;
    private boolean hasToCreateEvent;
    private String titleMatt, titlePom, titleNott1, titleNott2, titleText, titleText_REP, placeText = "";

    public LoadingEventsTask(Activity activity, String text, String surname, boolean isActivityCalled) {
        this.activity = activity;
        this.text = text;
        this.surname = surname;
        this.isActivityCalled = isActivityCalled;
    }

    protected void onPreExecute() {

        wSharedPrefs = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), activity.MODE_PRIVATE);

        if (DEBUG) {
            Log.d(TAG, "Calendario usato = " + wSharedPrefs.getString(SP_CALENDAR_USED, "nessun calendario").toString());
            Log.d(TAG, "Account usato = " + wSharedPrefs.getString(SP_ACCOUNT_USED, "nessun account").toString());
        }
    }

    protected Void doInBackground(Void... unused) {

        if (isActivityCalled) {
            createEvent();
            isActivityCalled = false;
            text = null;
            surname = null;
            activity.startPostponedEnterTransition();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }

    protected void onPostExecute(Void unused) {
        DoneDialog.isFinishing = true;
    }

    private final void createEvent() {
        //Put all the string to upper case to avoid errors reading the string
        text = text.toUpperCase();
        surname = surname.toUpperCase();
        Log.d(TAG, "surname = " + surname);
        Scanner sc = new Scanner(text);
        String line = "";
        //Get the calendar ID where the events should be put
        mCalID = Util.getCalendarID(activity, wSharedPrefs.getString(SP_CALENDAR_USED, null), wSharedPrefs.getString(SP_ACCOUNT_USED, null));
        long startMillis = 0;
        long endMillis = 0;
        long checkStartMillis = 0;
        long checkEndMillis = 0;
        boolean isFullDay;
        boolean hasReachable;
        boolean isVerona, isBassona;
        Calendar beginTime = null;
        Calendar endTime = null;
        Calendar checkEventBegin = null;
        Calendar checkEventEnd = null;
        hasToCreateEvent = false;
        hasReachable = false;
        isFullDay = false;
        isVerona = isBassona = false;

        if (DEBUG)
            Log.d(TAG, "testo dei turni: " + text);

        int fromIndex = 0;
        int i = 0;
        events = text.indexOf(surname, fromIndex);
        //un indice che permette di iterare (dopo vedi)
        //-1 è quel  valore che dice che non trova la substringa perche ha finito le iterazioni
        //fromIndex all'inizio è zero quindi cerca la substringa dall'inizio del testo

        while (text.indexOf(surname, fromIndex) != -1) {
            Log.d(TAG, "index: " + text.indexOf(surname, fromIndex));
            int surNameIndex = text.indexOf(surname, fromIndex);  //trovo la posizione del primo cognome
            int startLine = surNameIndex - 17;                     //Trovo la posizione dell'inizio della stringa che è 17 posti indietro l'inizio del cognome
            //Per trovare la fine della riga:
            //- trovo la posizione del cognome successivo. Per fare questo dico a indexOf di partire dalla fine del cognome precedente (surNameIndex+surName lenght)
            //- successivamente tolgo 18 posizioni e quindi mi si mette prima della seconda data
            //- faccio una verifica che il cognome che sto verificando ne abbia uno successivo o sia già l'ultimo
            Log.d(TAG, "startline = " + startLine);

            int endLine;
            if (text.indexOf(surname, surNameIndex + surname.length()) != -1) {
                endLine = text.indexOf(surname, surNameIndex + surname.length()) - 17;
                line = text.substring(startLine, endLine);
                fromIndex = endLine;
                Log.d(TAG, "valore stringa tirata fuori if " + line);
            } else {
                line = text.substring(startLine);
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

            if (DEBUG)
                Log.d(TAG, "turno = " + line);

            beginTime = Util.getEventDate(line);
            checkEventBegin = Util.getEventDate(line);
            endTime = null;
            checkEventEnd = null;

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
                checkEventBegin.set(Calendar.HOUR_OF_DAY, 0);
                checkEventBegin.set(Calendar.MINUTE, 1);
                checkEventEnd = (Calendar) checkEventBegin.clone();
                checkEventEnd.set(Calendar.HOUR_OF_DAY, 23);
                checkEventEnd.set(Calendar.MINUTE, 59);
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
                checkEventBegin.set(Calendar.HOUR_OF_DAY, 0);
                checkEventBegin.set(Calendar.MINUTE, 1);
                checkEventEnd = (Calendar) checkEventBegin.clone();
                checkEventEnd.set(Calendar.HOUR_OF_DAY, 23);
                checkEventEnd.set(Calendar.MINUTE, 59);
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
                checkEventBegin.set(Calendar.HOUR_OF_DAY, 0);
                checkEventBegin.set(Calendar.MINUTE, 1);
                checkEventEnd = (Calendar) checkEventBegin.clone();
                checkEventEnd.set(Calendar.HOUR_OF_DAY, 23);
                checkEventEnd.set(Calendar.MINUTE, 59);
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
                checkEventBegin.set(Calendar.HOUR_OF_DAY, 6);
                checkEventBegin.set(Calendar.MINUTE, 0);
                checkEventEnd = (Calendar) checkEventBegin.clone();
                checkEventEnd.add(Calendar.DAY_OF_YEAR, 1);
                checkEventEnd.set(Calendar.HOUR_OF_DAY, 6);
                checkEventEnd.set(Calendar.MINUTE, 1);
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
                checkEventBegin.set(Calendar.HOUR_OF_DAY, 0);
                checkEventBegin.set(Calendar.MINUTE, 1);
                checkEventEnd = (Calendar) checkEventBegin.clone();
                checkEventEnd.set(Calendar.HOUR_OF_DAY, 23);
                checkEventEnd.set(Calendar.MINUTE, 59);
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
                checkEventBegin.set(Calendar.HOUR_OF_DAY, 0);
                checkEventBegin.set(Calendar.MINUTE, 1);
                checkEventEnd = (Calendar) checkEventBegin.clone();
                checkEventEnd.set(Calendar.HOUR_OF_DAY, 23);
                checkEventEnd.set(Calendar.MINUTE, 59);
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
                checkEventBegin.set(Calendar.HOUR_OF_DAY, 0);
                checkEventBegin.set(Calendar.MINUTE, 1);
                checkEventEnd = (Calendar) checkEventBegin.clone();
                checkEventEnd.set(Calendar.HOUR_OF_DAY, 23);
                checkEventEnd.set(Calendar.MINUTE, 59);
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
                checkEventBegin.set(Calendar.HOUR_OF_DAY, 0);
                checkEventBegin.set(Calendar.MINUTE, 1);
                checkEventEnd = (Calendar) checkEventBegin.clone();
                checkEventEnd.set(Calendar.HOUR_OF_DAY, 23);
                checkEventEnd.set(Calendar.MINUTE, 59);
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

                if (DEBUG) {
                    Log.d(TAG, "Sono dentro all'if che crea gli eventi");
                    Log.d(TAG, "isVerona = " + isVerona);

                    Log.d(TAG, "isBassona = " + isBassona);
                }

                startMillis = beginTime.getTimeInMillis();
                endMillis = endTime.getTimeInMillis();
                checkStartMillis = checkEventBegin.getTimeInMillis();
                checkEndMillis = checkEventEnd.getTimeInMillis();

                if (DEBUG) {
                    Log.d(TAG, "startMillis = " + startMillis);
                    Log.d(TAG, "endMillis = " + endMillis);
                }

                mContentResolver = activity.getContentResolver();
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

                    if (DEBUG)
                        Log.d(TAG, "Sono entrato nell'if di verona");

                    values.put(CalendarContract.Events.EVENT_COLOR_KEY, wSharedPrefs.getInt(VERONA_COLOR_DEFAULT, 1));
                    values.put(CalendarContract.Events.EVENT_LOCATION, "Via Monte Bianco, 18\n" +
                            "37132 Verona VR");
                    isVerona = false;
                }
                if (isBassona) {

                    if (DEBUG)
                        Log.d(TAG, "Sono entrato nell'if di bassona");

                    values.put(CalendarContract.Events.EVENT_COLOR_KEY, wSharedPrefs.getInt(BASSONA_COLOR_DEFAULT, 1));
                    values.put(CalendarContract.Events.EVENT_LOCATION, "Via della Meccanica, 1\n" +
                            "37139 Verona VR");
                    isBassona = false;
                }
                values.put(CalendarContract.Events.CALENDAR_ID, mCalID);
                TimeZone tz = TimeZone.getDefault();
                values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
                String eventTitle = values.get(CalendarContract.Events.TITLE).toString();
                if ((eventTitle == titleMatt) || (eventTitle == titleNott1) || (eventTitle == titleNott2) || (eventTitle == titlePom) || (eventTitle == titleText_REP)) {
                    int iNumRowsDeleted = Util.isAlreadyCreate(checkStartMillis, checkEndMillis, mContentResolver);

                    if (DEBUG) {
                        Log.d(TAG, "numero di eventi deletati = " + iNumRowsDeleted);
                    }


                }
                //isAlreadyCreate(checkStartMillis, checkEndMillis, mContentResolver, mCalID, eventTitle);

                if (DEBUG)
                    Log.d(TAG, "sono dopo l'alreadyCreate");

                Uri uri1 = mContentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
                // get the event ID that is the last element in the
                //  Uri  long eventID = Long.parseLong(uri1.getLastPathSegment());
            }
            i++;
        }

        if (DEBUG)
            Log.d(TAG, "Eventi creati = " + i);

        if (i == 0) {
            Toast.makeText(activity.getApplicationContext(), "NESSUN EVENTO CREATO", Toast.LENGTH_LONG).show();
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle("Attenzione");
            alertDialog.setMessage("Nessun evento creato! Controlla il cognome inserito!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            activity.onBackPressed();
                        }
                    });
            alertDialog.show();
        }

    }

}

