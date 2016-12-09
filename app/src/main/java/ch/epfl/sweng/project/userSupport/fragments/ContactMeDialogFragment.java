package ch.epfl.sweng.project.userSupport.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseUser;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.parse.objects.ClientRequest;

public final class ContactMeDialogFragment extends DialogFragment {

    public static final String TAG = "ContactRequestDialog";

    private String propertyId;
    private String propertyDescription;

    @Override
    public void setArguments(Bundle args) {
        propertyId = args.getString(ClientRequest.LOOKFOR_TAG);
        propertyDescription = args.getString(ClientRequest.DESCRIPTION_TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_contact_me_message);
        builder.setTitle(R.string.dialog_contact_me_title);
        builder.setPositiveButton(R.string.accept_contact_me, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "User requested to be contacted");
                ClientRequest request = new ClientRequest();
                request.setUser(ParseUser.getCurrentUser());
                request.setInterestedId(propertyId);
                request.setPropertyDescription(propertyDescription);
                request.saveInBackground();
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

