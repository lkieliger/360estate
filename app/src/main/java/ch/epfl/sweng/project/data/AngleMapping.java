package ch.epfl.sweng.project.data;

import ch.epfl.sweng.project.util.Tuple;

public class AngleMapping{

    private final Tuple<Double, Double> thetaPhi;
    private final int mId;
    private final String mUrl;

    public AngleMapping(Tuple<Double, Double> extThetaPhi, int extId, String extUrl) {
        thetaPhi = extThetaPhi;
        mId = extId;
        mUrl = extUrl;
    }

    public AngleMapping(Double extTheta, Double extPhi, int extId, String extUrl){
        thetaPhi = new Tuple<Double, Double>(extTheta,extPhi);
        mId = extId;
        mUrl = extUrl;
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
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }
}
