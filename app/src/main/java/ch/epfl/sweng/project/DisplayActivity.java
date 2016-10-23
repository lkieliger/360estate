package ch.epfl.sweng.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import static ch.epfl.sweng.project.DataMgmt.getImgFromUrlIntoView;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*//Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        */
        setContentView(R.layout.activity_display);

        //Get ID from intent
        Bundle extras = getIntent().getExtras();
        String url = extras.getString("URL");

        ImageView imgV = (ImageView) findViewById(R.id.displayed_image);
        getImgFromUrlIntoView(DisplayActivity.this, url, imgV);
    }
}
