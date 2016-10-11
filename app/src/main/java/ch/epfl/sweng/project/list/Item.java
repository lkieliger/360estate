package ch.epfl.sweng.project.list;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import ch.epfl.sweng.project.MainActivity;
import ch.epfl.sweng.project.R;

@ParseClassName("Item")
public class Item extends ParseObject {

    public enum HouseType {
        APARTMENT(getString(R.string.apartment)), HOUSE(getString(R.string.house)),
        BUILDING(getString(R.string.building));

        private final String description;

        HouseType(String d) {
            description = d;
        }

        public String getDescription() {
            return description;
        }
    }

    //default constructor necessary for Parse subclass
    public Item() {
    }

    private int price;
    private String location;
    private HouseType type;
    private double rooms;
    private int surface;
    //private final ParseFile img; TODO:add image

    public Item(int price, String location, HouseType type, double rooms, int surface) {
        setPrice(price);
        setLocation(location);
        setType(type);
        setRooms(rooms);
        setSurface(surface);
    }

    private void setPrice(int price) {
        put("price", price);
    }

    private void setLocation(String location) {
        put("location", location);
    }

    private void setType(HouseType type) {
        put("type", type.ordinal());
    }

    private void setRooms(double rooms) {
        put("rooms", rooms);
    }

    private void setSurface(int surface) {
        put("surface", surface);
    }

    public HouseType getType() {
        return HouseType.values()[getInt("type")];
    }

    String getLocation() {
        return getString("location");
    }

    String getRooms() {
        return formatRooms(getDouble("rooms"));
    }

    private int getSurface() {
        return getInt("surface");
    }

    private int getPrice() {
        return getInt("price");
    }

    String printSurface() {
        return formatInts(getSurface(), 0);
    }

    String printPrice() {
        return formatInts(getPrice(), 0);
    }

    private static String getString(int resId) {
        return MainActivity.getContext().getString(resId);
    }

    private String formatRooms(double rooms) {
        if (rooms % 1 < 0.4) return (int) rooms + "";
        else return (int) rooms + "\u00BD";
    }

    private String formatInts(int price, int acc) {
        if (price > 9) {
            return formatInts(price / 10, (acc + 1) % 3) + (acc == 2 ? "'" : "") + price % 10;
        } else return "" + price;
    }
}