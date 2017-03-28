package mvc.model;

/**
 * Created by michaelborisov on 20.02.17.
 */
public class RateObject {
    public String getName() {
        return name;
    }

    private String name;
    private double rate;

    public double getRate() {
        return rate;
    }

    public RateObject(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }
}
