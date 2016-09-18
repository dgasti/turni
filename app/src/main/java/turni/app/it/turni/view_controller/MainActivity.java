package turni.app.it.turni.view_controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import turni.app.it.turni.R;


public class MainActivity extends ActionBarActivity {
    private static final boolean DEBUG = true;
    private static final String TAG = "MAIN ACTIVITY";
    public Context context;
    public IBinder windowToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.surname_change_setting:
                surnameDialog(this);
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public void surnameDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);


        dialog.show();

        final EditText surnameText = (EditText) dialog.findViewById(R.id.dialog_surname);
        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok);

        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String surname = surnameText.getText().toString();

                //if (DEBUG)
                //    Log.d(TAG, "surname = " + surname);

                /*if (surname.isEmpty()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Attenzione");
                    alertDialog.setMessage("Non hai inserito il cognome!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.create();
                    alertDialog.show();
                }*/

                //if (DEBUG)
                //    Log.d(TAG, "surname dopo if di controllo = " + surname);

                surname = surname.trim();
                if (surname.isEmpty() == false) {
                    MainFragment.mSurnameText.setText(surname);
                    MainFragment.mSharedPref.edit().putString("SURNAME", surname).commit();

                    if(DEBUG)
                        Log.d(TAG, "COGNOME INSERITO NELLE SHAREDPREF = "+MainFragment.mSharedPref.getString("SURNAME", "nessun cognome"));

                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
    }
}
