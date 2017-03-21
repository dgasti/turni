package turni.app.it.turni.view_controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import turni.app.it.turni.R;

public class SurnameDialog extends ActionBarActivity {

    private static final String SURNAME = "SURNAME";
    private static final int CODE_NOT_OK = 0;
    private static final int CODE_OK = 1;
    private Intent mActivityIntent;
    private static String sSurname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surname_dialog);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ColorSelectorDialog.PlaceholderFragment())
                    .commit();
        }
        //Getting the info from the parent activity
        mActivityIntent = getIntent();
        sSurname = mActivityIntent.getStringExtra(SURNAME);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_color_selector_dialog, menu);
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        private static final String VIEW_BACKGROUND = "view background";
        private static final String SURNAME_BUTTON = "button surname dialog";
        private SharedPreferences sSPref;
        private RelativeLayout sBackground;
        private EditText sSurname;
        private Button sButton;
        private String surname;
        private String surnameCheck;
        //     private int defaultColor;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //Setting up the shared preference
            sSPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            //Inflate the views
            View rootView = inflater.inflate(R.layout.fragment_color_selector_dialog, container, false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.enter_ma_da));
            }

            sBackground = (RelativeLayout) rootView.findViewById(R.id.color_selector_dialog_background);
            sSurname = (EditText) rootView.findViewById(R.id.surname);
            sButton = (Button) rootView.findViewById(R.id.surname_button);

            sBackground.setTag(VIEW_BACKGROUND);
            sButton.setTag(SURNAME_BUTTON);

            sBackground.setOnClickListener(this);
            sSurname.setOnClickListener(this);
            sButton.setOnClickListener(this);

            return rootView;
        }

        @Override
        public void onClick(View v) {
            String tag = (String)v.getTag();
            if(VIEW_BACKGROUND.equals(tag)) {
                getActivity().setResult(CODE_NOT_OK, getActivity().getIntent());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().finishAfterTransition();
                }
                else {
                    getActivity().finish();
                }
            }
            if(SURNAME_BUTTON.equals(tag)) {
                surname = sSurname.getText().toString();
                surnameCheck = sSPref.getString(SURNAME, "");
                if(surname.isEmpty() && surnameCheck.isEmpty()) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Attenzione");
                    alertDialog.setMessage("Non hai inserito il cognome! Inseriscilo prima di continuare.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else if(!(surname.isEmpty())) {
                    sSPref.edit().putString(SURNAME, surname).commit();
                }
            }
            getActivity().setResult(CODE_OK, getActivity().getIntent());
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
