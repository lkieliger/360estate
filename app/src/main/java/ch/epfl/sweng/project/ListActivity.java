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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.epfl.sweng.project.filter.CustomOnSeekBarChangeListener;
import ch.epfl.sweng.project.filter.EraseButtonListener;
import ch.epfl.sweng.project.filter.StateOfPopUpLayout;
import ch.epfl.sweng.project.data.Item;
import ch.epfl.sweng.project.data.ItemAdapter;

public class ListActivity extends AppCompatActivity {

    private static final int MIN_VALUE_PRICE = 100000;
    private static final int MAX_VALUE_PRICE = 500000;
    private static final int MIN_VALUE_SURFACE = 20;
    private static final int MAX_VALUE_SURFACE = 400;
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

        DataMgmt.getData(itemList, itemAdapter, stateOfPopUpLayout);
        // Assign adapter to ListView
        listView.setAdapter(itemAdapter);
        // ListView Item Click Listener
        final Intent intent = new Intent(this, DescriptionActivity.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                Item itemValue = (Item) listView.getItemAtPosition(position);
                intent.putExtra("id", itemValue.getId());

                startActivity(intent);
            }
        });
    }

    /**
     * Display above the seekBar a text to help the user to adjust the seekBar and his selected input.
     *
     * @param seekBar  The seekBar where he can put his input.
     * @param text     The textView to display the value the user entered in the seekBar.
     * @param minValue The minimum value the seekBar authorize.
     * @param maxValue The maximum value the seekBar authorize.
     * @param units    The units in which the input is expressed.
     */
    private void showSeekBar(SeekBar seekBar, TextView text, int minValue, int maxValue, String units) {
        SeekBar.OnSeekBarChangeListener seekBarListenerPrice = new CustomOnSeekBarChangeListener(
                text, minValue, maxValue, units);
        seekBar.setOnSeekBarChangeListener(seekBarListenerPrice);
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
        final SeekBar seekBarPrice = (SeekBar) popupLayout.findViewById(R.id.seekBarPrice);
        final SeekBar seekBarSurface = (SeekBar) popupLayout.findViewById(R.id.seekBarSurface);
        final TextView showPrice = (TextView) popupLayout.findViewById(R.id.showPrice);
        final TextView showSurface = (TextView) popupLayout.findViewById(R.id.showSurface);

        /*
         * Load the last state of popup layout, to display it in the popup.
         */
        if (stateOfPopUpLayout != null) {
            spinner.setSelection(stateOfPopUpLayout.getPositionSpinner());
            city.setText(stateOfPopUpLayout.getCity());
            numberOfRooms.setText(stateOfPopUpLayout.getNumberOfRooms());
            showPrice.setText(stateOfPopUpLayout.getPrice());
            showSurface.setText(stateOfPopUpLayout.getSurface());
            seekBarPrice.setProgress(stateOfPopUpLayout.getSeekBarPricePosition());
            seekBarSurface.setProgress(stateOfPopUpLayout.getSeekBarSurfacePosition());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        city.setAdapter(adapter);

        showSeekBar(seekBarPrice, showPrice, MIN_VALUE_PRICE, MAX_VALUE_PRICE, "Chf");
        showSeekBar(seekBarSurface, showSurface, MIN_VALUE_SURFACE, MAX_VALUE_SURFACE, "m\u00B2");

        Button eraseButton = (Button) popupLayout.findViewById(R.id.eraseButton);
        eraseButton.setOnClickListener(new EraseButtonListener(
                spinner, city, numberOfRooms, showPrice, showSurface, seekBarPrice, seekBarSurface));

        Button filterButton = (Button) popupLayout.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                 * Saving the state of popup layout.
                 */
                stateOfPopUpLayout = new StateOfPopUpLayout(
                        spinner.getSelectedItemPosition(),
                        spinner.getSelectedItemPosition(),
                        city.getText().toString(),
                        numberOfRooms.getText().toString(),
                        showPrice.getText().toString(),
                        showSurface.getText().toString(),
                        seekBarPrice.getProgress(),
                        seekBarSurface.getProgress()
                );
                DataMgmt.getData(itemCollection, itemAdapter, stateOfPopUpLayout);
                listView.setAdapter(itemAdapter);
                helpDialog.dismiss();
            }
        });
    }
}