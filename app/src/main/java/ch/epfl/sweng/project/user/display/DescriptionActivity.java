package ch.epfl.sweng.project.user.display;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ch.epfl.sweng.project.DataMgmt;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;
import ch.epfl.sweng.project.slider.SlideActivity;

import static ch.epfl.sweng.project.DataMgmt.getImgFromUrlIntoView;
import static ch.epfl.sweng.project.util.InternetAvailable.isInternetAvailable;
import static ch.epfl.sweng.project.util.Toaster.shortToast;


public class DescriptionActivity extends AppCompatActivity {

    public static final int cellSize = 300;
    private boolean isInitiallyInFavorite = false;
    private CheckBox checkBoxFavorite = null;
    private String idItem = null;
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        mContext = getApplicationContext();


        idItem = getIntent().getStringExtra("idItem");
        final ArrayList<String> imagesURL = new ArrayList<>();
        StringBuilder descriptionBuilder = new StringBuilder();

        DataMgmt.getDataForDescription(idItem, imagesURL, descriptionBuilder, mContext);
        String description = descriptionBuilder.toString();


        if (!description.equals("")) {


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
                getImgFromUrlIntoView(this, url, imgV);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(cellSize, cellSize);
                params.setMargins(0, 0, 10, 0);
                imgV.setLayoutParams(params);
                imgV.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imgV.setOnClickListener(imgListener);
                scrollImg.addView(imgV);
            }

            Button button = (Button) findViewById(R.id.action_launch_panorama);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isInternetAvailable(mContext)) {
                        Intent intentToPanorama = new Intent(DescriptionActivity.this, PanoramaActivity.class);
                        intentToPanorama.putExtra("id", idItem);
                        startActivity(intentToPanorama);
                    } else {

                        shortToast(mContext, mContext.getResources().getText(R.string.no_panorama_view));
                    }
                }
            });

            checkBoxFavorite = (CheckBox) findViewById(R.id.addToFavorites);

            isInitiallyInFavorite = ListActivity.favoriteContainsUrl(idItem);

            if (isInitiallyInFavorite) {
                checkBoxFavorite.setChecked(true);
            } else {
                checkBoxFavorite.setChecked(false);
            }
            checkBoxFavorite.setOnClickListener(new OnCheckedFavorite(idItem, checkBoxFavorite));
        }
    }


    @Override
    public void onBackPressed() {
        if (isInternetAvailable(mContext)) {
            ListActivity.synchronizeServer();
            if (getIntent().getBooleanExtra("isToggled", false)) {
                if (isInitiallyInFavorite != checkBoxFavorite.isChecked()) {
                    if (isInitiallyInFavorite) {
                        ListActivity.removeItem(idItem);
                    } else {
                        ListActivity.addItem(idItem);
                    }
                }
            }
        }
        ListActivity.notifyItemAdapter();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        if (isInternetAvailable(mContext)) {
            ListActivity.synchronizeServer();
        }
        super.onStop();
    }
}


