package ch.epfl.sweng.project.filter;

import android.util.Log;

import com.parse.ParseQuery;

import ch.epfl.sweng.project.list.Item;

/**
 * A state of the popup layout.
 */
public class StateOfPopUpLayout {

    private static final double MIN_COEFF = 0.98;
    private static final double MAX_COEFF = 1.02;

    private int typeSpinner;
    private int positionSpinner;
    private String city;
    private String numberOfRooms;
    private String price;
    private String surface;
    private int seekBarPricePosition;
    private int seekBarSurfacePosition;



    /**
     * @param typeSpinner        The type entered.
     * @param positionSpinner    The position of the selected item in the spinner.
     * @param city               The city entered.
     * @param numberOfRooms      The number of rooms entered.
     * @param price              The price entered.
     * @param surface            The surface entered.
     * @param barPricePosition   The position of the seek bar used for the price.
     * @param barSurfacePosition The position of the seek bar used for the surface.
     */
    public StateOfPopUpLayout(int typeSpinner, int positionSpinner, String city, String numberOfRooms,
                              String price, String surface, int barPricePosition, int barSurfacePosition) {
        this.typeSpinner = typeSpinner;
        this.positionSpinner = positionSpinner;
        this.city = city;
        this.numberOfRooms = numberOfRooms;
        this.price = price;
        this.surface = surface;
        seekBarPricePosition = barPricePosition;
        seekBarSurfacePosition = barSurfacePosition;
    }

    public int getPositionSpinner() {
        return positionSpinner;
    }

    public String getCity() {
        return city;
    }

    public String getNumberOfRooms() {
        return numberOfRooms;
    }

    public String getPrice() {
        return price;
    }

    public String getSurface() {
        return surface;
    }

    public int getSeekBarPricePosition() {
        return seekBarPricePosition;
    }

    public int getSeekBarSurfacePosition() {
        return seekBarSurfacePosition;
    }

    /**
     * @return The query obtained by doing the logical AND of every of the condition by the parameters of the
     * current state of the popup layout.
     */
    public ParseQuery<Item> filterQuery() {

        ParseQuery<Item> query = ParseQuery.getQuery("Item");

        Boolean isTypeFiltered = typeSpinner != 0;
        Boolean isCityFiltered = !city.equals("");
        Boolean isNbrOfRoomsFiltered = !numberOfRooms.equals("");
        Boolean isPriceFiltered = !price.equals("");
        Boolean isSurfaceFiltered = !surface.equals("");

        if (isTypeFiltered) {
            try {
                query.whereEqualTo("type", typeSpinner - 1);
            } catch (IllegalArgumentException e) {
                Log.d("StateOfPopUpLayout", "IllegalArgumentException" + e.getMessage());
            }
        }

        if (isCityFiltered) {
            query.whereEqualTo("location", city);
        }

        if (isNbrOfRoomsFiltered) {
            try {
                query.whereEqualTo("rooms", Integer.parseInt(numberOfRooms));
            } catch (NumberFormatException e) {
                Log.d("StateOfPopUpLayout", "NumberFormatException" + e.getMessage());
            }
        }

        if (isPriceFiltered) {
            try {
                int temp = Integer.parseInt(price.split(" ")[0]);
                query.whereLessThanOrEqualTo("price", temp * MAX_COEFF);
            } catch (NumberFormatException e) {
                Log.d("StateOfPopUpLayout", "NumberFormatException" + e.getMessage());
            }
        }

        if (isSurfaceFiltered) {
            try {
                int temp = Integer.parseInt(surface.split(" ")[0]);
                query.whereGreaterThanOrEqualTo("surface", temp * MIN_COEFF);
            } catch (NumberFormatException e) {
                Log.d("StateOfPopUpLayout", "NumberFormatException" + e.getMessage());
            }
        }
        return query;
    }
}
