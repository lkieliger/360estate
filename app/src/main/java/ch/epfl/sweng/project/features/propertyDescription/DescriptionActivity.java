package ch.epfl.sweng.project.features.propertyDescription;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.parse.PInterface;
import ch.epfl.sweng.project.data.parse.objects.ClientRequest;
import ch.epfl.sweng.project.engine3d.PanoramaActivity;
import ch.epfl.sweng.project.features.propertyDescription.slider.SlideActivity;
import ch.epfl.sweng.project.features.propertylist.ListActivity;
import ch.epfl.sweng.project.features.propertylist.listeners.OnCheckedFavorite;
import ch.epfl.sweng.project.userSupport.fragments.ContactMeDialogFragment;
import ch.epfl.sweng.project.util.LogHelper;

import static ch.epfl.sweng.project.data.ImageMgmt.getImgFromUrlIntoView;
import static ch.epfl.sweng.project.util.Toaster.shortToast;


public final class DescriptionActivity extends AppCompatActivity {

    public static final int cellSize = 300;
    private boolean isInitiallyInFavorite = false;
    private CheckBox checkBoxFavorite = null;
    private String idItem = null;
    private Context mContext = null;
    private String descriptionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        descriptionTitle = getIntent().getStringExtra(ClientRequest.DESCRIPTION_TAG);
        mContext = getApplicationContext();

        TextView textTitle = (TextView) findViewById(R.id.description_title);
        textTitle.setText(descriptionTitle);

        idItem = getIntent().getStringExtra("idItem");
        final ArrayList<String> imagesURL = new ArrayList<>();
        StringBuilder descriptionBuilder = new StringBuilder();

        PInterface.INST.getDataForDescription(idItem, imagesURL, descriptionBuilder, mContext);
        String description = descriptionBuilder.toString();


        if (!description.equals("")) {


            LogHelper.log("description ", description);

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

                    if (PInterface.INST.getProxy().internetAvailable()) {
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

            checkBoxFavorite.setChecked(isInitiallyInFavorite);
            checkBoxFavorite.setOnClickListener(new OnCheckedFavorite(idItem, checkBoxFavorite));
        }
    }


    /**
     * Display the pop-up that will ask the user if he want to contact the real estate developer.
     */
    public void confirmContactRequest(View v) {
        DialogFragment contactDialog = new ContactMeDialogFragment();
        Bundle additionalInfo = new Bundle();
        additionalInfo.putString(ClientRequest.LOOKFOR_TAG, idItem);
        additionalInfo.putString(ClientRequest.DESCRIPTION_TAG,
                descriptionTitle);

        contactDialog.setArguments(additionalInfo);
        contactDialog.show(getFragmentManager(), ContactMeDialogFragment.TAG);
    }

    @Override
    public void onBackPressed() {
        ListActivity.synchronizeServer();
        if (getIntent().getBooleanExtra("isToggled", false)) {
            if (isInitiallyInFavorite && !checkBoxFavorite.isChecked()) {
                ListActivity.removeItem(idItem);
            }
        }
        ListActivity.notifyItemAdapter();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        if (PInterface.INST.getProxy().internetAvailable()) {
            ListActivity.synchronizeServer();
        }
        super.onStop();
    }
}


