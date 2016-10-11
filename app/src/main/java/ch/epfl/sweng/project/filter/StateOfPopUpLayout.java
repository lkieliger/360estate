package ch.epfl.sweng.project.filter;


import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.ParseQuery;

import ch.epfl.sweng.project.list.Item;

public class StateOfPopUpLayout {

    private static final double MIN_COEFF = 0.9;
    private static final double MAX_COEFF = 1.1;

    private Spinner typeSpinner;
    private AutoCompleteTextView city;
    private TextView numberOfRooms;
    private TextView price;
    private TextView surface;
    private SeekBar seekBarPrice;
    private SeekBar seekBarSurface;

    public StateOfPopUpLayout(Spinner typeSpinner, AutoCompleteTextView city, TextView numberOfRooms,
                              TextView price, TextView surface, SeekBar seekBarPrice, SeekBar seekBarSurface) {

        this.typeSpinner = typeSpinner;
        this.city = city;
        this.numberOfRooms = numberOfRooms;
        this.price = price;
        this.surface = surface;
        this.seekBarPrice = seekBarPrice;
        this.seekBarSurface = seekBarSurface;
    }


    private TextView getSurface() {
        return surface;
    }

    private Spinner getTypeSpinner() {
        return typeSpinner;
    }

    private AutoCompleteTextView getCity() {
        return city;
    }

    private TextView getNumberOfRooms() {
        return numberOfRooms;
    }

    private TextView getPrice() {
        return price;
    }

    private SeekBar getSeekBarSurface() {
        return seekBarSurface;
    }

    private SeekBar getSeekBarPrice() {
        return seekBarPrice;
    }


    public ParseQuery<Item> filterQuery() {

        ParseQuery<Item> query = ParseQuery.getQuery("Item");

        String typeSelected = typeSpinner.getSelectedItem().toString();
        String citySelected = city.getText().toString();
        String roomSelected = numberOfRooms.getText().toString();
        String priceSelected = price.getText().toString();
        String surfaceSelected = surface.getText().toString();

        Boolean isTypeFiltered = !typeSelected.equals("All");
        Boolean isCityFiltered = !citySelected.equals("");
        Boolean isNbrOfRoomsFiltered = !roomSelected.equals("");
        Boolean isPriceFiltered = !priceSelected.equals("");
        Boolean isSurfaceFiltered = !surfaceSelected.equals("");


        if (isTypeFiltered) {
            try {
                query.whereEqualTo("type", Item.HouseType.valueOf(typeSelected.toUpperCase()).ordinal());
            } catch (IllegalArgumentException e) {
                System.err.print("IllegalArgumentException" + e.getMessage());
            }
        }

        if (isCityFiltered) {
            query.whereEqualTo("location", citySelected);
        }

        if (isNbrOfRoomsFiltered) {
            try {
                query.whereEqualTo("rooms", Integer.parseInt(roomSelected));
            } catch (NumberFormatException e) {
                System.err.print("NumberFormatException" + e.getMessage());
            }
        }

        if (isPriceFiltered) {
            try {
                int temp = Integer.parseInt(price.getText().toString().split(" ")[0]);
                query.whereLessThanOrEqualTo("price", temp * MAX_COEFF);
            } catch (NumberFormatException e) {
                System.err.print("NumberFormatException" + e.getMessage());
            }
        }

        if (isSurfaceFiltered) {
            try {
                int temp = Integer.parseInt(surface.getText().toString().split(" ")[0]);
                query.whereGreaterThanOrEqualTo("surface", temp * MIN_COEFF);
            } catch (NumberFormatException e) {
                System.err.print("NumberFormatException" + e.getMessage());
            }
        }

        return query;
    }

    public void recoverFilter(StateOfPopUpLayout other) {
        other.getTypeSpinner().setSelection(getTypeSpinner().getSelectedItemPosition());
        other.getCity().setText(getCity().getText());
        other.getNumberOfRooms().setText(getNumberOfRooms().getText());
        other.getPrice().setText(getPrice().getText());
        other.getSurface().setText(getSurface().getText());
        other.getSeekBarPrice().setProgress(getSeekBarPrice().getProgress());
        other.getSeekBarSurface().setProgress(getSeekBarSurface().getProgress());
    }
}
