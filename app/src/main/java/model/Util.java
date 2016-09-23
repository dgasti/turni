package model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import turni.app.it.turni.R;

/**
 * Created by nick on 26/03/2015.
 */
public class Util {
    private static final boolean DEBUG = true;
    private static final String TAG = "Util";


    /**
     * Return the calendar ID using the calendar name
     *
     * @param context application context
     * @param calName The name of the calendar
     * @param accName The name of the account that contains the calendar.
     * @return The ID of the desired calendar. If the given calendar name and account don't exist, the returned ID is negative.
     */
    public static int getCalendarID(Context context, String calName, String accName) {
        if (calName == null || accName == null)
            return -1;
        Cursor cur = null;
        int calID = -1;
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME};
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{accName, "com.google"};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, projection, selection, selectionArgs, null);

        if (cur.moveToFirst())
            while (!cur.isAfterLast()) {
                if (cur.getString(1).equals(calName)) {
                    calID = cur.getInt(0);
                    break;
                }
                cur.move(1);
            }
        cur.close();
        return calID;
    }

    public static Drawable getResourceColor(Context context, int color, boolean isCircle) {
        Drawable resource = null;
        switch (color) {
            case 1:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
            case 2:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.salvia);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
            case 3:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.vinaccia);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
            case 4:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.fenicottero);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
            case 5:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.banana);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
            case 6:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.mandarino);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
            case 7:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.pavone);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
            case 8:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.grafite);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
            case 9:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.mirtillo);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
            case 10:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.basilico);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
            case 11:
                if (isCircle)
                    resource = context.getResources().getDrawable(R.drawable.pomodoro);
                else
                    resource = context.getResources().getDrawable(R.drawable.lavanda);
                break;
        }
        return resource;
    }

    public static ArrayList<String> getCalendarAccounts(Context context) {
        Cursor cur = null;
        ArrayList<String> names = new ArrayList<String>();
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{CalendarContract.Calendars.ACCOUNT_NAME};
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{"com.google"};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, projection, selection, selectionArgs, null);
        int index = 0;
        if (cur.moveToFirst())
            while (!cur.isAfterLast()) {
                String text = cur.getString(0);
                if (index == 0) {
                    names.add(cur.getString(0));
                    index++;
                } else if (!names.get(index - 1).equalsIgnoreCase(text)) {
                    names.add(text);
                    index++;
                }
                cur.move(1);
            }
        cur.close();
        return names;
    }

    public static ArrayList<String> getCalendarNames(Context context, String accountName) {
        Cursor cur = null;
        ArrayList<String> names = new ArrayList<String>();
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{CalendarContract.Calendars.NAME};
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{accountName, "com.google"};
        // Submit the query and get a Cursor object back.
        cur = cr.query(uri, projection, selection, selectionArgs, null);

        if (cur.moveToFirst())
            while (!cur.isAfterLast()) {
                names.add(cur.getString(0));
                cur.move(1);
            }
        cur.close();
        return names;
    }

    /**
     * Get the date from the processed line
     *
     * @param line time from which get the date
     * @return Calendar with the right date
     */
    public static Calendar getEventDate(String line) {
        Calendar date = Calendar.getInstance();
        try {
            date.set(Integer.parseInt(line.substring(0, 4)), Integer.parseInt(line.substring(5, 7)) - 1,
                    Integer.parseInt(line.substring(8, 10)));
        } catch (Exception e) {
            Log.e(TAG, "ERROR WHILE CREATING EVENT DATE");
        }
        return date;
    }

    public static Uri getCalendarUriBase() {
        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            // the old way

            eventUri = Uri.parse("content://calendar/events");
        } else {
            // the new way

            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        return eventUri;
    }

    public static int isAlreadyCreate(long begin, long end, ContentResolver content) {

        String titleMatt = "TURNO MATTINO";
        String titleNott1 = "TURNO NOTTURNO 21";
        String titleNott2 = "TURNO NOTTURNO 00";
        String titlePom = "TURNO POMERIGGIO";
        String titleText_REP = "REPERIBILITA'";

        if (DEBUG)
            Log.d(TAG, "Sono dentro all'isAlreadyCreate");

        String[] proj =
                new String[]{
                        CalendarContract.Instances._ID,
                        CalendarContract.Instances.EVENT_ID,
                        CalendarContract.Instances.TITLE};
        Cursor cursor =
                CalendarContract.Instances.query(content, proj, begin, end);

        if (DEBUG) {
            Log.d(TAG, "proj[1] = " + proj[1].toString());
            Log.d(TAG, "proj[2] = " + proj[2].toString());
        }

        int eventID;
        int numEventiEliminati = 0;
        if (cursor.moveToFirst()) {

            String idColString;
            String titleCursor;

            if (DEBUG) {
                Log.d(TAG, "cursor.getCount = " + cursor.getCount());
                Log.d(TAG, "Nomi delle colonne = " + cursor.getColumnNames());
            }

            int idCol = cursor.getColumnIndex(proj[1]);
            int titleCol = cursor.getColumnIndex(proj[2]);

            if (DEBUG) {
                Log.d(TAG, "idColonna = " + idCol);
                Log.d(TAG, "titleColonna numero di colonna = " + titleCol);
            }

            do {
                idColString = cursor.getString(idCol);

                if (DEBUG)
                    Log.d(TAG, "id Colonna dell'eventID in Stringa  = " + idColString);

                titleCursor = cursor.getString(titleCol);

                if (DEBUG)
                    Log.d(TAG, "id Colonna del titolo in Stringa  = " + idColString);

                if ((titleCursor.equals(titleMatt)) || (titleCursor.equals(titleNott1)) || (titleCursor.equals(titleNott2)) || (titleCursor.equals(titlePom)) || (titleCursor.equals(titleText_REP))) {

                    if (DEBUG)
                        Log.d(TAG, "Sono dentro all'if dell'isAlreadyCreate");

                    eventID = Integer.parseInt(idColString);
                    Uri eventsUri = Uri.parse(getCalendarUriBase() + "");
                    Uri eventUri = ContentUris.withAppendedId(eventsUri, eventID);
                    numEventiEliminati = content.delete(eventUri, null, null);

                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return numEventiEliminati;
    }


}
