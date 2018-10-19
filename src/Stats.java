public class Stats {
    private String name;
    private double numberMean;
    private double numberStandardDeviation;
    private double speedMean;
    private double speedStandardDeviation;

    public Stats(String a, double b1, double b2, double c1, double c2) {
        name = a;
        numberMean = b1;
        numberStandardDeviation = b2;
        speedMean = c1;
        speedStandardDeviation = c2;
    }

    @Override
    public String toString() {
        return String.format("Vehicle name: %s, numberMean: %.2f, numberStandardDeviation: %.2f, speedMean: %.2f, speedStandardDeviation: %.2f", name, numberMean, numberStandardDeviation, speedMean, speedStandardDeviation);
    }
}
