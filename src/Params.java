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

    //迭代的次数netlogo的模型里没有，因为他是要一直跑下去的，这里设置一个，为了让程序结束
    public static final int EPOCH = 300;

    public static final boolean INHERIT_WEALTH = false;
    public static final boolean INHERIT_VISION = false;

    static int randomInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
}
