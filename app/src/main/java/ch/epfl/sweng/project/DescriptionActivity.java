package ch.epfl.sweng.project;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ch.epfl.sweng.project.ScreenSlide.SlideActivity;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;

import static ch.epfl.sweng.project.DataMgmt.getImgFromUrlIntoView;
import static ch.epfl.sweng.project.util.InternetAvailable.isInternetAvailable;
import static ch.epfl.sweng.project.util.Toaster.longToast;


public class DescriptionActivity extends AppCompatActivity {

    public static final int cellSize = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Bundle b = getIntent().getExtras();
        String id = b.getString("id");
        final ArrayList<String> imagesURL = new ArrayList<>();
        String description = DataMgmt.getDataForDescription(id, imagesURL, getApplicationContext());

        if (description != null) {
            Log.d("description ", description);

            TextView txt = (TextView) findViewById(R.id.description_text);
            txt.setText(description.toCharArray(), 0, description.length());

            View.OnClickListener imgListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DescriptionActivity.this, SlideActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("URL", (String) view.getTag());
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
                getImgFromUrlIntoView(this, url, imgV); // TODO cache image via picasso
                imgV.setOnClickListener(imgListener);
                scrollImg.addView(imgV);
            }

            Button button = (Button) findViewById(R.id.action_launch_panorama);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isInternetAvailable(getApplicationContext())) {
                        Intent intentToPanorama = new Intent(DescriptionActivity.this, PanoramaActivity.class);
                        Intent intentFromList = getIntent();
                        String id = intentFromList.getStringExtra("id");
                        intentToPanorama.putExtra("id", id);
                        startActivity(intentToPanorama);
                    } else {
                        longToast(getApplicationContext(), "Panorama view is not available without internet.");
                    }
                }
            });

        }


    }

}
