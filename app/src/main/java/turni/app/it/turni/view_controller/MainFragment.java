package turni.app.it.turni.view_controller;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import model.Util;
import turni.app.it.turni.R;

public class MainFragment extends Fragment implements View.OnClickListener {


    private static final boolean DEBUG = true;
    private static final String TAG = "MAINFRAGMENT";
    private static final String TURN_TEXT = "LAUNCH_WORKINGACTIVITY";
    private static final String TAG_FOWARD_BUTTON = "foward button";
    private static final String TAG_ACCOUNT_BUTTON = "calendar button";
    private static final int CALENDAR_DIALOG_ACTIVITY_RESULT_CODE = 1;
    private static final int COLOR_DIALOG_ACTIVITY_RESULT_CODE = 2;
    private static final String RESULT_COLOR_SELECTED = "result color selected";
    private static final String CALENDAR_ROW = "calendar row";
    private static final String SURNAME_TEXT = "surname";
    /**
     * Dialog Account is used?
     */
    private static boolean ACCOUNT_IS_USED = false;
    /**
     * Activity result intent Key
     */
    private static final String RESULT_CALENDAR = "result calendar";
    /**
     * Activity resul code Ok
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
    private static final String TAG_SURNAME_CHANGE = "change surname";
    private static final String TAG_PASTE_TEXT = "paste text";

    private View mView;
    private Context context;
    private FloatingActionButton mFowardButton;
    private EditText mEditText;
    private String mText;
    private TextView mTextView, mSurnameText;
    private Button mAccountButton, mSurnameChangeButton, mPasteButton;
    private SharedPreferences mSharedPref;
    private Button mVeronaColorButton;
    private Button mBassonaColorButton;
    private boolean openVerona = false;
    private Button mImportTextButton;
    private String surname = null;
    private String surname_check = null;

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
        mSurnameChangeButton = (Button) mView.findViewById(R.id.surname_change);
        mPasteButton = (Button) mView.findViewById(R.id.paste_text);


        mFowardButton.setTag(TAG_FOWARD_BUTTON);
        mAccountButton.setTag(TAG_ACCOUNT_BUTTON);
        mVeronaColorButton.setTag(TAG_VERONA_COLOR_BUTTON);
        mBassonaColorButton.setTag(TAG_BASSONA_COLOR_BUTTON);
        mImportTextButton.setTag(TAG_IMPORT_TEXT_BUTTON);
        mSurnameChangeButton.setTag(TAG_SURNAME_CHANGE);
        mPasteButton.setTag(TAG_PASTE_TEXT);

        int drawableColor = ColorSelectorDialog.getColorDrawable(mSharedPref.getInt(VERONA_COLOR_DEFAULT, 1));
        Drawable d = getResources().getDrawable(drawableColor);
        mVeronaColorButton.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
        drawableColor = ColorSelectorDialog.getColorDrawable(mSharedPref.getInt(BASSONA_COLOR_DEFAULT, 1));
        d = getResources().getDrawable(drawableColor);
        mBassonaColorButton.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);

        surname_check = mSharedPref.getString("SURNAME", null);

        if (DEBUG) {
            Log.d(TAG, "surname_check = " + surname_check);
        }

        if (surname_check == null) {
            showSurnameDialog(getActivity());
        } else {
            surname_check = surname_check.trim();
            mSurnameText.setText("Ciao " + surname_check + "!");
        }


        mFowardButton.setOnClickListener(this);
        mAccountButton.setOnClickListener(this);
        mVeronaColorButton.setOnClickListener(this);
        mBassonaColorButton.setOnClickListener(this);
        mSurnameChangeButton.setOnClickListener(this);
        mImportTextButton.setOnClickListener(this);
        mPasteButton.setOnClickListener(this);


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

    //TODO fix starting popup
    public void showSurnameDialog(Activity activity) {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

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

                if (surname.isEmpty() == false) {
                    mSurnameText.setText("Ciao " + surname + "!");
                    dialog.dismiss();
                }
                if (DEBUG)
                    Log.d(TAG, "Surname dopo if del dismiss = " + surname);
            }
        });

    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if(DEBUG)
            Log.d(TAG, "tag selezionato = "+tag);
        boolean account_is_used = mSharedPref.getBoolean("ACCOUNT_IS_USED", false);
        if (TAG_SURNAME_CHANGE.equals(tag)) {
            surname_check = null;
            if (surname_check == null) {
                showSurnameDialog(getActivity());
            } else {
                surname_check = surname_check.trim();
                mSurnameText.setText("Ciao " + surname_check + "!");
            }        }
        if (TAG_IMPORT_TEXT_BUTTON.equals(tag)) {
            Toast.makeText(getActivity().getApplicationContext(), "Funzione ancora non attiva!", Toast.LENGTH_SHORT).show();
        }
        if (TAG_PASTE_TEXT.equals(tag)) {
            if(DEBUG)
                Log.d(TAG, "Ho cliccato il tasto dell'incolla");
            //Toast.makeText(getActivity().getApplicationContext(), "Funzione ancora non attiva!", Toast.LENGTH_LONG).show();
            String pasteData = "";
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            pasteData = (String) item.getText().toString();
            mEditText.setText(pasteData);
            Toast.makeText(getActivity().getApplicationContext(), "Incollato", Toast.LENGTH_SHORT).show();
        }
        String text = mEditText.getText().toString();
        if (TAG_FOWARD_BUTTON.equals(tag)) {
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
            } else {
                Intent intent = new Intent(getActivity(), WorkingActivity.class);
                intent.putExtra(TURN_TEXT, text);
                if (surname_check != null) {
                    intent.putExtra(SURNAME_TEXT, surname_check);
                } else {
                    intent.putExtra(SURNAME_TEXT, surname);
                }
                getActivity().getWindow().setExitTransition(null);
                getActivity().getWindow().setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.enter_ma_dwa));
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        }

        if (TAG_ACCOUNT_BUTTON.equals(tag))

        {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int req = requestCode;
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
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
