package ch.epfl.sweng.project.userSupport.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.parse.ParseUser;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.parse.objects.ClientRequest;

import static ch.epfl.sweng.project.util.Toaster.shortToast;

/**
 * Class used to display a pop-up that will offer the possibility to the user to contact the
 * real estate developer.
 */
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_dialog_fragment, container, false);

        // Watch for button clicks.
        Button acceptButton = (Button) v.findViewById(R.id.contact_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                ClientRequest request = new ClientRequest();
                request.setUser(ParseUser.getCurrentUser());
                request.setInterestedId(propertyId);
                request.setPropertyDescription(propertyDescription);
                request.saveInBackground();
                dismiss();
                shortToast(getActivity().getApplicationContext(), getResources().getText(R.string.success_request));
            }
        });

        Button refuse = (Button)v.findViewById(R.id.contact_refuse);
        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return v;
    }
}

