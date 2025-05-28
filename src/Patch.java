import java.util.ArrayList;

public class Patch {
    protected double maxGrainHere;
    protected double grainHere;
    //There may be multiple people on a patch
    protected ArrayList<Person> peopleHere;

    public Patch() {
        //Set up the initial amounts of grain each patch has
        if (Params.randomInt(0, 100) < Params.PERCENT_BEST_LAND) {
            //Give some patches the highest amount of grain possible. these patches are the "best land"
            maxGrainHere = Params.MAX_GRAIN;
            grainHere = maxGrainHere;
        } else {
            maxGrainHere = 0;
            grainHere = 0;
        }
        peopleHere = new ArrayList<>();
    }

    //Harvesting grains by people in the patch
    public void harvest() {
        //Calculate the amount of grain each person harvests
        double avgGrain = grainHere / peopleHere.size();
        for (Person person : peopleHere) {
            person.wealth = Math.floor(person.wealth + avgGrain);
        }
        //Reset the amount of grain
        grainHere = 0;
    }

    //Grow the grain in the patch
    public void growGrain() {
        grainHere = Math.min(maxGrainHere, grainHere + Params.NUM_GRAIN_GROWN);
    }
}
