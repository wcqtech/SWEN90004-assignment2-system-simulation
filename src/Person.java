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
        this.vision = randomVersion();
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
        return metabolism * Params.randomInt(0, 50);
    }

    private int randomVersion(){
        return Params.randomInt(0, Params.NUM_VISION);
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
        double[] totalDirection = {0, 0, 0, 0, 0};
        //这里不确定能不能原地不动，逻辑上应该可以
        int[] dx = {0, 0, 0, -1, 1};
        int[] dy = {0, 1, -1, 0, 0};

        //遍历视野范围内的所有格子
        for (int i = 0; i <= vision; i++) {
            for (int j = 0; j < 5; j++) {
                //判断格子是否合法
                if (x + dx[j] * i >= 0 && x + dx[j] * i < Params.MAX_X
                        && y + dy[j] * i >= 0 && y + dy[j] * i < Params.MAX_Y) {
                    //计算每个格子的总方向
                    totalDirection[j] += landscape.patches.get(x + dx[j] * i).get(y + dy[j] * i).grainHere;
                } else {
                    //如果走到的格子不合法，就置成-1
                    totalDirection[j] = -1;
                }
            }
        }

        int maxIndex = 0;
        //找到总方向最大的格子
        for (int i = 1; i < 5; i++) {
            if (totalDirection[i] > totalDirection[maxIndex]) {
                maxIndex = i;
            }
        }

        //从原先的patch上把这个人移除
        landscape.patches.get(x).get(y).peopleHere.remove(this);

        //移动到新的格子
        x += dx[maxIndex];
        y += dy[maxIndex];

        //再放到新的patch上
        landscape.patches.get(x).get(y).peopleHere.add(this);

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
        metabolism = randomMetabolism();
        wealth = randomWealth(metabolism);
        lifeExpectancy = randomLifeExpectancy();
        vision = randomVersion();
    }
}