package ch.epfl.sweng.project;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.Collection;
import java.util.List;

import ch.epfl.sweng.project.filter.StateOfPopUpLayout;
import ch.epfl.sweng.project.data.Item;
import ch.epfl.sweng.project.data.ItemAdapter;

public final class DataMgmt {

    private DataMgmt() {
    }


    public static void getData(
            final Collection<Item> itemList, final ItemAdapter itemAdapter, StateOfPopUpLayout stateOfPopUpLayout) {
        ParseQuery<Item> query;
        if(stateOfPopUpLayout == null) {
            query = ParseQuery.getQuery("Item");
        }
        else{
            query = stateOfPopUpLayout.filterQuery();
        }
        query.findInBackground(new FindCallback<Item>() {
            public void done(List<Item> objects, ParseException e) {
                if (e == null) {
                    Log.d("DataMgmt", "Retrieved " + objects.size() + " house items");
                    itemList.clear();
                    itemList.addAll(objects);
                    itemAdapter.notifyDataSetChanged();

                } else {
                    Log.d("DataMgmt", "Error: " + e.getMessage());
                }
            }
        });
    }


}
