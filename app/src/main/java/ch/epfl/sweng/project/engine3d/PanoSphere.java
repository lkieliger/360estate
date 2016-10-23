package ch.epfl.sweng.project.engine3d;

import org.rajawali3d.primitives.Sphere;



public class PanoSphere extends Sphere {

    public void removeAllChild(){
        mChildren.clear();
    }

    public PanoSphere(float radius, int segmentsW, int segmentsH) {
        super(radius, segmentsW, segmentsH);
    }

    public PanoSphere(float radius, int segmentsW, int segmentsH, boolean mirrorTextureCoords) {
        super(radius, segmentsW, segmentsH, mirrorTextureCoords);
    }

    public PanoSphere(float radius, int segmentsW, int segmentsH, boolean createTextureCoordinates,
                      boolean createVertexColorBuffer, boolean createVBOs) {
        super(radius, segmentsW, segmentsH, createTextureCoordinates, createVertexColorBuffer, createVBOs);
    }

    public PanoSphere(float radius, int segmentsW, int segmentsH, boolean createTextureCoordinates,
                      boolean createVertexColorBuffer, boolean createVBOs, boolean mirrorTextureCoords) {
        super(radius, segmentsW, segmentsH, createTextureCoordinates, createVertexColorBuffer,
                createVBOs, mirrorTextureCoords);
    }
}
