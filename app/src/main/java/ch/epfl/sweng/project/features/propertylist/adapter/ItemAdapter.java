package ch.epfl.sweng.project.features.propertylist.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.parse.objects.Item;
import ch.epfl.sweng.project.features.propertylist.layout.ItemLayout;

public class ItemAdapter extends ArrayAdapter<Item> {

    public ItemAdapter(Context c, List<Item> items) {
        super(c, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ItemLayout itemLayout = (ItemLayout) convertView;
        if (null == itemLayout) {
            itemLayout = ItemLayout.inflate(parent);
        }
        itemLayout.setItem(getItem(position));
        itemLayout.setBackground(getContext().getDrawable(R.drawable.item_selector));
        return itemLayout;
    }
}
