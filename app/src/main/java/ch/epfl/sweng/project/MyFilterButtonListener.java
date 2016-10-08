package ch.epfl.sweng.project;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import ch.epfl.sweng.project.list.Item;
import ch.epfl.sweng.project.list.ItemAdapter;


class MyFilterButtonListener implements View.OnClickListener {

    private AlertDialog alertDialog;
    private Filter filter;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;
    private ListView listView;

    MyFilterButtonListener(AlertDialog alertDialog, Filter filter, List<Item> itemList,
                                  ItemAdapter itemAdapter, ListView listView) {
        this.alertDialog = alertDialog;
        this.filter = filter;
        this.itemList = itemList;
        this.itemAdapter = itemAdapter;
        this.listView = listView;
    }

    @Override
    public void onClick(View v) {
        DataMgmt.getData(itemList, itemAdapter,filter);
        listView.setAdapter(itemAdapter);
        alertDialog.dismiss();
    }
}
