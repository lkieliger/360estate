package ch.epfl.sweng.project.filter;

import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import ch.epfl.sweng.project.DataMgmt;
import ch.epfl.sweng.project.list.Item;
import ch.epfl.sweng.project.list.ItemAdapter;


public class FilterButtonListener implements View.OnClickListener {

    private AlertDialog alertDialog;
    private StateOfPopUpLayout stateOfPopUpLayout;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;
    private ListView listView;

    public FilterButtonListener(AlertDialog alertDialog, StateOfPopUpLayout stateOfPopUpLayout, List<Item> itemList,
                                ItemAdapter itemAdapter, ListView listView) {
        this.alertDialog = alertDialog;
        this.stateOfPopUpLayout = stateOfPopUpLayout;
        this.itemList = itemList;
        this.itemAdapter = itemAdapter;
        this.listView = listView;
    }

    @Override
    public void onClick(View v) {
        DataMgmt.getData(itemList, itemAdapter, stateOfPopUpLayout);
        listView.setAdapter(itemAdapter);
        alertDialog.dismiss();
    }
}
