package turni.app.it.turni.view_controller;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import model.Util;
import turni.app.it.turni.R;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment implements View.OnClickListener {


    private static final boolean DEBUG = true;
    private static final String TAG = "MAINFRAGMENT";
    private static final String TURN_TEXT = "LAUNCH_WORKINGACTIVITY";
    private static final String TAG_FORWARD_BUTTON = "forward button";
    private static final String TAG_ACCOUNT_BUTTON = "calendar button";
    private static final int CALENDAR_DIALOG_ACTIVITY_RESULT_CODE = 1;
    private static final int COLOR_DIALOG_ACTIVITY_RESULT_CODE = 2;
    private static final String RESULT_COLOR_SELECTED = "result color selected";
    private static final String CALENDAR_ROW = "calendar row";
    private static final String SURNAME_TEXT = "SURNAME";
    private static final int FILE_SELECT_RESULT_CODE = 3;
    private static final int FORWARD_SELECT_BUTTON = 4;

    /**
     * Dialog Account is used?
     */
    private static boolean ACCOUNT_IS_USED = false;
    /**
     * Activity result intent Key
     */
    private static final String RESULT_CALENDAR = "result calendar";
    /**
     * Activity result code Ok
     */
    private static final int CODE_OK = 1;
    /**
     * Activity resul code not Ok
     */
    private static final int CODE_NOT_OK = 0;
    /**
     * Activity result intent key
     */
    private static final String RESULT_ACCOUNT = "result account";
    private static final String SP_CALENDAR_USED = "calendar used";
    private static final String SP_ACCOUNT_USED = "account used";
    private static final String TAG_VERONA_COLOR_BUTTON = "tag color button";
    private static final String TAG_BASSONA_COLOR_BUTTON = "tag bassona color button";
    private static final String COLOR_SELECTOR_BUNDLE = "color selector bundle";
    private static final String BASSONA_COLOR_DEFAULT = "bassona color default";
    private static final String VERONA_COLOR_DEFAULT = "result color selected";
    private static final String TAG_IMPORT_TEXT_BUTTON = "import text button";
    private static final String TAG_PASTE_TEXT = "paste text";
    private static final String TAG_DELETE_TEXT = "delete text";

    private View mView;
    private Context context;
    private FloatingActionButton mFowardButton;
    private EditText mEditText;
    private String mText;
    public static TextView mTextView, mSurnameText;
    private Button mAccountButton, mPasteButton, mDeleteButton;
    public static SharedPreferences mSharedPref;
    private Button mVeronaColorButton;
    private Button mBassonaColorButton;
    private boolean openVerona = false;
    private Button mImportTextButton;
    private String surname = "";
    private String surname_check = "";
    private boolean isSurnameDialogShow = false;
    private String path;
    private InputMethodManager imm;
    private ClipData.Item item;
    private Bundle bundle;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, null, false);
        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.my_awesome_toolbar);
        ((ActionBarActivity) getActivity()).setSupportActionBar(toolbar);
        ((ActionBarActivity) getActivity()).setTitle("VTS - Turni");
        mFowardButton = (FloatingActionButton) mView.findViewById(R.id.foward_button);
        mEditText = (EditText) mView.findViewById(R.id.edit_text);
        mAccountButton = (Button) mView.findViewById(R.id.account_button);
        mVeronaColorButton = (Button) mView.findViewById(R.id.verona_color_button);
        mBassonaColorButton = (Button) mView.findViewById(R.id.bassona_color_button);
        mImportTextButton = (Button) mView.findViewById(R.id.import_text_button);
        mSurnameText = (TextView) mView.findViewById(R.id.surname);
        mPasteButton = (Button) mView.findViewById(R.id.paste_text);
        mDeleteButton = (Button) mView.findViewById(R.id.delete_button);

        mFowardButton.setTag(TAG_FORWARD_BUTTON);
        mAccountButton.setTag(TAG_ACCOUNT_BUTTON);
        mVeronaColorButton.setTag(TAG_VERONA_COLOR_BUTTON);
        mBassonaColorButton.setTag(TAG_BASSONA_COLOR_BUTTON);
        mImportTextButton.setTag(TAG_IMPORT_TEXT_BUTTON);
        mPasteButton.setTag(TAG_PASTE_TEXT);
        mDeleteButton.setTag(TAG_DELETE_TEXT);

        int drawableColor = ColorSelectorDialog.getColorDrawable(mSharedPref.getInt(VERONA_COLOR_DEFAULT, 1));
        Drawable d = getResources().getDrawable(drawableColor);
        mVeronaColorButton.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
        drawableColor = ColorSelectorDialog.getColorDrawable(mSharedPref.getInt(BASSONA_COLOR_DEFAULT, 1));
        d = getResources().getDrawable(drawableColor);
        mBassonaColorButton.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);

        surname_check = mSharedPref.getString("SURNAME", "");

        if (DEBUG) {
            Log.d(TAG, "surname_check = " + surname_check);
        }

        if (surname_check.isEmpty()) {
            isSurnameDialogShow = showSurnameDialog(getActivity());
            surname_check = mSharedPref.getString("SURNAME", "");
        } else {
            surname_check = surname_check.trim();
            mSurnameText.setText(surname_check);
        }

        if (isSurnameDialogShow) {
            mSurnameText.setText("Chi sei?");
        }

        mFowardButton.setOnClickListener(this);
        mAccountButton.setOnClickListener(this);
        mVeronaColorButton.setOnClickListener(this);
        mBassonaColorButton.setOnClickListener(this);
        mImportTextButton.setOnClickListener(this);
        mPasteButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);

        View.OnLongClickListener listener = new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                Button clickedButton = (Button) v;
                String buttonText = clickedButton.getTag().toString();
                if (buttonText.equals(TAG_IMPORT_TEXT_BUTTON)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Importa i turni", Toast.LENGTH_SHORT).show();
                }
                if (buttonText.equals(TAG_PASTE_TEXT)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Incolla", Toast.LENGTH_SHORT).show();
                }
                if (buttonText.equals(TAG_DELETE_TEXT)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Cancella tutto", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        };

        mImportTextButton.setOnLongClickListener(listener);
        mPasteButton.setOnLongClickListener(listener);
        mDeleteButton.setOnLongClickListener(listener);

        String calendarName, accountName = null;
        calendarName = mSharedPref.getString(SP_CALENDAR_USED, null);
        accountName = mSharedPref.getString(SP_ACCOUNT_USED, null);
        //If there is a saved calendar and this calendar still exists, set the calendar name into the button.
        if (calendarName != null && accountName != null && Util.getCalendarID(getActivity(), calendarName, accountName) >= 0)
            mAccountButton.setText(calendarName + "  (" + accountName + ")");

        //String text = "2016-08-26EE39318Gastaldo S.LD1-VR107.00-14.12 \n" +
        //      "2016-08-27EE39318Gastaldo S.WEDAssenza WeekEnd \n" +
        //    "2016-08-28EE39318Gastaldo S.FN2-VR116.00-24.00";
        //         "2015-04-07 XL90355Bonuzzi N.RECRecupero";
        //String text="";
        //mEditText.setText(text);

        return mView;
    }

    public boolean showSurnameDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        if (surname_check.isEmpty()) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }

        dialog.show();

        final EditText surnameText = (EditText) dialog.findViewById(R.id.dialog_surname);
        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok);

        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                surname = surnameText.getText().toString();

                if (DEBUG)
                    Log.d(TAG, "surname = " + surname);

                if (surname.isEmpty()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Attenzione");
                    alertDialog.setMessage("Non hai inserito il cognome!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

                if (DEBUG)
                    Log.d(TAG, "surname dopo if di controllo = " + surname);

                surname = surname.trim();
                mSharedPref.edit().putString("SURNAME", surname).commit();
                hideSoftKeyboard(getActivity());

                if (surname.isEmpty() == false) {
                    mSurnameText.setText(surname);
                    hideSoftKeyboard(getActivity());
                    dialog.dismiss();
                }
                if (DEBUG)
                    Log.d(TAG, "Surname dopo if del dismiss = " + surname);
            }
        });
        return true;
    }

    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(Activity activity) {

        if (DEBUG) {
            Log.d(TAG, "Sono dentro al metodo della tastiera");
            Log.d(TAG, "getActivity().getCurrentFocus() = " + activity.getCurrentFocus());
        }

        InputMethodManager inputManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();

        if (DEBUG)
            Log.d(TAG, "tag selezionato = " + tag);

        boolean account_is_used = mSharedPref.getBoolean("ACCOUNT_IS_USED", false);
        if (TAG_IMPORT_TEXT_BUTTON.equals(tag)) {
            //Toast.makeText(getActivity().getApplicationContext(), "Funzione ancora non attiva!", Toast.LENGTH_SHORT).show();
            imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

            showFileChooser();

            if (DEBUG)
                Log.d(TAG, "Risultato dell'intent import button = " + FILE_SELECT_RESULT_CODE);
        }
        if (TAG_PASTE_TEXT.equals(tag)) {

            imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

            if (DEBUG)
                Log.d(TAG, "Ho cliccato il tasto dell'incolla");

            String pasteData = "";
            try {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {

                    if (DEBUG)
                        Log.d(TAG, "sono dentro a if clipboard != null");

                    item = clipboard.getPrimaryClip().getItemAt(0);
                    pasteData = item.getText().toString();
                }
                mEditText.setText(pasteData);
            } catch (NullPointerException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Nulla da incollare", Toast.LENGTH_SHORT).show();

            }
            //Toast.makeText(getActivity().getApplicationContext(), "Incollato", Toast.LENGTH_SHORT).show();
        }
        if (TAG_DELETE_TEXT.equals(tag)) {

            imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

            if (DEBUG)
                Log.d(TAG, "Ho cliccato il tasto del cancella");

            mEditText.setText(null);
        }
        String text = mEditText.getText().toString();
        if (TAG_FORWARD_BUTTON.equals(tag)) {

            imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

            if (DEBUG)
                Log.d(TAG, "Sono dentro all'if del Forward button nell'onclick");

            if (account_is_used == false) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Attenzione");
                alertDialog.setMessage("Seleziona un calendario prima di continuare!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else if (text.isEmpty()) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Attenzione");
                alertDialog.setMessage("Inserisci i turni prima di continuare!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            //else if (surname_check.isEmpty()) {
            //    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            //   alertDialog.setTitle("Attenzione");
            //  alertDialog.setMessage("Impossibile continuare, nessun cognome inserito!");
            //  alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
            //          new DialogInterface.OnClickListener() {
            //              public void onClick(DialogInterface dialog, int which) {
            //                  dialog.dismiss();
            //              }
            //          });
            //  alertDialog.show();
            //    }
            else {

                if (DEBUG)
                    Log.d(TAG, "Sono dentro all'else dell'intent del Forward button nell'onclick, ho superato tutti i test");

                Intent intent = new Intent(getActivity(), WorkingDialog.class);
                v.setTransitionName("snapshot");
                getActivity().getWindow().setExitTransition(null);
                getActivity().getWindow().setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.enter_ma_da));
                //         getActivity().getWindow().setSharedElementEnterTransition(TransitionInflater.from(getActivity())
                //               .inflateTransition(R.transition.circular_reveal_shared_transition));

                //Intent intent = new Intent(getActivity(), WorkingDialog.class);
                intent.putExtra(TURN_TEXT, text);
                //bundle = new Bundle();
                //bundle .putString(TURN_TEXT, text);

                Log.d(TAG, "text passato alla workingDialog = " + text);

                if (!(surname_check.isEmpty())) {
                    intent.putExtra(SURNAME_TEXT, surname_check);
                    //bundle.putString(SURNAME_TEXT, surname_check);
                    if (DEBUG)
                        Log.d(TAG, "Cognome passato alla workingDialog surname_check= " + surname_check);
                } else {
                    intent.putExtra(SURNAME_TEXT, surname);
                    //bundle.putString(SURNAME_TEXT, surname);
                    if (DEBUG)
                        Log.d(TAG, "Cognome passato alla workingDialog surname = " + surname);
                }

                startActivityForResult(intent, FORWARD_SELECT_BUTTON, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                mFowardButton.animate().alpha(0).setDuration(250);
                //getActivity().getWindow().setExitTransition(null);
                //getActivity().getWindow().setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.enter_ma_dwa));
                //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                //Toast.makeText(getActivity().getApplicationContext(), "Caricamento dei turni effettuata", Toast.LENGTH_SHORT).show();
            }
        }

        if (TAG_ACCOUNT_BUTTON.equals(tag))

        {
            imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

            Intent intent = new Intent(getActivity(), CalendarDialog.class);
            v.setTransitionName("snapshot");
            getActivity().getWindow().setExitTransition(null);
            getActivity().getWindow().setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.enter_ma_da));
            //         getActivity().getWindow().setSharedElementEnterTransition(TransitionInflater.from(getActivity())
            //               .inflateTransition(R.transition.circular_reveal_shared_transition));
            startActivityForResult(intent, CALENDAR_DIALOG_ACTIVITY_RESULT_CODE,
                    ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            mAccountButton.animate().alpha(0).setDuration(250);
        }

        if (TAG_VERONA_COLOR_BUTTON.equals(tag) || TAG_BASSONA_COLOR_BUTTON.equals(tag))

        {
            imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

            Intent intent = new Intent(getActivity(), ColorSelectorDialog.class);
            if (TAG_VERONA_COLOR_BUTTON.equals(tag)) {
                intent.putExtra(COLOR_SELECTOR_BUNDLE, TAG_VERONA_COLOR_BUTTON);
                openVerona = true;
            }
            if (TAG_BASSONA_COLOR_BUTTON.equals(tag)) {
                intent.putExtra(COLOR_SELECTOR_BUNDLE, TAG_BASSONA_COLOR_BUTTON);
                openVerona = false;
            }
            v.setTransitionName("snapshot");
            getActivity().getWindow().setExitTransition(null);
            getActivity().getWindow().setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.enter_ma_da));
            //         getActivity().getWindow().setSharedElementEnterTransition(TransitionInflater.from(getActivity())
            //               .inflateTransition(R.transition.circular_reveal_shared_transition));
            startActivityForResult(intent, COLOR_DIALOG_ACTIVITY_RESULT_CODE,
                    ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            if (TAG_VERONA_COLOR_BUTTON.equals(tag))
                mVeronaColorButton.animate().alpha(0).setDuration(250);
            if (TAG_BASSONA_COLOR_BUTTON.equals(tag))
                mBassonaColorButton.animate().alpha(0).setDuration(250);
        }

    }

    public boolean onLongClick(View v) {
        String tag = (String) v.getTag();

        if (DEBUG)
            Log.d(TAG, "Sono dentro al Long Click");

        if (TAG_IMPORT_TEXT_BUTTON.equals(tag)) {
            Toast.makeText(getActivity().getApplicationContext(), "Importa i turni", Toast.LENGTH_SHORT).show();
        }


        return true;
    }


    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        //intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_RESULT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getActivity().getApplicationContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int req = requestCode;

        if (DEBUG) {
            Log.d(TAG, "request code = " + requestCode);
            Log.d(TAG, "RESULT CODE = " + resultCode);
        }
        switch (requestCode) {
            case (CALENDAR_DIALOG_ACTIVITY_RESULT_CODE):
                if (resultCode == CODE_OK) {
                    ACCOUNT_IS_USED = true;
                    mSharedPref.edit().putBoolean("ACCOUNT_IS_USED", true).commit();
                    String calendarName = data.getStringExtra(RESULT_CALENDAR);
                    String accountName = data.getStringExtra(RESULT_ACCOUNT);
                    // calendar_name  (account_name)
                    mAccountButton.setText(calendarName + "  (" + accountName + ")");
                    mAccountButton.animate().alpha(1f).setDuration(250);
                    SharedPreferences.Editor edit = mSharedPref.edit();
                    edit.putString(SP_CALENDAR_USED, calendarName);
                    edit.putString(SP_ACCOUNT_USED, accountName);
                    edit.commit();
                } else
                    mAccountButton.animate().alpha(1f).setDuration(250);

                break;
            case (COLOR_DIALOG_ACTIVITY_RESULT_CODE):
                if (resultCode == CODE_OK) {
                    int colorSelected = 0;
                    if (TAG_VERONA_COLOR_BUTTON.equals(data.getStringExtra(COLOR_SELECTOR_BUNDLE))) {
                        colorSelected = mSharedPref.getInt(VERONA_COLOR_DEFAULT, 0);
                        int colorDrawable = ColorSelectorDialog.getColorDrawable(colorSelected);
                        Drawable d = getActivity().getResources().getDrawable(colorDrawable);
                        mVeronaColorButton.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
                        mVeronaColorButton.animate().alpha(1f).setDuration(250);
                    } else {
                        colorSelected = mSharedPref.getInt(BASSONA_COLOR_DEFAULT, 0);
                        int colorDrawable = ColorSelectorDialog.getColorDrawable(colorSelected);
                        Drawable d = getActivity().getResources().getDrawable(colorDrawable);
                        mBassonaColorButton.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
                        mBassonaColorButton.animate().alpha(1f).setDuration(250);
                    }
                    break;
                } else {
                    mVeronaColorButton.animate().alpha(1f).setDuration(250);
                    mBassonaColorButton.animate().alpha(1f).setDuration(250);
                }
            case (FILE_SELECT_RESULT_CODE):

                if (DEBUG)
                    Log.d(TAG, "Result ok: " + RESULT_OK);

                if (resultCode != RESULT_OK) return;
                if (null == data) return;
                if (resultCode == RESULT_OK) {

                    if (DEBUG)
                        Log.d(TAG, "Sono dentro all'if del SELECT FILE");

                    // Get the Uri of the selected file
                    Uri uri;
                    try {
                        uri = data.getData();
                    } catch (NullPointerException e) {
                    /*    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Attenzione");
                        alertDialog.setMessage("File non trovato, riprovare!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        */
                        uri = Uri.parse(" ");
                    }


                    if (DEBUG)
                        Log.d(TAG, "File Uri: " + uri.toString());

                    //TODO check if it works
                    //if (uri != null && "content".equals(uri.getScheme())) {
                    //   Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    //   cursor.moveToFirst();
                    //    path = cursor.getString(0);
                    //    cursor.close();
                    //} else {
                    // Get the path
                    //    path = " ";
                    try {
                        path = getPath(getActivity().getApplicationContext(), uri);
                    } catch (NullPointerException e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Path del file non trovato", Toast.LENGTH_SHORT).show();
                    }

                    if (DEBUG)
                        Log.d(TAG, "File Path: " + path);
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                    if (path.isEmpty()) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Attenzione");
                        alertDialog.setMessage("File non trovato, riprovare!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                    try {
                        //Read text from file
                        StringBuilder text = new StringBuilder();
                        String s = "";

                        BufferedReader br = new BufferedReader(new FileReader(path));
                        while ((s = br.readLine()) != null) {
                            text.append(s);
                            text.append('\n');
                        }

                        if (DEBUG)
                            Log.d(TAG, "String text = " + text);

                        // Set TextView text here using tv.setText(s);
                        mEditText.setText(text);

                    } catch (FileNotFoundException e) {
                        /*    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setTitle("Attenzione");
                            alertDialog.setMessage("File non trovato, riprovare!");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                            */
                        path = " ";
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Errore in lettura del file. Sicuro che sia quello giusto?", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    break;
                    //}
                } else {

                    if (DEBUG)
                        Log.d(TAG, "CODE OK = " + CODE_OK);
                }
            case (FORWARD_SELECT_BUTTON):
                if(resultCode == CODE_NOT_OK) {
                    mFowardButton.animate().alpha(1f).setDuration(250);
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
