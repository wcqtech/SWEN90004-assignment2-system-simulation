import java.util.ArrayList;

public class Patch {
    //因为要diffuse所以这里必须都是double
    protected double maxGrainHere;
    protected double grainHere;
    protected ArrayList<Person> peopleHere;

    public Patch() {
        if (Params.randomInt(0, 100) < Params.PERCENT_BEST_LAND) {
            maxGrainHere = Params.NUM_GRAIN_GROWN;
            grainHere = Params.NUM_GRAIN_GROWN;
        } else {
            maxGrainHere = 0;
            grainHere = 0;
        }
        peopleHere = new ArrayList<>();
    }

    public void harvest() {
        double avgGrain = grainHere / peopleHere.size();
        for (Person person : peopleHere) {
            person.wealth = Math.floor(person.wealth + avgGrain);
        }
        grainHere = 0;
    }

    public void growGrain() {
        grainHere = Math.min(maxGrainHere, grainHere + Params.NUM_GRAIN_GROWN);
    }
}
