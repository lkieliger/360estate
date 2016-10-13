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
    private TextView price;
    private TextView surface;
    private SeekBar seekBarPrice;
    private SeekBar seekBarSurface;

    /**
     * @param typeSpinner The spinner of the types.
     * @param city The city entered.
     * @param numberOfRooms The number of rooms entered.
     * @param price The price entered.
     * @param surface The surface entered.
     */
    public EraseButtonListener(Spinner typeSpinner, AutoCompleteTextView city, TextView numberOfRooms, TextView price,
                               TextView surface, SeekBar seekBarPrice, SeekBar seekBarSurface) {
        this.typeSpinner = typeSpinner;
        this.city = city;
        this.numberOfRooms = numberOfRooms;
        this.price = price;
        this.surface = surface;
        this.seekBarPrice = seekBarPrice;
        this.seekBarSurface = seekBarSurface;
    }

    @Override
    public void onClick(View v) {
        typeSpinner.setSelection(0);
        city.setText("");
        numberOfRooms.setText("");
        price.setText("");
        surface.setText("");
        seekBarPrice.setProgress(seekBarPrice.getMax());
        seekBarSurface.setProgress(0);
    }
}