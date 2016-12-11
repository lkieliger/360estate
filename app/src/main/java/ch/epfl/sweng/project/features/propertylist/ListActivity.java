package ch.epfl.sweng.project.features.propertylist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.parse.PInterface;
import ch.epfl.sweng.project.data.parse.ParseProxy;
import ch.epfl.sweng.project.data.parse.objects.ClientRequest;
import ch.epfl.sweng.project.data.parse.objects.Favorites;
import ch.epfl.sweng.project.data.parse.objects.Item;
import ch.epfl.sweng.project.features.propertyDescription.DescriptionActivity;
import ch.epfl.sweng.project.features.propertylist.adapter.ItemAdapter;
import ch.epfl.sweng.project.features.propertylist.filter.FilterValues;
import ch.epfl.sweng.project.features.propertylist.listeners.EraseButtonListener;


public final class ListActivity extends AppCompatActivity {


    private static ItemAdapter itemAdapter = null;
    private static List<Item> itemList = new ArrayList<>();
    private static Favorites f = null;
    private final String[] cities = new String[]{
            "Geneve", "Renens", "Lausanne"
    };
    private final String idUser = ParseUser.getCurrentUser().getObjectId();
    private Boolean isFavoriteToggle = false;
    private FilterValues filterValues = null;
    private Context mContext = null;


    private ListView listView;

    private static void setFavorites(Favorites extFavorites) {
        ListActivity.f = extFavorites;
    }

    private static void setItemAdapter(ItemAdapter extItemAdapter) {
        ListActivity.itemAdapter = extItemAdapter;
    }

    public static void addIdItemToFavorite(String idItem) {
        f.addUrlToLocal(idItem);
    }

    public static void removeIdItemToFavorite(String idItem) {
        f.deleteUrlToLocal(idItem);
    }

    public static Boolean favoriteContainsUrl(String idItem) {
        return f.containsUrl(idItem);
    }

    public static void synchronizeServer() {
        if (ParseProxy.PROXY.internetAvailable()) {
            f.synchronizeServer();
        }
    }

    public static void notifyItemAdapter() {
        itemAdapter.notifyDataSetChanged();
    }

    public static void addItem(String id) {
        Item i = PInterface.getItemFromId(id);
        itemList.add(i);
    }

    public static void removeItem(String id) {

        int i = 0;
        while (i < itemList.size() && !Objects.equals(itemList.get(i).getId(), id)) {
            ++i;
        }
        if (i < itemList.size()) {
            itemList.remove(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mContext = getApplicationContext();


        setFavorites(PInterface.getFavoriteFromId(idUser));


        f.synchronizeFromServer();


        setItemAdapter(new ItemAdapter(this, itemList));
        listView = (ListView) findViewById(R.id.houseList);


        ImageButton popupButton = (ImageButton) findViewById(R.id.filterButtonPopUp);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopUpFilter(itemList, itemAdapter, listView);
            }
        });

        ImageButton logOutButton = (ImageButton) findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ListActivity.synchronizeServer();

