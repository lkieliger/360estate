package ch.epfl.sweng.project;

import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.list.Item;

public class DescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        ///////////
        List<Integer> imagesIDs = new ArrayList<Integer>();
        imagesIDs.add(1);
        imagesIDs.add(2);
        imagesIDs.add(3);
        imagesIDs.add(4);
        imagesIDs.add(5);
        View.OnClickListener imgListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "MIIIIIAAAAAAOUUUUUUUUU "+view.getId(), Toast.LENGTH_LONG)
                        .show();
            }
        };


        final LinearLayout scroll_img = (LinearLayout) findViewById(R.id.imgs);
        for(Integer imgID : imagesIDs){
            ImageView imgV = new ImageView(this);
            imgV.setId(imgID);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300, 300);
            params.setMargins(0, 0, 10, 0);
            imgV.setLayoutParams(params);
            imgV.setImageResource(R.drawable.chaton);
            imgV.setOnClickListener(imgListener);
            scroll_img.addView(imgV);
        }
    }
}
