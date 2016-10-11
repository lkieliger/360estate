package ch.epfl.sweng.project.filter;

import android.widget.SeekBar;
import android.widget.TextView;

public class CustomOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
    private TextView textShow;
    private final int minValue;
    private final int maxValue;
    private final String units;
    private int progressChanged;

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

