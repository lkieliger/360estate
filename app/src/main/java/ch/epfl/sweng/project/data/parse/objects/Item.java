package ch.epfl.sweng.project.data.parse.objects;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import ch.epfl.sweng.project.R;

@ParseClassName("Item")
public class Item extends ParseObject {

    private static final double halfRoom = 0.5;

    //default constructor necessary for Parse subclass
    public Item() {
    }

    public Item(int price, String location, HouseType type, double rooms, int surface, String id) {
        setPrice(price);
        setLocation(location);
        setType(type);
        setRooms(rooms);
        setSurface(surface);
        setId(id);
    }

    public HouseType getType() {
        return HouseType.values()[getInt("type")];
    }

    private void setType(HouseType type) {
        put("type", type.ordinal());
    }

    public String getId() {
        return getString("idHouse");
    }

    private void setId(String id) {
        put("idHouse", id);
    }

    public String getStartingImageUrl() {
        return getString("startingImageUrl");
    }

    private void setStartingImageUrl(String url) {
        put("startingImageUrl", url);
    }

    public String getLocation() {
        return getString("location");
    }

    private void setLocation(String location) {
        put("location", location);
    }

    public String getRooms() {
        return formatRooms(getDouble("rooms"));
    }

    private void setRooms(double rooms) {
        put("rooms", rooms);
    }

    public int getSurface() {
        return getInt("surface");
    }

    private void setSurface(int surface) {
        put("surface", surface);
    }

    public int getPrice() {
        return getInt("price");
    }

    private void setPrice(int price) {
        put("price", price);
    }

    public String printSurface() {
        return formatInts(getSurface(), 0);
    }

    public String printPrice() {
        return formatInts(getPrice(), 0);
    }

    private String formatRooms(double rooms) {
        if (rooms % 1 < halfRoom) return (int) rooms + "";
        else return (int) rooms + "\u00BD";
    }

    private String formatInts(int price, int acc) {
        if (price > 9) {
            return formatInts(price / 10, (acc + 1) % 3) + (acc == 2 ? "'" : "") + price % 10;
        } else return "" + price;
    }

    public enum HouseType {
        APARTMENT(R.string.apartment), HOUSE(R.string.house),
        BUILDING(R.string.building);

        private final int description;

        HouseType(int d) {
            description = d;
        }

        public int getDescription() {
            return description;
        }
    }
}