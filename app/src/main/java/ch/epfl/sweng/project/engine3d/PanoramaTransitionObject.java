package ch.epfl.sweng.project.engine3d;

import org.rajawali3d.primitives.Sphere;

public class PanoramaTransitionObject extends Sphere {

    private final int Id;
    private final String nextUrl;

    public PanoramaTransitionObject(float radius, int segmentsW, int segmentsH, int id, String nextUrl) {
        super(radius, segmentsW, segmentsH);
        Id = id;
        this.nextUrl = nextUrl;
    }

    public PanoramaTransitionObject(float radius, int segmentsW, int segmentsH, boolean mirrorTextureCoords, int id,
                                    String nextUrl) {
        super(radius, segmentsW, segmentsH, mirrorTextureCoords);
        Id = id;
        this.nextUrl = nextUrl;
    }

    public PanoramaTransitionObject(float radius, int segmentsW, int segmentsH, boolean createTextureCoordinates,
                                    boolean createVertexColorBuffer, boolean createVBOs, int id, String nextUrl) {
        super(radius, segmentsW, segmentsH, createTextureCoordinates, createVertexColorBuffer, createVBOs);
        Id = id;
        this.nextUrl = nextUrl;
    }

    public PanoramaTransitionObject(float radius, int segmentsW, int segmentsH, boolean createTextureCoordinates,
                                    boolean createVertexColorBuffer, boolean createVBOs, boolean mirrorTextureCoords,
                                    int id, String nextUrl) {
        super(radius, segmentsW, segmentsH, createTextureCoordinates, createVertexColorBuffer, createVBOs,
                mirrorTextureCoords);
        Id = id;
        this.nextUrl = nextUrl;
    }

    public int getId() {
        return Id;
    }

    public String getNextUrl() {
        return nextUrl;
    }
}
