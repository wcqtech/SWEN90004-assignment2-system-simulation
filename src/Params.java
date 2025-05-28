import java.util.Random;

public class Params {
    public static final int NUM_PEOPLE = 250;
    public static final int NUM_VISION = 5;
    public static final int METABOLISM_MAX = 15;
    public static final int LIFE_EXPECTANCY_MIN = 1;
    public static final int LIFE_EXPECTANCY_MAX = 83;
    public static final int PERCENT_BEST_LAND = 10;//val%
    public static final int GRAIN_GROWTH_INTER = 2;
    public static final int NUM_GRAIN_GROWN = 4;

    public static final int MAX_X = 50;
    public static final int MAX_Y = 50;

    public static final int MAX_GRAIN = 50;

    //The number of times the program runs
    public static final int TIME = 300;

    //Used to configure whether the children born after the death of a person will inherit the wealth of their parents
    public static final boolean INHERIT_WEALTH = true;
    //Inheriting wealth types
    public static final int INHERIT_WEALTH_TYPE = 3;

    static int randomInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
