package turni.app.it.turni.view_controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;

import turni.app.it.turni.R;

public class ColorSelectorDialog extends ActionBarActivity {

    public static final int COLOR_1 = 1;
    public static final int COLOR_2 = 2;
    public static final int COLOR_3 = 3;
    public static final int COLOR_4 = 4;
    public static final int COLOR_5 = 5;
    public static final int COLOR_6 = 6;
    public static final int COLOR_7 = 7;
    public static final int COLOR_8 = 8;
    public static final int COLOR_9 = 9;
    public static final int COLOR_10 = 10;
    public static final int COLOR_11 = 11;
    private static final int CODE_OK = 1;
    /**
     * Activity resul code not Ok
     */
    private static final int CODE_NOT_OK = 0;
    private static final boolean DEBUG = true;
    private static final String TAG = "COLORSELECTORDIALOG";
    private static final String COLOR_SELECTOR_BUNDLE = "color selector bundle";
    private static final String TAG_VERONA_COLOR_BUTTON = "tag verona color button";
    private static final String TAG_BASSONA_COLOR_BUTTON = "tag bassona color button";
    private static final String BASSONA_COLOR_DEFAULT = "bassona color default";
    private static final String VERONA_COLOR_DEFAULT = "result color selected";
    private static final String TAG_RECOVERY_COLOR_BUTTON = "tag recovery color button";
    private static final String RECOVERY_COLOR_DEFAULT = "recovery color default";

    /**
     * It represent the location to which the class must assign the color
     */
    private static String mLocationColor;
    private Intent mActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selector_dialog);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        //Getting the info from the parent activity
        mActivityIntent = getIntent();
        mLocationColor = mActivityIntent.getStringExtra(COLOR_SELECTOR_BUNDLE);
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
        private Button[] mColorView = new Button[12];
        private SharedPreferences mSPref;
        private RelativeLayout mBackground;
        //     private int defaultColor;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //Setting up the shared preference
            mSPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), getActivity().MODE_PRIVATE);
            int colorDefault = 0;
            //Get the color previously assigned to Verona or Bassona
            if (TAG_VERONA_COLOR_BUTTON.equals(mLocationColor)) {

                if (DEBUG) {
                    Log.d(TAG, "mLocationColor = " + mLocationColor);
                    Log.d(TAG, "Sono dentro a: " + TAG_VERONA_COLOR_BUTTON);
                }

                colorDefault = mSPref.getInt(VERONA_COLOR_DEFAULT, 1);

                if (DEBUG) {
                    Log.d(TAG, "ColorDefault = " + colorDefault);
                }
            }
            if (TAG_BASSONA_COLOR_BUTTON.equals(mLocationColor))
                colorDefault = mSPref.getInt(BASSONA_COLOR_DEFAULT, 1);
            if (TAG_RECOVERY_COLOR_BUTTON.equals(mLocationColor))
                colorDefault = mSPref.getInt(RECOVERY_COLOR_DEFAULT, 1);
            //Inflate the views
            View rootView = inflater.inflate(R.layout.fragment_color_selector_dialog, container, false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.enter_ma_da));
            }
            mColorView[1] = (Button) rootView.findViewById(R.id.row_color_1);
            mColorView[2] = (Button) rootView.findViewById(R.id.row_color_2);
            mColorView[3] = (Button) rootView.findViewById(R.id.row_color_3);
            mColorView[4] = (Button) rootView.findViewById(R.id.row_color_4);
            mColorView[5] = (Button) rootView.findViewById(R.id.row_color_5);
            mColorView[6] = (Button) rootView.findViewById(R.id.row_color_6);
            mColorView[7] = (Button) rootView.findViewById(R.id.row_color_7);
            mColorView[8] = (Button) rootView.findViewById(R.id.row_color_8);
            mColorView[9] = (Button) rootView.findViewById(R.id.row_color_9);
            mColorView[10] = (Button) rootView.findViewById(R.id.row_color_10);
            mColorView[11] = (Button) rootView.findViewById(R.id.row_color_11);

            mBackground=(RelativeLayout)rootView.findViewById(R.id.color_selector_dialog_background);

            //modify the text for the button that has the precedent color
            String colorButtonText = (String) mColorView[colorDefault].getText();
            colorButtonText = colorButtonText;
            mColorView[colorDefault].setText(colorButtonText);
            mColorView[colorDefault].setCompoundDrawablesWithIntrinsicBounds(getColorDrawable(colorDefault), 0, R.drawable.check, 0);

            //   catch (Exception e){}
            for (int i = 1; i < 12; i++) {
                mColorView[i].setTag(i+"");
                mColorView[i].setOnClickListener(this);
            }
            mBackground.setTag(VIEW_BACKGROUND);
            mBackground.setOnClickListener(this);

            return rootView;
        }

        @Override
        public void onClick(View view) {
            String tag=(String)view.getTag();
            if(VIEW_BACKGROUND.equals(tag)){
                getActivity().setResult(CODE_NOT_OK, getActivity().getIntent());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().finishAfterTransition();
                }
                else {
                    getActivity().finish();
                }
            }
            else {
                //Get the color selected
                int colorSelected = Integer.parseInt((String) view.getTag());
                SharedPreferences.Editor edit = mSPref.edit();
                //Set the selected color as the new default
                if (TAG_VERONA_COLOR_BUTTON.equals(mLocationColor)) {
                    edit.putInt(VERONA_COLOR_DEFAULT, colorSelected);
                }
                if (TAG_BASSONA_COLOR_BUTTON.equals(mLocationColor)) {
                    edit.putInt(BASSONA_COLOR_DEFAULT, colorSelected);
                }
                if (TAG_RECOVERY_COLOR_BUTTON.equals(mLocationColor)) {
                    edit.putInt(RECOVERY_COLOR_DEFAULT, colorSelected);
                }
                edit.commit();
                int c = mSPref.getInt(VERONA_COLOR_DEFAULT, 0);
                getActivity().setResult(CODE_OK, getActivity().getIntent());
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

    public static int getColorDrawable(int color) {
        switch (color) {
            case COLOR_1:
                return R.drawable.lavanda;
            case COLOR_2:
                return R.drawable.salvia;
            case COLOR_3:
                return R.drawable.vinaccia;
            case COLOR_4:
                return R.drawable.fenicottero;
            case COLOR_5:
                return R.drawable.banana;
            case COLOR_6:
                return R.drawable.mandarino;
            case COLOR_7:
                return R.drawable.pavone;
            case COLOR_8:
                return R.drawable.grafite;
            case COLOR_9:
                return R.drawable.mirtillo;
            case COLOR_10:
                return R.drawable.basilico;
            case COLOR_11:
                return R.drawable.pomodoro;
        }
        return 0;
    }
}
