package ch.epfl.sweng.project.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {

    public ItemAdapter(Context c, List<Item> items) {
        super(c, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ItemView itemView = (ItemView) convertView;
        if (null == itemView) {
            itemView = ItemView.inflate(parent);
        }
        itemView.setItem(getItem(position));
        return itemView;
    }
}
