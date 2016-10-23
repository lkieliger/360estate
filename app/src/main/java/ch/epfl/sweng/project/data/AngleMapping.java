package ch.epfl.sweng.project.data;

import ch.epfl.sweng.project.util.Tuple;

class AngleMapping {

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

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        AngleMapping thatAngleMapping = (AngleMapping) obj;
        return getId() == thatAngleMapping.getId() &&
                getUrl().equals(thatAngleMapping.getUrl()) &&
                (Double.compare(getTheta(), thatAngleMapping.getTheta()) == 0) &&
                (Double.compare(getPhi(), thatAngleMapping.getPhi()) == 0);
    }

    @Override
    public int hashCode() {
        int result = mId;
        result = 31 * result + (mUrl != null ? mUrl.hashCode() : 0);
        result = 31 * result + thetaPhi.hashCode();
        return result;
    }
}
