package ch.epfl.sweng.project.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ch.epfl.sweng.project.R;

/**
 * Isaac Leimgruber
 * SCIPER 236908
 */

public class ItemView extends RelativeLayout {
    private TextView locationSurfaceRooms;
    private TextView priceType;
    private ImageView img;
    //private ImageView mImageView;

    public ItemView(Context context, AttributeSet attrs/*, int defStyle*/) {
        super(context, attrs/*, defStyle*/);
        LayoutInflater.from(context).inflate(R.layout.children, this, true);
        setupChildren();
    }

    private void setupChildren() {
        locationSurfaceRooms = (TextView) findViewById(R.id.location_surface_rooms);
        priceType = (TextView) findViewById(R.id.price_type);
        img = (ImageView) findViewById(R.id.miniature);
        //mImageView = (ImageView) findViewById(R.id.item_imageView);TODO:add img
    }

    public static ItemView inflate(ViewGroup parent) {
        return (ItemView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
    }

    public void setItem(Item item) {
        locationSurfaceRooms.setText(item.getLocation() + ", " + item.printSurface() + " m\u00B2, " +
                item.getRooms() + " " + getContext().getString(R.string.rooms));
        priceType.setText("" + item.printPrice() + " CHF"+", "+item.getType().getDescription());
        img.setImageResource(R.mipmap.chaton);
    }
}