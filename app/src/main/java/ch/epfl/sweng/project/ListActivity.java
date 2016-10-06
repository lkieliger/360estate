package ch.epfl.sweng.project;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.list.Item;
import ch.epfl.sweng.project.list.ItemAdapter;

public class ListActivity extends AppCompatActivity {

    private static final int MIN_VALUE_PRICE = 100000;
    private static final int MAX_VALUE_PRICE = 500000;
    private static final int MIN_VALUE_SURFACE = 20;
    private static final int MAX_VALUE_SURFACE = 400;


    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Button popupButton = (Button)findViewById(R.id.filterButtonPopUp);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopUpFilter();
            }
        });

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.houseList);

        // Defined Array values to show in ListView
        final List<Item> values = new ArrayList<Item>();

        ParseQuery<Item> query = ParseQuery.getQuery("Item");
        query.findInBackground(new FindCallback<Item>() {
            public void done(List<Item> objects, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + objects.size() + " house items");
                    values.addAll(objects);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        Item i1 = new Item();
        i1.setForTest();
        values.add(i1);
        Item i2 = new Item();
        i2.setForTest();
        values.add(i2);

/*
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };
*/
        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

       ItemAdapter adapter = new ItemAdapter(this, values);

        /*ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,  values);
*/

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index

                // ListView Clicked item value
                Item  itemValue    = (Item) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "MIIIIIAAAAAAOUUUUUUUUU" , Toast.LENGTH_LONG)
                        .show();

            }

        });
    }

    void initPopUpFilter(){

        final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("");

        LayoutInflater inflater = getLayoutInflater();

        final ViewGroup nullParent = null;
        final View popupLayout = inflater.inflate(R.layout.popup_filter,nullParent);
        helpBuilder.setView(popupLayout);

        final AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();

        AutoCompleteTextView textView  = (AutoCompleteTextView) popupLayout.findViewById(R.id.autoCompleteTextView);
        final String[] cities = new String[]{
                "Geneve","Renens","Lausanne"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, cities);
        textView.setAdapter(adapter);

        showSeekBar(popupLayout,R.id.seekBarPrice,R.id.ShowPrice,MIN_VALUE_PRICE,MAX_VALUE_PRICE,"Chf");
        showSeekBar(popupLayout,R.id.seekBarSurface,R.id.ShowSurface,MIN_VALUE_SURFACE,MAX_VALUE_SURFACE,"m^2");

    }

    private void showSeekBar(View view,int seekBarId,int textId, int minValue, int maxValue,String units){
        SeekBar seekBar = (SeekBar)view.findViewById(seekBarId);
        TextView text = (TextView)view.findViewById(textId);
        SeekBar.OnSeekBarChangeListener seekBarListenerPrice   = new MyOnSeekBarChangeListener(
                text,minValue,maxValue,units);
        seekBar.setOnSeekBarChangeListener(seekBarListenerPrice);
    }
}