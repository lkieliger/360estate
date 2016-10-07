package ch.epfl.sweng.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import ch.epfl.sweng.project.list.Item;
import ch.epfl.sweng.project.list.ItemAdapter;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Get ListView object from xml
        final ListView listView = (ListView) findViewById(R.id.houseList);

        // Defined Array values to show in ListView
        final List<Item> itemList = new ArrayList<>();
        ItemAdapter itemAdapter = new ItemAdapter(this, itemList);

        DataMgmt.getData(itemList, itemAdapter);

        // Assign adapter to ListView
        listView.setAdapter(itemAdapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index

                // ListView Clicked item value
                Item itemValue = (Item) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "MIIIIIAAAAAAOUUUUUUUUU", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }
}