package ch.epfl.sweng.project;

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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.epfl.sweng.project.data.Item;
import ch.epfl.sweng.project.data.ItemAdapter;
import ch.epfl.sweng.project.filter.EraseButtonListener;
import ch.epfl.sweng.project.filter.StateOfPopUpLayout;

public class ListActivity extends AppCompatActivity {

    private final String[] cities = new String[]{
            "Geneve", "Renens", "Lausanne"
    };

    private StateOfPopUpLayout stateOfPopUpLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);





        final List<Item> itemList = new ArrayList<>();
        final ItemAdapter itemAdapter = new ItemAdapter(this, itemList);
        final ListView listView = (ListView) findViewById(R.id.houseList);

        Button popupButton = (Button) findViewById(R.id.filterButtonPopUp);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopUpFilter(itemList, itemAdapter, listView);
            }
        });

        Button logOutButton = (Button) findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                logOutUser();
            }
        });



        DataMgmt.getData(itemList, itemAdapter, stateOfPopUpLayout);
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
                intent.putExtra("id", itemValue.getId());
                startActivity(intent);
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
        final TextView numberOfRooms = (TextView) popupLayout.findViewById(R.id.numberOfRooms);
        final TextView maxPrice = (TextView) popupLayout.findViewById(R.id.MaxPrice);
        final TextView minPrice = (TextView) popupLayout.findViewById(R.id.MinPrice);
        final TextView maxSurface = (TextView) popupLayout.findViewById(R.id.MaxSurface);
        final TextView minSurface = (TextView) popupLayout.findViewById(R.id.MinSurface);



        /*
         * Load the last state of popup layout, to display it in the popup.
         */
        if (stateOfPopUpLayout != null) {
            spinner.setSelection(stateOfPopUpLayout.getPositionSpinner());
            city.setText(stateOfPopUpLayout.getCity());
            numberOfRooms.setText(stateOfPopUpLayout.getNumberOfRooms());
            maxPrice.setText(stateOfPopUpLayout.getMaxPrice());
            minPrice.setText(stateOfPopUpLayout.getMinPrice());
            maxSurface.setText(stateOfPopUpLayout.getMaxSurface());
            minSurface.setText(stateOfPopUpLayout.getMinSurface());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        city.setAdapter(adapter);

        Button eraseButton = (Button) popupLayout.findViewById(R.id.eraseButton);
        eraseButton.setOnClickListener(new EraseButtonListener(
                spinner, city, numberOfRooms, maxPrice, minPrice, maxSurface, minSurface));

        Button filterButton = (Button) popupLayout.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                 * Saving the state of popup layout.
                 */
                stateOfPopUpLayout = new StateOfPopUpLayout(
                        spinner.getSelectedItemPosition(),
                        spinner.getSelectedItemPosition(),
                        city.getText().toString(),
                        numberOfRooms.getText().toString(),
                        maxPrice.getText().toString(),
                        minPrice.getText().toString(),
                        maxSurface.getText().toString(),
                        minSurface.getText().toString()
                );
                DataMgmt.getData(itemCollection, itemAdapter, stateOfPopUpLayout);
                listView.setAdapter(itemAdapter);
                helpDialog.dismiss();
            }
        });
    }


    public void logOutUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser != null) {
            ParseUser.logOut();
        }
        finish();
    }


}