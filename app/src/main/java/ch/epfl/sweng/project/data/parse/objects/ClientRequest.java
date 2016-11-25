package ch.epfl.sweng.project.data.parse.objects;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ClientRequest")
public class ClientRequest extends ParseObject {

    public static final String LOOKFOR_TAG = "propertyId";
    private static final String NAME_TAG = "name";
    private static final String LASTNAME_TAG = "lastName";
    private static final String EMAIL_TAG = "email";
    private static final String PHONE_TAG = "phone";

    public ClientRequest() {
    }

    public void setFromUser(ParseUser user) {
        setName("Señor", "Sanchez");
        setEmail(user.getEmail());
        setPhone(user.getString(PHONE_TAG));
    }

    private void setName(String name, String lastname) {
        put(NAME_TAG, name);
        put(LASTNAME_TAG, lastname);
    }

    public void setInterestedId(String id) {
        put(LOOKFOR_TAG, id);
    }

    public void setEmail(String email) {
        put(EMAIL_TAG, email);
    }

    public void setPhone(String phone) {
        put(PHONE_TAG, phone);
    }

}
