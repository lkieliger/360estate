package ch.epfl.sweng.project.filter;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * An erase button that erase the state of the popup
 */
public class EraseButtonListener implements View.OnClickListener {

    private Spinner typeSpinner;
    private AutoCompleteTextView city;
    private TextView maxRooms;
    private TextView minRooms;
    private TextView maxPrice;
    private TextView minPrice;
    private TextView maxSurface;
    private TextView minSurface;

    public EraseButtonListener(Spinner typeSpinner, AutoCompleteTextView city, TextView maxRooms, TextView
            minRooms, TextView maxPrice, TextView minPrice, TextView maxSurface, TextView minSurface) {
        this.typeSpinner = typeSpinner;
        this.city = city;
        this.maxRooms = maxRooms;
        this.minRooms = minRooms;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.maxSurface = maxSurface;
        this.minSurface = minSurface;
    }

    @Override
    public void onClick(View view) {
        typeSpinner.setSelection(0);
        city.setText("");
        maxRooms.setText("");
        minRooms.setText("");
        maxPrice.setText("");
        minPrice.setText("");
        maxSurface.setText("");
        minSurface.setText("");
    }
}
