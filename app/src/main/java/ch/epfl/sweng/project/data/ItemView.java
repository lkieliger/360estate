package ch.epfl.sweng.project.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.DataMgmt;
import ch.epfl.sweng.project.user.display.ListActivity;
import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.user.display.OnCheckedFavorite;


public class ItemView extends RelativeLayout {
    private TextView locationSurfaceRooms;
    private TextView priceType;
    private ImageView img;
    private CheckBox checkBox;
    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.children, this, true);
        setupChildren();
    }

    private void setupChildren() {
        locationSurfaceRooms = (TextView) findViewById(R.id.location_surface_rooms);
        locationSurfaceRooms.setBackground(getContext().getDrawable(R.color.colorTrueTransparent));
        priceType = (TextView) findViewById(R.id.price_type);
        priceType.setBackground(getContext().getDrawable(R.color.colorTrueTransparent));
        img = (ImageView) findViewById(R.id.miniature);
        checkBox = (CheckBox) findViewById(R.id.favoriteCheckBox);
    }

    public static ItemView inflate(ViewGroup parent) {
        return (ItemView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
    }

    public void setItem(Item item) {
        // locationSurfaceRooms.setText(item.getLocation() + ", " + item.printSurface() + " m\u00B2, " +
        //       item.getRooms() + " " + getDataForDescription().getString(R.string.rooms));
        locationSurfaceRooms.setText(String.format(
                getResources().getString(R.string.text_location_surface),
                item.getLocation(),
                item.printSurface(),
                item.getRooms(),
                getResources().getString(R.string.rooms)
        ));
        // priceType.setText("" + item.printPrice() + " CHF"+", "+
        //       getDataForDescription().getString(item.getType().getDescription()));
        priceType.setText(String.format(
                getResources().getString(R.string.text_price_type),
                item.printPrice(),
                getResources().getString(R.string.text_currency),
                getResources().getString(item.getType().getDescription())
        ));
        String url = item.getStartingImageUrl();
        if(url == null){
            img.setImageResource(R.drawable.no_image);
        }else {
            DataMgmt.getImgFromUrlIntoView(getContext(),url,img);
        }
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);


        if(ListActivity.favoriteContainsUrl(item.getId())){
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }

        checkBox.setButtonDrawable(ContextCompat.getDrawable(getContext(), R.drawable.star_selector));
        checkBox.setOnClickListener(new OnCheckedFavorite(item.getId(),checkBox));
    }
}
