package ch.epfl.sweng.project;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.filter.StateOfPopUpLayout;
import ch.epfl.sweng.project.filter.EraseButtonListener;
import ch.epfl.sweng.project.filter.FilterButtonListener;
import ch.epfl.sweng.project.filter.CustomOnSeekBarChangeListener;
import ch.epfl.sweng.project.list.Item;
import ch.epfl.sweng.project.list.ItemAdapter;

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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                Item itemValue = (Item) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        itemValue.getType().toString(), Toast.LENGTH_LONG)
                        .show();


            }
        });
    }

    public void initPopUpFilter(List<Item> itemList, ItemAdapter itemAdapter, ListView listView) {


        View popupLayout = inflatePopUp();
        AlertDialog helpDialog = createAlertDialog(popupLayout);

        Spinner spinner = (Spinner) popupLayout.findViewById(R.id.spinner);
        AutoCompleteTextView city = (AutoCompleteTextView) popupLayout.findViewById(R.id.autoCompleteTextView);
        TextView numberOfRooms = (TextView) popupLayout.findViewById(R.id.numberOfRooms);
        SeekBar seekBarPrice = (SeekBar) popupLayout.findViewById(R.id.seekBarPrice);
        SeekBar seekBarSurface = (SeekBar) popupLayout.findViewById(R.id.seekBarSurface);
        TextView showPrice = (TextView) popupLayout.findViewById(R.id.showPrice);
        TextView showSurface = (TextView) popupLayout.findViewById(R.id.showSurface);


        if(stateOfPopUpLayout != null){
            stateOfPopUpLayout.recoverFilter(
                    new StateOfPopUpLayout(
                            spinner,city,numberOfRooms,showPrice,showSurface,seekBarPrice,seekBarSurface));
        }


        stateOfPopUpLayout = new StateOfPopUpLayout(
                        spinner, city, numberOfRooms, showPrice, showSurface, seekBarPrice, seekBarSurface);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        city.setAdapter(adapter);

        showSeekBar(seekBarPrice, showPrice, MIN_VALUE_PRICE, MAX_VALUE_PRICE, "Chf");
        showSeekBar(seekBarSurface, showSurface, MIN_VALUE_SURFACE, MAX_VALUE_SURFACE, "m\u00B2");

        Button eraseButton = (Button) popupLayout.findViewById(R.id.eraseButton);
        eraseButton.setOnClickListener(
                new EraseButtonListener(spinner, city, numberOfRooms, showPrice, showSurface));

        Button filterButton = (Button) popupLayout.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(
                new FilterButtonListener(helpDialog, stateOfPopUpLayout, itemList, itemAdapter, listView));
    }

    private void showSeekBar(SeekBar seekBar, TextView text, int minValue, int maxValue, String units) {
        SeekBar.OnSeekBarChangeListener seekBarListenerPrice = new CustomOnSeekBarChangeListener(
                text, minValue, maxValue, units);
        seekBar.setOnSeekBarChangeListener(seekBarListenerPrice);
    }

    private View inflatePopUp(){
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        return inflater.inflate(R.layout.popup_filter, nullParent);
    }

    private AlertDialog createAlertDialog(View popupLayout) {
        final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("");
        helpBuilder.setView(popupLayout);
        final AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
        return helpDialog;
    }
}