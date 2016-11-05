package ch.epfl.sweng.project.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import ch.epfl.sweng.project.R;

@ParseClassName("Item")
public class Item extends ParseObject {

    private static final double halfRoom = 0.5;

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

    private void setId(String id){ put("id", id);}

    public HouseType getType() {
        return HouseType.values()[getInt("type")];
    }

    public String getId(){ return getString("id");}

    String getLocation() {
        return getString("location");
    }

    String getRooms() {
        return formatRooms(getDouble("rooms"));
    }

    public int getSurface() {
        return getInt("surface");
    }

    public int getPrice() {
        return getInt("price");
    }

    String printSurface() {
        return formatInts(getSurface(), 0);
    }

    String printPrice() {
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
}