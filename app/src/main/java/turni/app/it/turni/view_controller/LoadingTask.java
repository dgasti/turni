package turni.app.it.turni.view_controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by gasty on 20/09/16.
 */

public class LoadingTask extends AsyncTask<Void, Void, Void> {

    private Activity activity;
    ProgressDialog pDialog;
    Context context;

    public LoadingTask(Activity activity) {
        this.activity = activity;
    }

    protected void onPreExecute() {
        pDialog = new ProgressDialog(activity);
        pDialog.setTitle("Caricando");
        pDialog.setMessage("Un momento di pazienza mentre carico i turni nel calendario...");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    protected Void doInBackground(Void... unused) {

        pDialog.dismiss();

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }

    protected void onPostExecute(Void unused) {
    }

}