                logOutUser();
            }
        });


        PInterface.getItemList(itemList, itemAdapter, filterValues, isFavoriteToggle, idUser, mContext
        );
        // Assign adapter to ListView
        listView.setAdapter(itemAdapter);
        // ListView Item Click Listener
        final Intent intent = new Intent(this, DescriptionActivity.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int i, long l) {

                // ListView Clicked item index
                Item itemValue = (Item) listView.getItemAtPosition(i);
                intent.putExtra("isToggled", isFavoriteToggle);
                intent.putExtra("idUser", idUser);
                intent.putExtra("idItem", itemValue.getId());

                StringBuilder descriptionBuilder = new StringBuilder();
                descriptionBuilder.append(getString(itemValue.getType().getDescription())).append(" ");
                descriptionBuilder.append(itemValue.getRooms()).append(" ");
                descriptionBuilder.append(getString(R.string.rooms)).append(" ");
                descriptionBuilder.append(getString(R.string.location_separator)).append(" ");
                descriptionBuilder.append(itemValue.getLocation()).append(" ");
                descriptionBuilder.append(getString(R.string.price_separator)).append(" ");
                descriptionBuilder.append(itemValue.getPrice());
                descriptionBuilder.append(getString(R.string.text_currency));
                intent.putExtra(ClientRequest.DESCRIPTION_TAG, descriptionBuilder.toString());

                startActivity(intent);
            }
        });


        ToggleButton favoriteButton = (ToggleButton) findViewById(R.id.FavoritesButton);
        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                f.synchronizeServer();

                isFavoriteToggle = b;
                PInterface.getItemList(itemList, itemAdapter, filterValues, isFavoriteToggle, idUser, mContext);
            }
        });

    }

    /**
     * @return A inflated layout of the popup.
     */
    private View inflatePopUp() {
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        return inflater.inflate(R.layout.popup_filter, nullParent);
    }

    /**
     * @param popupLayout The inflated layout to be displayed.
     * @return The alertDialog actually displaying the layout.
     */
    private AlertDialog createAlertDialog(View popupLayout) {
        final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("");
        helpBuilder.setView(popupLayout);
        final AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
        return helpDialog;
    }

    /**
     * Initiate the popup filter.
     *
     * @param itemCollection The Collection containing the houses.
     * @param itemAdapter    The adapter that make the link between the collection and the View.
     * @param listView       The view where the items are displayed.
     */
    public void initPopUpFilter(
            final Collection<Item> itemCollection, final ItemAdapter itemAdapter, final ListView listView) {

        View popupLayout = inflatePopUp();
        final AlertDialog helpDialog = createAlertDialog(popupLayout);

        final Spinner spinner = (Spinner) popupLayout.findViewById(R.id.spinner);
        final AutoCompleteTextView city = (AutoCompleteTextView) popupLayout.findViewById(R.id.location);

        final TextView maxRooms = (TextView) popupLayout.findViewById(R.id.MaxRooms);
        final TextView minRooms = (TextView) popupLayout.findViewById(R.id.MinRooms);
        final TextView maxPrice = (TextView) popupLayout.findViewById(R.id.MaxPrice);
        final TextView minPrice = (TextView) popupLayout.findViewById(R.id.MinPrice);
        final TextView maxSurface = (TextView) popupLayout.findViewById(R.id.MaxSurface);
        final TextView minSurface = (TextView) popupLayout.findViewById(R.id.MinSurface);



        /*
         * Load the last state of popup layout, to display it in the popup.
         */
        if (filterValues != null) {
            spinner.setSelection(filterValues.getPositionSpinner());
            city.setText(filterValues.getCity());
            maxRooms.setText(filterValues.getMaxRooms());
            minRooms.setText(filterValues.getMinRooms());
            maxPrice.setText(filterValues.getMaxPrice());
            minPrice.setText(filterValues.getMinPrice());
            maxSurface.setText(filterValues.getMaxSurface());
            minSurface.setText(filterValues.getMinSurface());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        city.setAdapter(adapter);

        Button eraseButton = (Button) popupLayout.findViewById(R.id.eraseButton);
        eraseButton.setOnClickListener(new EraseButtonListener(
                spinner, city, maxRooms, minRooms, maxPrice, minPrice, maxSurface, minSurface));

        Button filterButton = (Button) popupLayout.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                 * Saving the state of popup layout.
                 */
                filterValues = new FilterValues(
                        spinner.getSelectedItemPosition(),
                        spinner.getSelectedItemPosition(),
                        city.getText().toString(),
                        maxRooms.getText().toString(),
                        minRooms.getText().toString(),
                        maxPrice.getText().toString(),
                        minPrice.getText().toString(),
                        maxSurface.getText().toString(),
                        minSurface.getText().toString()
                );
                PInterface.getItemList(itemCollection, itemAdapter, filterValues, isFavoriteToggle,
                        idUser, mContext);
                listView.setAdapter(itemAdapter);
                helpDialog.dismiss();
            }
        });
    }


    public void logOutUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseUser.logOut();
        }

        finish();
    }

    @Override
    protected void onStop() {
        ListActivity.synchronizeServer();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true); // HOME BUTTON behavior.
    }


}