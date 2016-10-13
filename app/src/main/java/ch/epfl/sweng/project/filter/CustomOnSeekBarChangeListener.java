package ch.epfl.sweng.project.filter;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A custom seekBar listener that help the user by displaying the value of the seekBar.
 */
public class CustomOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    private TextView textShow;
    private final int minValue;
    private final int maxValue;
    private final String units;
    private int progressChanged;

    /**
     * @param textShow The textView to display the value the user entered in the seekBar.
     * @param minValue The minimum value the seekBar authorize.
     * @param maxValue The maximum value the seekBar authorize.
     * @param units The units in which the input is expressed.
     */
    public CustomOnSeekBarChangeListener(TextView textShow, int minValue, int maxValue, String units) {
        this.maxValue = maxValue;
        this.textShow = textShow;
        this.minValue = minValue;
        this.units = units;
        progressChanged = 0;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progressChanged = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (maxValue > minValue) {
            int res = progressChanged * (maxValue - minValue) / seekBar.getMax() + minValue;
            textShow.setText(String.format("%1$d " + units, res));
        }
    }
}

