package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.engine3d.PanoramaActivity;


public class DescriptionActivity extends AppCompatActivity {

    public static final int cellSize = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        ///////////
        List<Integer> imagesIDs = new ArrayList<>();
        imagesIDs.add(1);
        imagesIDs.add(2);
        imagesIDs.add(3);
        imagesIDs.add(4);
        imagesIDs.add(5);
        View.OnClickListener imgListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "MIIIIIAAAAAAOUUUUUUUUU "+v.getId(), Toast.LENGTH_SHORT)
                        .show();
            }
        };


        final LinearLayout scrollImg = (LinearLayout) findViewById(R.id.imgs);
        for(Integer imgID : imagesIDs){
            ImageView imgV = new ImageView(this);
            imgV.setId(imgID);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cellSize, cellSize);
            params.setMargins(0, 0, 10, 0);
            imgV.setLayoutParams(params);
            imgV.setImageResource(R.drawable.chaton);
            imgV.setOnClickListener(imgListener);
            scrollImg.addView(imgV);
        }

        Button button = (Button) findViewById(R.id.action_launch_panorama);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DescriptionActivity.this, PanoramaActivity.class);
                startActivity(intent);
            }
        });

    }
}
