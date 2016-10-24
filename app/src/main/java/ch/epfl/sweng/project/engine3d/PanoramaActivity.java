package ch.epfl.sweng.project.engine3d;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import org.rajawali3d.view.ISurface;
import org.rajawali3d.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.AngleMapping;
import ch.epfl.sweng.project.data.HouseManager;

@SuppressWarnings("FieldCanBeLocal")
public class PanoramaActivity extends Activity {

    private SurfaceView mSurface = null;
    private PanoramaRenderer mRenderer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSurface = new SurfaceView(this);
        mSurface.setFrameRate(60.0);
        mSurface.setRenderMode(ISurface.RENDERMODE_WHEN_DIRTY);

        // Add mSurface to root view
        addContentView(mSurface, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));



        //Example of Pano
        String url1 = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/360%C2%B0_Panorama_Obermarkt_G%C3%B" +
                "6rlitz.jpg/1024px-360%C2%B0_Panorama_Obermarkt_G%C3%B6rlitz.jpg";
        String url2 = "https://upload.wikimedia" +
                ".org/wikipedia/commons/thumb/c/c5/360%C2%B0_Panorama_Bahnhof_G%C3%B6rlitz.jpg/1024px-360%C2%B0_P" +
                "anorama_Bahnhof_G%C3%B6rlitz.jpg";
        String url3 = ("https://upload.wikimedia" +
                ".org/wikipedia/commons/thumb/4/40/The_Facade_of_Birla_Auditorium%2C_A_360_Panorama" +
                "-interactive_100_Pix_HDR-20130301.JPG/1024px-The_Facade_of_Birla_Auditorium%2C_A_360_Panor" +
                "ama-interactive_100_Pix_HDR-20130301.JPG");
        String url4 = ("https://upload.wikimedia.org/wikipedia/commons/thumb/7/7b/Panorama_Notre-Dame_de_Paris" +
                ".jpg/1024px-Panorama_Notre-Dame_de_Paris.jpg");
        String url5 = ("https://upload.wikimedia" +
                ".org/wikipedia/commons/thumb/f/fe/Nishinomiya-shi_Kitayama_tree_planting_botanical_garden_-_Japan" +
                "_-_Nikon_1_V1_%2B_FC-E9_equirectangular_panorama_360%C2%B0x180%C2%B0_%286475787765%29.jpg/1024px-" +
                "Nishinomiya-shi_Kitayama_tree_planting_botanical_garden_-_Japan_-_Nikon_1_V1_%2B_FC-E9_equir" +
                "ectangular_panorama_360%C2%B0x180%C2%B0_%286475787765%29.jpg");


        AngleMapping angleMapping1 = new AngleMapping(0.0,1.5,1,url1);
        AngleMapping angleMapping2 = new AngleMapping(1.5,1.5,2,url2);
        AngleMapping angleMapping3 = new AngleMapping(3.0,1.5,3,url3);
        AngleMapping angleMapping4 = new AngleMapping(4.5,1.5,5,url5);

        AngleMapping angleMapping5 = new AngleMapping(0.0,1.5,4,url4);
        AngleMapping angleMapping6 = new AngleMapping(1.5,1.5,4,url4);
        AngleMapping angleMapping7 = new AngleMapping(3.0,1.5,4,url4);
        AngleMapping angleMapping8 = new AngleMapping(4.5,1.5,4,url4);


        List<AngleMapping> list4 = new ArrayList<>();
        list4.add(angleMapping1);
        list4.add(angleMapping2);
        list4.add(angleMapping3);
        list4.add(angleMapping4);

        List<AngleMapping> list2 = new ArrayList<>();
        list2.add(angleMapping5);


        List<AngleMapping> list3 = new ArrayList<>();
        list3.add(angleMapping6);

        List<AngleMapping> list1 = new ArrayList<>();
        list1.add(angleMapping7);

        List<AngleMapping> list5 = new ArrayList<>();
        list5.add(angleMapping8);

        SparseArray<List<AngleMapping>> sparseArray = new SparseArray<>();

        sparseArray.append(4, list4);
        sparseArray.append(2, list2);
        sparseArray.append(3, list3);
        sparseArray.append(1, list1);
        sparseArray.append(5, list5);


        HouseManager houseManager = new HouseManager(sparseArray,4,url4);


        mRenderer = new PanoramaRenderer(this, getWindowManager().getDefaultDisplay(),houseManager);
        mSurface.setSurfaceRenderer(mRenderer);

        //Create listener for handling user inputs
        View.OnTouchListener listener = new PanoramaTouchListener(mRenderer);
        mSurface.setOnTouchListener(listener);



    }


    public PanoramaRenderer getAssociatedRenderer() {
        return mRenderer;
    }
}
