package ch.epfl.sweng.project.data;

import ch.epfl.sweng.project.util.Tuple;

class AngleMapping{

    private final Tuple<Double, Double> thetaPhi;
    private final int mId;
    private final String mUrl;

    AngleMapping(Tuple<Double, Double> extThetaPhi, int extId, String extUrl) {
        thetaPhi = extThetaPhi;
        mId = extId;
        mUrl = extUrl;
    }


    public Tuple<Double, Double> getThetaPhi() {
        return thetaPhi;
    }

    Double getTheta() {
        return thetaPhi.getX();
    }

    Double getPhi() {
        return thetaPhi.getY();
    }

    public int getId() {
        return mId;
    }

    String getUrl() {
        return mUrl;
    }
}
