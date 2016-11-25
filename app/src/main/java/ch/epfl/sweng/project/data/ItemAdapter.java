package ch.epfl.sweng.project.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.parse.objects.Item;

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
        itemView.setBackground(getContext().getDrawable(R.drawable.item_selector));
        return itemView;
    }
}
