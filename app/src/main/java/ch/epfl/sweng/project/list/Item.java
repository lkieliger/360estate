package ch.epfl.sweng.project.list;
// Armor.java

import com.parse.ParseObject;
import com.parse.ParseClassName;

/**
 * Created by Isaac on 30.09.2016.
 */

@ParseClassName("Item")
public class Item extends ParseObject {
    public enum House_type {
        APPART("appartement"), HOUSE("house"), BUILDING("Building");
        private final String description;

        private House_type(String descr) {
            description = descr;
        }

        public String getDescription() {
            return description;
        }

    } //TODO:edit to more precisions

    //default constructor necessary for parse subclass
    public Item() {
    }


    //TODO: Call ParseObject.registerSubclass(YourClass.class)
    //TODO: in your Application constructor before calling Parse.initialize().
    private int price;
    private String location;
    private House_type type;
    private double rooms;
    private int surface;
    //private final ParseFile img; TODO:add image

    public Item(int price, String location, House_type type, double rooms, int surface) {
        this.price = price;
        this.location = location;
        this.type = type;
        this.rooms = rooms;
        this.surface = surface;

    }

    public void setPrice(int price) {
        put("price", price);
    }

    public void setLocation(String location) {
        put("location", location);
    }

    public void setType(House_type type) {
        put("type", type);
    }

    public void setRooms(double rooms) {
        put("rooms", rooms);
    }

    public void setSurface(int surface) {
        put("surface", surface);
    }

    public void setForTest() {
        setPrice(1050000);
        setLocation("Lausanne VD");
        setRooms(4.5);
        setSurface(200);
        //setType(House_type.House);
    }

    private String formatPrice(int price, int acc) {
        if (price > 9) {
            return formatPrice(price / 10, (acc + 1) % 3) + (acc == 2 ? "'" : "") + price % 10;
        } else return "" + price;
    }

    public Object getType() {
        return get("type");
    }

    public String getLocation() {
        return getString("location");
    }

    public Double getRooms() {
        return getDouble("rooms");
    }

    public int getSurface() {
        return getInt("surface");
    }

    public String getPrice() {
        return formatPrice(getInt("price"), 0);
    }

}
