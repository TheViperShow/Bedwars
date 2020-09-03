package me.thevipershow.aussiebedwars.bedwars.objects.spawners.animations;

public class DoubleTriple extends Triple<Double,Double,Double> {

    public DoubleTriple(Double aDouble, Double aDouble2, Double aDouble3) {
        super(aDouble, aDouble2, aDouble3);
    }

    /**
     * Resize the current vector
     *
     * @param value the double value that will multiply every current value
     * @return a new DoubleTriple with the new values
     */
    public DoubleTriple resize(double value) {
        return new DoubleTriple((super.getX() * value), (super.getY() * value), (super.getZ() * value));
    }
}
