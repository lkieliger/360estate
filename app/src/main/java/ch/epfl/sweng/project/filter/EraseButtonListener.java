package ch.epfl.sweng.project.filter;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

public class EraseButtonListener implements View.OnClickListener {

    private Spinner typeSpinner;
    private AutoCompleteTextView city;
    private TextView numberOfRooms;
    private TextView price;
    private TextView Surface;

    public EraseButtonListener(Spinner typeSpinner, AutoCompleteTextView city, TextView numberOfRooms, TextView price,
                               TextView surface) {
        this.typeSpinner = typeSpinner;
        this.city = city;
        this.numberOfRooms = numberOfRooms;
        this.price = price;
        Surface = surface;
    }

    @Override
    public void onClick(View v) {
        typeSpinner.setSelection(0);
        city.setText("");
        numberOfRooms.setText("");
        price.setText("");
        Surface.setText("");
    }
}
