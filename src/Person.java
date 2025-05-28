public class Person {
    private static int nextId = 0;

    //这个是用来输出的
    protected int id;
    protected int age;
    protected double wealth;
    protected int lifeExpectancy;
    protected int metabolism;
    protected int vision;

    protected int x;
    protected int y;

    public Person(){
        this.id = nextId++;
        this.metabolism = randomMetabolism();
        this.lifeExpectancy = randomLifeExpectancy();
        this.wealth = randomWealth(this.metabolism);
        this.age = randomLifespan(this.lifeExpectancy);
        this.vision = randomVision();
        this.x = randomX();
        this.y = randomY();
    }

    private int randomMetabolism(){
        return Params.randomInt(1, Params.METABOLISM_MAX);
    }

    private int randomLifeExpectancy(){
        return Params.randomInt(Params.LIFE_EXPECTANCY_MIN, Params.LIFE_EXPECTANCY_MAX);
    }

    private int randomWealth(int metabolism){
        return metabolism + Params.randomInt(0, 50);
    }

    private int randomVision(){
        return Params.randomInt(1, Params.NUM_VISION);
    }

    private int randomLifespan(int lifeExpectancy){
        return Params.randomInt(0, lifeExpectancy);
    }

    private int randomX(){
        return Params.randomInt(0, Params.MAX_X - 1);
    }

    private int randomY(){
        return Params.randomInt(0, Params.MAX_Y - 1);
    }

    public void turnTowardsGrainAndMove(Landscape landscape) {
        //move the turtle to the patch with the most grain
        double maxValue = landscape.patches.get(x).get(y).grainHere;
        int maxValueX = x;
        int maxValueY = y;
        //这里不确定能不能原地不动，逻辑上应该可以
        int[] dx = {0, 0, -1, 1};
        int[] dy = {1, -1, 0, 0};

        //遍历视野范围内的所有格子
        for (int i = 0; i <= vision; i++) {
            for (int j = 0; j < 4; j++) {
                //判断格子是否合法
                if (x + dx[j] * i >= 0 && x + dx[j] * i < Params.MAX_X
                        && y + dy[j] * i >= 0 && y + dy[j] * i < Params.MAX_Y) {
                    //计算每个格子的总方向
                    double targetValue = landscape.patches.get(x + dx[j] * i).get(y + dy[j] * i).grainHere;
                    if (targetValue >= maxValue){
                        maxValue = targetValue;
                        maxValueX = x + dx[j] * i;
                        maxValueY = y + dy[j] * i;
                    }
                }
            }
        }

        //从原先的patch上把这个人移除
        landscape.patches.get(x).get(y).peopleHere.remove(this);

        landscape.patches.get(maxValueX).get(maxValueY).peopleHere.add(this);

    }

    public void eatAgeDie(Landscape landscape) {
        wealth = wealth - metabolism;
        age++;
        if (wealth < 0 || age > lifeExpectancy) {
            //死掉然后放一个后代在这
            produceChild();
        }

    }

    public void produceChild() {
        //实际上是变成一个小孩
        id = nextId++;
        age = 0;
        lifeExpectancy = randomLifeExpectancy();
        metabolism = randomMetabolism();
        if (!Params.INHERIT_WEALTH){
            wealth = randomWealth(metabolism);
        } else {
//            wealth = Math.max(wealth, metabolism);
            //第一种继承方案，无负债开局+随机值
            wealth = randomWealth(metabolism) + Math.max(0, wealth);
            //第二种继承方案，有负债开局+随机值
//            wealth = randomWealth(metabolism) + wealth;
            //第三种继承方案，完全继承
//            wealth = Math.max(wealth, 0);
        }

        if (!Params.INHERIT_VISION){
            vision = randomVision();
        } else {
            vision = Math.max(vision, randomVision());
        }


    }
}