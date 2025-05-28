public class Person {
    //Generate a unique ID for each person
    private static int nextId = 0;

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
        this.age = randomAge(this.lifeExpectancy);
        this.vision = randomVision();
        this.x = randomX();
        this.y = randomY();
    }

    //Generate random metabolism
    private int randomMetabolism(){
        return Params.randomInt(1, Params.METABOLISM_MAX);
    }

    //Generate random life expectancy
    private int randomLifeExpectancy(){
        return Params.randomInt(Params.LIFE_EXPECTANCY_MIN, Params.LIFE_EXPECTANCY_MAX);
    }

    //Generate random wealth
    private int randomWealth(int metabolism){
        return metabolism + Params.randomInt(0, 50);
    }

    //Generate random vision
    private int randomVision(){
        return Params.randomInt(1, Params.NUM_VISION);
    }

    //Generate random age
    private int randomAge(int lifeExpectancy){
        return Params.randomInt(0, lifeExpectancy);
    }

    private int randomX(){
        return Params.randomInt(0, Params.MAX_X - 1);
    }

    private int randomY(){
        return Params.randomInt(0, Params.MAX_Y - 1);
    }

    //Move the turtle to the patch with the most grains in its field of vision
    public void turnTowardsGrainAndMove(Landscape landscape) {
        //The maximum grain value within the field of vision
        double maxValue = landscape.patches.get(x).get(y).grainHere;
        //The coordinates of the patch with the maximum grain value
        int maxValueX = x;
        int maxValueY = y;

        //The direction of movement
        int[] dx = {0, 0, -1, 1};
        int[] dy = {1, -1, 0, 0};

        //Loop through the vision
        for (int i = 0; i <= vision; i++) {
            //Loop through the four directions
            for (int j = 0; j < 4; j++) {
                //Determine whether the coordinates are legal
                if (x + dx[j] * i >= 0 && x + dx[j] * i < Params.MAX_X
                        && y + dy[j] * i >= 0 && y + dy[j] * i < Params.MAX_Y) {
                    //Get the grain value of the target patch
                    double targetValue = landscape.patches.get(x + dx[j] * i).get(y + dy[j] * i).grainHere;
                    //Update the maximum grain value and coordinates
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

    //Consume wealth and age, and die if wealth is exhausted or age exceeds life expectancy
    public void eatAgeDie(Landscape landscape) {
        wealth = wealth - metabolism;
        age++;
        if (wealth < 0 || age > lifeExpectancy) {
            //If a person dies, one child is produced
            produceChild();
        }

    }

    public void produceChild() {
        //Essentially, it resets the properties of the Person object.
        id = nextId++;
        age = 0;
        lifeExpectancy = randomLifeExpectancy();
        metabolism = randomMetabolism();
        vision = randomVision();
        if (!Params.INHERIT_WEALTH){
            //If do not inherit parents' wealth, set a random value
            wealth = randomWealth(metabolism);
        } else {
            switch (Params.INHERIT_WEALTH_TYPE){
                case 1:
                    //The first inheritance scheme is to inherit the parents' wealth and generate random value.
                    //Note that in this case, if the parents' wealth is negative, it will not be inherited.
                    wealth = randomWealth(metabolism) + Math.max(0, wealth);
                    break;
                case 2:
                    //The second inheritance scheme is to inherit the wealth of the parents and generate random value.
                    //Note that in this case, if the parents' wealth is negative, debt may occur.
                    wealth = randomWealth(metabolism) + wealth;
                case 3:
                    //The third inheritance scheme is to only inherit the wealth of the parents, without generating random values.
                    //This means that if the parents die because they have no wealth, the children will also have no shortage of survival.
                    wealth = Math.max(wealth, 0);
                    break;
                default:
                    throw new RuntimeException("ERROR INHERIT WEALTH TYPE");
            }
        }
    }
}