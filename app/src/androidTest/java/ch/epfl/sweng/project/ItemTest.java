package ch.epfl.sweng.project;

import com.parse.ParseObject;

import org.junit.Test;

import ch.epfl.sweng.project.list.Item;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class tests the Item object, that represent the house.
 */
public class ItemTest {

    private int price = 750000;
    private String location = "Lausanne";
    private Item.HouseType type = Item.HouseType.APARTMENT;
    private double rooms = 5.5;
    private int surface = 200;

    private Item itemFactoryWithArgument() {
        ParseObject.registerSubclass(Item.class);

        return new Item(price, location, type, rooms, surface);
    }

    @Test
    public void newItemCreationWithArgument() {
        Item item = itemFactoryWithArgument();

        assertThat("invalid price in item", item.getPrice(), is(price));
        assertThat("invalid house type in item", item.getType(), is(type));
    }
}
