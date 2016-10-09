package ch.epfl.sweng.project;


import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseQuery;

import ch.epfl.sweng.project.list.Item;

class Filter {


    private static final double MIN_COEFF = 0.9;
    private static final double MAX_COEFF = 1.1;

    private Spinner typeSpinner;
    private AutoCompleteTextView city;
    private TextView numberOfRooms;
    private TextView price;
    private TextView surface;
    private SeekBar seekBarPrice;
    private SeekBar seekBarSurface;

    Filter(Spinner typeSpinner, AutoCompleteTextView city, TextView numberOfRooms,
           TextView price, TextView surface, SeekBar seekBarPrice, SeekBar seekBarSurface) {

        this.typeSpinner = typeSpinner;
        this.city = city;
        this.numberOfRooms = numberOfRooms;
        this.price = price;
        this.surface = surface;
        this.seekBarPrice = seekBarPrice;
        this.seekBarSurface = seekBarSurface;
    }

    ParseQuery<Item> filterQuery() {
        ParseQuery<Item> query = ParseQuery.getQuery("Item");

        Boolean isTypeFiltered;
        Boolean isCityFiltered;
        Boolean isNbrOfRoomsFiltered;
        Boolean isPriceFiltered;
        Boolean isSurfaceFiltered;

        /*
        if(isTypeFiltered || !typeSpinner.getSelectedItem().equals("All")){
            query.whereEqualTo("type",typeSpinner.getSelectedItem());
        }*/


        isNbrOfRoomsFiltered = !numberOfRooms.getText().toString().equals("");

        if (isNbrOfRoomsFiltered) {
            query.whereEqualTo("rooms", Integer.parseInt(numberOfRooms.getText().toString()));
        }


        isPriceFiltered = !price.getText().toString().equals("");

        if (isPriceFiltered) {
            int temp = Integer.parseInt(price.getText().toString().split(" ")[0]);
            query.whereLessThanOrEqualTo("price", temp * MIN_COEFF);
            query.whereGreaterThanOrEqualTo("price", temp * MAX_COEFF);
        }

        isSurfaceFiltered = !surface.getText().toString().equals("");

        if (isSurfaceFiltered) {
            int temp = Integer.parseInt(surface.getText().toString().split(" ")[0]);
            query.whereLessThanOrEqualTo("surface", temp * MAX_COEFF);
            query.whereGreaterThanOrEqualTo("surface", temp * MIN_COEFF);
        }
        return query;
    }

    TextView getSurface() {
        return surface;
    }

    Spinner getTypeSpinner() {return typeSpinner;}

    AutoCompleteTextView getCity() {
        return city;
    }

    TextView getNumberOfRooms() {
        return numberOfRooms;
    }

    TextView getPrice() {
        return price;
    }

    SeekBar getSeekBarSurface() {
        return seekBarSurface;
    }

    SeekBar getSeekBarPrice() {
        return seekBarPrice;
    }
}
