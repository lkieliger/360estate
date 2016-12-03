package ch.epfl.sweng.project.features.propertylist.filter;

import android.support.compat.BuildConfig;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import ch.epfl.sweng.project.data.parse.objects.Item;

/**
 * A state of the popup layout.
 */
public final class FilterValues {

    private int typeSpinner;
    private int positionSpinner;
    private String city;
    private String maxRooms;
    private String minRooms;
    private String maxPrice;
    private String minPrice;
    private String maxSurface;
    private String minSurface;

    /**
     * @param typeSpinner     The type entered.
     * @param positionSpinner The position of the selected item in the spinner.
     * @param city            The city entered.
     * @param maxRooms        The max number of rooms entered.
     * @param minRooms        The min number of rooms entered.
     * @param maxPrice        The maximum price entered.
     * @param minPrice        The minimum price entered.
     * @param maxSurface      The maximum Surface entered.
     * @param minSurface      The minimum Surface entered.
     */
    public FilterValues(int typeSpinner, int positionSpinner, String city, String maxRooms, String minRooms,
                        String maxPrice, String minPrice, String maxSurface, String minSurface) {
        this.minSurface = minSurface;
        this.typeSpinner = typeSpinner;
        this.positionSpinner = positionSpinner;
        this.city = city;
        this.maxRooms = maxRooms;
        this.minRooms = minRooms;
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

    public String getMaxRooms() {
        return maxRooms;
    }

    public String getMinRooms() {
        return minRooms;
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
        Boolean isMaxRoomsFiltered = !maxRooms.equals("");
        Boolean isMinRoomsFiltered = !minRooms.equals("");
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

            filterWithMax(isMaxRoomsFiltered, "rooms", maxRooms, query);
            filterWithMin(isMinRoomsFiltered, "rooms", minRooms, query);
            filterWithMax(isMaxPriceFiltered, "price", maxPrice, query);
            filterWithMin(isMinPriceFiltered, "price", minPrice, query);
            filterWithMax(isMaxSurfaceFiltered, "surface", maxSurface, query);
            filterWithMin(isMinSurfaceFiltered, "surface", minSurface, query);

        } catch (NumberFormatException e) {
            if (BuildConfig.DEBUG) {
                Log.d("FilterValues", "NumberFormatException" + e.getMessage());
            }
        }
        return query;
    }

    private <T extends ParseObject> void filterWithMax(boolean isFiltered, String paramToFilter,
                                                       String paramGet, ParseQuery<T> query) {
        if (isFiltered) {
            int temp = Integer.parseInt(paramGet);
            query.whereLessThanOrEqualTo(paramToFilter, temp);
        }
    }

    private <T extends ParseObject> void filterWithMin(boolean isFiltered, String paramToFilter,
                                                       String paramGet, ParseQuery<T> query) {
        if (isFiltered) {
            int temp = Integer.parseInt(paramGet);
            query.whereGreaterThanOrEqualTo(paramToFilter, temp);
        }
    }

}
