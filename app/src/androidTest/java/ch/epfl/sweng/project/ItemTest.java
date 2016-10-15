package ch.epfl.sweng.project;

import com.parse.ParseObject;

import org.junit.Test;

import ch.epfl.sweng.project.list.Item;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @brief This class test the Item object, that represent the house.
 */
public class ItemTest {

    private int price = 750000;
    private String location = "Lausanne";
    private Item.HouseType type = Item.HouseType.APARTMENT;
    private double rooms = 5.5;
    private int surface = 200;

    private Item itemFactoryNoArgument(){
        ParseObject.registerSubclass(Item.class);

        return new Item();
    }


    private Item itemFactoryWithArgument(){
        ParseObject.registerSubclass(Item.class);

        return new Item(price,location, type, rooms, surface);
    }

    @Test
    public void newItemCreationWithArgument(){
        Item item = itemFactoryWithArgument();



        assertThat("invalid price in item",item.getPrice(),is(price) );
        //assertThat("invalid location in item",item.getLocation(), is(location));
        assertThat("invalid house type in item",item.getType(), is(type));



    }




}