package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.ScreenSlide.SlideActivity;
import ch.epfl.sweng.project.data.PhotoSphereData;
import ch.epfl.sweng.project.data.Resources;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;

import static ch.epfl.sweng.project.DataMgmt.getImgFromUrlIntoView;
import static ch.epfl.sweng.project.DataMgmt.getResources;


public class DescriptionActivity extends AppCompatActivity {

    public static final int cellSize = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Bundle b = getIntent().getExtras();
        String id = b.getString("id");
        final ArrayList<String> imagesURL = new ArrayList<>();
        String description = DataMgmt.getResources(id, imagesURL);
        Log.d("description ", description);

        TextView txt = (TextView) findViewById(R.id.description_text);
        txt.setText(description.toCharArray(), 0, description.length());

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
