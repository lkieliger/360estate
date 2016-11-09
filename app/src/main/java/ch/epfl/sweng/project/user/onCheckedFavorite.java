package ch.epfl.sweng.project.user;

import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.Collection;

import ch.epfl.sweng.project.DataMgmt;

public class OnCheckedFavorite implements View.OnClickListener {

    private Favorites f;
    private String idItem;
    private String idUser;
    private CheckBox checkBox;

    public OnCheckedFavorite(Favorites f, String idItem, String idUser, CheckBox checkBox) {
        this.f = f;
        this.idItem = idItem;
        this.idUser = idUser;
        this.checkBox = checkBox;
    }


    @Override
    public void onClick(View view) {
        if (checkBox.isChecked()) {
            Collection<String> l = new ArrayList<>();
            l.add(idItem);
            DataMgmt.updateFavorites(idUser,l);
        } else {
            f.deleteUrlToLocal(idItem);
            f.synchronizeServer(idUser);
        }
    }
}