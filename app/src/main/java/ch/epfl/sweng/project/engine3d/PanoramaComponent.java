package ch.epfl.sweng.project.engine3d;


import org.rajawali3d.util.ObjectColorPicker;

/**
 * This interface should be implemented by all objects that need to be displayed inside the panorama scene
 */
interface PanoramaComponent {
    void unregisterComponent(ObjectColorPicker p);

    void registerComponent(ObjectColorPicker p);
}
