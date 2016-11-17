package ch.epfl.sweng.project.slider;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ch.epfl.sweng.project.R;

import static ch.epfl.sweng.project.DataMgmt.getImgFromUrlIntoView;

public class SlideFragment extends Fragment {
    private String url;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide, container, false
        );
        Bundle b = getArguments();
        String url = b.getString("url");
        ImageView imgV = (ImageView) root.findViewById(R.id.displayed_image);


        getImgFromUrlIntoView(getActivity(), url, imgV);
        return root;
    }
}
