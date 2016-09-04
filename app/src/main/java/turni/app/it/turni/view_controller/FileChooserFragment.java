package turni.app.it.turni.view_controller;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.net.URISyntaxException;

import turni.app.it.turni.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileChooserFragment extends Fragment {

    public FileChooserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_chooser, container, false);
    }

    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("text/plain");
    intent.addCategory(Intent.CATEGORY_OPENABLE);

    try {
        startActivityForResult(intent, FILE_SELECT_RESULT_CODE, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    } catch (android.content.ActivityNotFoundException ex) {
        // Potentially direct the user to the Market with a Dialog
        Toast.makeText(getActivity(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
    }
}

    public static String getPath(Context context, Uri uri, Activity activity) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
                AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                alertDialog.setTitle("Attenzione");
                alertDialog.setMessage("File non trovato!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
