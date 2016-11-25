package ch.epfl.sweng.project.user.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import ch.epfl.sweng.project.R;

public class ContactMeDialogFragment extends DialogFragment {

    public static final String TAG = "ContactRequestDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_contact_me_message);
        builder.setTitle(R.string.dialog_contact_me_title);
        builder.setPositiveButton(R.string.accept_contact_me, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "User requested to be contacted");
            }
        });
        builder.setNegativeButton(R.string.refuse_contact_me, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "User do not want to be contacted");
            }
        });
        return builder.create();
    }
}

