package ch.epfl.sweng.project.filter;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * An erase button that erase the state of the popup
 */
public class EraseButtonListener implements View.OnClickListener {

    private Spinner typeSpinner;
    private AutoCompleteTextView city;
    private TextView numberOfRooms;
    private TextView maxPrice;
    private TextView minPrice;
    private TextView maxSurface;
    private TextView minSurface;

    public EraseButtonListener(Spinner typeSpinner, AutoCompleteTextView city, TextView numberOfRooms,
                               TextView maxPrice, TextView minPrice, TextView maxSurface, TextView minSurface) {
        this.typeSpinner = typeSpinner;
        this.city = city;
        this.numberOfRooms = numberOfRooms;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.maxSurface = maxSurface;
        this.minSurface = minSurface;
    }

    @Override
    public void onClick(View view) {
        typeSpinner.setSelection(0);
        city.setText("");
        numberOfRooms.setText("");
        maxPrice.setText("");
        minPrice.setText("");
        maxSurface.setText("");
        minSurface.setText("");
    }
}
