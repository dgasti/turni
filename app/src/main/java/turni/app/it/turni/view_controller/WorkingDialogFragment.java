package turni.app.it.turni.view_controller;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import turni.app.it.turni.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class WorkingDialogFragment extends Fragment {

    public WorkingDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_working_dialog, container, false);
    }
}
