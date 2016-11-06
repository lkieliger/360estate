package ch.epfl.sweng.project.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.BuildConfig;
import ch.epfl.sweng.project.util.Tuple;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)

public class PhotoSphereDataTests {

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void correctEqualsBehavior() {
        List<AngleMapping> neighborsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            neighborsList.add(new AngleMapping(
                    new Tuple<>(0.14d + i / 1000d, 0.10d + i / 1000d),
                    i,
                    i + ".jpg"));
        }

        PhotoSphereData pData = new PhotoSphereData.Builder(14).
                setNeighborsList(neighborsList).
                build();

        PhotoSphereData pData2 = new PhotoSphereData.Builder(14).
                setNeighborsList(neighborsList).
                build();

        PhotoSphereData pData3 = new PhotoSphereData.Builder(15).
                setNeighborsList(neighborsList).
                build();

        PhotoSphereData pData4 = new PhotoSphereData.Builder(14).
                setNeighborsList(neighborsList).
                build();

        PhotoSphereData pData5 = new PhotoSphereData.Builder(14).
                setNeighborsList(neighborsList.subList(0, 5)).
                build();


        List<AngleMapping> neighborsList2 = new ArrayList<>(neighborsList);
        neighborsList2.set(0, new AngleMapping(new Tuple<>(1.4, 2.8), 13, "url"));
        PhotoSphereData pData6 = new PhotoSphereData.Builder(14).
                setNeighborsList(neighborsList2).
                build();

        assertTrue(pData.equals(pData2));

        assertFalse(pData.equals(null));
        assertFalse(pData.equals(neighborsList));
        assertTrue(pData.equals(pData3));
        assertTrue(pData.equals(pData4));
        assertFalse(pData.equals(pData5));
        assertFalse(pData.equals(pData6));
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void correcthashCodeBehavior() {
        List<AngleMapping> neighborsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            neighborsList.add(new AngleMapping(
                    new Tuple<>(0.14d + i / 1000d, 0.10d + i / 1000d),
                    i,
                    i + ".jpg"));
        }

        PhotoSphereData pData = new PhotoSphereData.Builder(14).
                setNeighborsList(neighborsList).
                build();

        PhotoSphereData pData2 = new PhotoSphereData.Builder(14).
                setNeighborsList(neighborsList).
                build();


        assertTrue(pData.hashCode() == pData2.hashCode());



        PhotoSphereData pData4 = new PhotoSphereData.Builder(14).
                build();

        assertFalse(pData.hashCode() == pData4.hashCode());

    }
}
