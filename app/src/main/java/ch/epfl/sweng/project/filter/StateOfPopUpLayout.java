package ch.epfl.sweng.project.filter;

import android.support.compat.BuildConfig;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import ch.epfl.sweng.project.data.Item;

/**
 * A state of the popup layout.
 */
public class StateOfPopUpLayout {

    private int typeSpinner;
    private int positionSpinner;
    private String city;
    private String numberOfRooms;
    private String maxPrice;
    private String minPrice;
    private String maxSurface;
    private String minSurface;

    /**
     * @param typeSpinner        The type entered.
     * @param positionSpinner    The position of the selected item in the spinner.
     * @param city               The city entered.
     * @param numberOfRooms      The number of rooms entered.
     * @param maxPrice           The maximum price entered.
     * @param minPrice           The minimum price entered.
     * @param maxSurface         The maximum Surface entered.
     * @param minSurface         The minimum Surface entered.
     */
    public StateOfPopUpLayout(int typeSpinner, int positionSpinner, String city, String numberOfRooms,
                              String maxPrice, String minPrice, String maxSurface,String minSurface) {
        this.minSurface = minSurface;
        this.typeSpinner = typeSpinner;
        this.positionSpinner = positionSpinner;
        this.city = city;
        this.numberOfRooms = numberOfRooms;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.maxSurface = maxSurface;
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

    public String getMaxPrice() {
        return maxPrice;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public String getMaxSurface() {
        return maxSurface;
    }

    public String getMinSurface() {
        return minSurface;
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
        Boolean isMaxPriceFiltered = !maxPrice.equals("");
        Boolean isMinPriceFiltered = !minPrice.equals("");
        Boolean isMaxSurfaceFiltered = !maxSurface.equals("");
        Boolean isMinSurfaceFiltered = !minSurface.equals("");


        if (isTypeFiltered) {
            query.whereEqualTo("type", typeSpinner - 1);
        }

        if (isCityFiltered) {
            query.whereEqualTo("location", city);
        }

        try {
            if (isNbrOfRoomsFiltered) {
                query.whereEqualTo("rooms", Integer.parseInt(numberOfRooms));
            }

            filterWithMax(isMaxPriceFiltered,"price",maxPrice,query);
            filterWithMin(isMinPriceFiltered,"price",minPrice,query);
            filterWithMax(isMaxSurfaceFiltered,"surface",maxSurface,query);
            filterWithMin(isMinSurfaceFiltered,"surface",minSurface,query);

        } catch (NumberFormatException e) {
            if (BuildConfig.DEBUG) {
                Log.d("StateOfPopUpLayout", "NumberFormatException" + e.getMessage());
            }
        }
        return query;
    }

    private <T extends ParseObject> void filterWithMax(boolean isFiltered, String paramToFilter,
                                                       String paramGet, ParseQuery<T> query){
        if(isFiltered){
            int temp = Integer.parseInt(paramGet);
            query.whereLessThanOrEqualTo(paramToFilter, temp);
        }
    }

    private <T extends ParseObject> void filterWithMin(boolean isFiltered,String paramToFilter,
                                                       String paramGet,ParseQuery<T> query){
        if(isFiltered){
            int temp = Integer.parseInt(paramGet);
            query.whereGreaterThanOrEqualTo(paramToFilter, temp);
        }
    }

}
