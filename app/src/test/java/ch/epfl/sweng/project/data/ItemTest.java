package ch.epfl.sweng.project.data;

import com.parse.ParseObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ch.epfl.sweng.project.BuildConfig;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class ItemTest {

    private static final String TAG = "Item tests:";

    @Before
    public void setup() {
        ParseObject.registerSubclass(Item.class);
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void resourcesCorrectBehavior() {

        Item item = new Item(141414, "NotFar", Item.HouseType.HOUSE, 3.5, 500, "Away");

        assertEquals(item.getId(), "Away");
    }


}
