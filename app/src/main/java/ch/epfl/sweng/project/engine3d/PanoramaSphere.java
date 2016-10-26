package ch.epfl.sweng.project.engine3d;

import org.rajawali3d.primitives.Sphere;


/**
 * Represent the panoramic Spere containing the image.
 */
class PanoramaSphere extends Sphere {

    void removeAllChild(){
        mChildren.clear();
    }

    PanoramaSphere(float radius, int segmentsW, int segmentsH) {
        super(radius, segmentsW, segmentsH);
    }

    public PanoramaSphere(float radius, int segmentsW, int segmentsH, boolean mirrorTextureCoords) {
        super(radius, segmentsW, segmentsH, mirrorTextureCoords);
    }

    public PanoramaSphere(float radius, int segmentsW, int segmentsH, boolean createTextureCoordinates,
                          boolean createVertexColorBuffer, boolean createVBOs) {
        super(radius, segmentsW, segmentsH, createTextureCoordinates, createVertexColorBuffer, createVBOs);
    }

    public PanoramaSphere(float radius, int segmentsW, int segmentsH, boolean createTextureCoordinates,
                          boolean createVertexColorBuffer, boolean createVBOs, boolean mirrorTextureCoords) {
        super(radius, segmentsW, segmentsH, createTextureCoordinates, createVertexColorBuffer,
                createVBOs, mirrorTextureCoords);
    }
}
