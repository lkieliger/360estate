package ch.epfl.sweng.project.user.display;

import android.view.View;
import android.widget.CheckBox;

public class OnCheckedFavorite implements View.OnClickListener {

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