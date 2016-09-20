package turni.app.it.turni.view_controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by gasty on 20/09/16.
 */

public class LoadingTask extends AsyncTask<Void, Void, Void> {

    ProgressDialog pDialog;
    Context context;

    protected void onPreExecute() {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Uploading events...");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    protected Void doInBackground(Void... unused) {

        // Do ur work

        return (null);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(values);
    }

    protected void onPostExecute(Void unused) {
        pDialog.dismiss();
    }

}
