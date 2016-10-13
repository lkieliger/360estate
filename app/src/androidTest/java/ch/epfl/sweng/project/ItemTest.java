package ch.epfl.sweng.project;

import org.junit.Test;

import ch.epfl.sweng.project.list.Item;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Niroshan Vijayarasa on 10.10.16.
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
        Item item = new Item();

        return item;
    }


    private Item itemFactoryWithArgument(){


        Item item = new Item(price,location, type, rooms, surface);

        return item;
    }

    @Test
    public void newItemCreationWithArgument(){
        Item item = itemFactoryWithArgument();



        assertThat("invalid price in item",item.getPrice(),is(price) );
        //assertThat("invalid location in item",item.getLocation(), is(location));
        assertThat("invalid house type in item",item.getType(), is(type));



    }




}
