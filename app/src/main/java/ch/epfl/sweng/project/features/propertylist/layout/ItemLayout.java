package ch.epfl.sweng.project.features.propertylist.layout;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.R;
import ch.epfl.sweng.project.data.ImageMgmt;
import ch.epfl.sweng.project.data.parse.objects.Item;
import ch.epfl.sweng.project.features.propertylist.ListActivity;
import ch.epfl.sweng.project.features.propertylist.listeners.OnCheckedFavorite;


public final class ItemLayout extends RelativeLayout {
    private TextView locationSurfaceRooms;
    private TextView priceType;
    private ImageView img;
    private CheckBox checkBox;

    public ItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.children, this, true);
        setupChildren();
    }

    public static ItemLayout inflate(ViewGroup parent) {
        return (ItemLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
    }

    private void setupChildren() {
        locationSurfaceRooms = (TextView) findViewById(R.id.location_surface_rooms);
        locationSurfaceRooms.setBackground(getContext().getDrawable(R.color.colorTrueTransparent));
        priceType = (TextView) findViewById(R.id.price_type);
        priceType.setBackground(getContext().getDrawable(R.color.colorTrueTransparent));
        img = (ImageView) findViewById(R.id.miniature);
        checkBox = (CheckBox) findViewById(R.id.favoriteCheckBox);
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
            ImageMgmt.getImgFromUrlIntoView(getContext(),url,img);
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
