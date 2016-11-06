package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ch.epfl.sweng.project.ScreenSlide.SlideActivity;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;

import static ch.epfl.sweng.project.DataMgmt.getImgFromUrlIntoView;


public class DescriptionActivity extends AppCompatActivity {

    public static final int cellSize = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Bundle b = getIntent().getExtras();
        //TODO: get imgUrls list from Parse and put it into imagesUrl
        final ArrayList<String> imagesURL = new ArrayList<>();
        imagesURL.add("https://360.astutus.org/estate/chaton1.jpg");
        imagesURL.add("https://360.astutus.org/estate/chaton2.jpg");
        imagesURL.add("https://360.astutus.org/estate/chaton3.jpg");
        imagesURL.add("https://360.astutus.org/estate/chaton4.jpg");
        imagesURL.add("https://360.astutus.org/estate/houseSmall.jpg");

        View.OnClickListener imgListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DescriptionActivity.this, SlideActivity.class);
                Bundle extras = new Bundle();
                extras.putString("URL", (String) v.getTag());
                extras.putStringArrayList("ArrayURL", imagesURL);
                intent.putExtras(extras);
                startActivity(intent);
            }
        };


        final LinearLayout scrollImg = (LinearLayout) findViewById(R.id.imgs);
        for (String url : imagesURL) {
            ImageView imgV = new ImageView(this);
            imgV.setTag(url);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cellSize, cellSize);
            params.setMargins(0, 0, 10, 0);
            imgV.setLayoutParams(params);
            getImgFromUrlIntoView(this, url, imgV);
            imgV.setOnClickListener(imgListener);
            scrollImg.addView(imgV);
        }

        Button button = (Button) findViewById(R.id.action_launch_panorama);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToPanorama = new Intent(DescriptionActivity.this, PanoramaActivity.class);
                Intent intentFromList = getIntent();
                String id = intentFromList.getStringExtra("id");
                intentToPanorama.putExtra("id",id);
                startActivity(intentToPanorama);
            }
        });
    }
}
