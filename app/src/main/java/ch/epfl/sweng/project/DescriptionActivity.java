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

import ch.epfl.sweng.project.ScreenSlide.SlideActivity;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;

import static ch.epfl.sweng.project.DataMgmt.getImgFromUrlIntoView;


public class DescriptionActivity extends AppCompatActivity {

    public static final int cellSize = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        //TODO: get imgUrls list from Parse and put it into imagesUrl
        final ArrayList<String> imagesURL = new ArrayList<>();
        imagesURL.add("http://www.matoucity.fr/wp-content/uploads/2014/08/chaton.jpg");
        imagesURL.add("http://conseils-veto.com/wp-content/uploads/2013/05/chaton-trop-mignon.jpg");
        imagesURL.add("http://www.monchatonetmoi.com/upload/images/portrait-chaton.jpg");
        imagesURL.add("http://img0.gtsstatic.com/wallpapers/0539864a91f53aefe5e9f026b8b2ec1a_large.jpeg");
        imagesURL.add("http://media.koreus.com/201102/chaton-pikachu.jpg");
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
        for(String url : imagesURL){
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
                Intent intent = new Intent(DescriptionActivity.this, PanoramaActivity.class);
                startActivity(intent);
            }
        });

    }
}
