package ch.epfl.sweng.project.engine3d;


import org.rajawali3d.util.ObjectColorPicker;

/**
 * This interface should be implemented by all objects that need to be displayed inside the panorama scene
 * Failing to do so will produce errors as the renderer expects to be able to cast selected objects on the
 * screen to PanoramaComponents
 */
interface PanoramaComponent {

    void unregisterComponentFromPicker(ObjectColorPicker p);

    void registerComponentAtPicker(ObjectColorPicker p);

    /**
     * Detach the caller from its parent and properly destroy it
     */
    void detachFromParentAndDie();
}
