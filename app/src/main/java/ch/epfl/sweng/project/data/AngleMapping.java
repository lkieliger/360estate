package ch.epfl.sweng.project.data;

import ch.epfl.sweng.project.util.Tuple;

class AngleMapping{

    private final Tuple<Double, Double> thetaPhi;
    private final int id;
    private final String url;

    AngleMapping(Tuple<Double, Double> extThetaPhi, int extId, String extUrl) {
        this.thetaPhi = extThetaPhi;
        this.id = extId;
        this.url = extUrl;
    }


    public Tuple<Double, Double> getThetaPhi() {
        return thetaPhi;
    }

    public Double getTheta() {
        return thetaPhi.getX();
    }

    public Double getPhi() {
        return thetaPhi.getY();
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
