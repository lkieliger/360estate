package ch.epfl.sweng.project.features.propertylist.listeners;

import android.view.View;
import android.widget.CheckBox;

import ch.epfl.sweng.project.features.propertylist.ListActivity;

public final class OnCheckedFavorite implements View.OnClickListener {

    private String idItem;
    private CheckBox checkBox;

    public OnCheckedFavorite(String idItem, CheckBox checkBox) {
        this.idItem = idItem;
        this.checkBox = checkBox;
    }


    @Override
    public void onClick(View view) {
        if (checkBox.isChecked()) {
            ListActivity.addIdItemToFavorite(idItem);
        } else {
            ListActivity.removeIdItemToFavorite(idItem);
        }
    }
}