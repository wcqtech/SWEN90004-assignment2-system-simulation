import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Landscape {
    public ArrayList<ArrayList<Patch>> patches;
    public List<Person> people;
    public int grainGrowthClockTicks;

    public Landscape(){
        patches = new ArrayList<>();
        people = new ArrayList<>();
        grainGrowthClockTicks = Params.GRAIN_GROWTH_INTER;
    }

    public void setup(){
        setupPatches();
        setupPeople();
    }

    public void go(){
        //result stores the data in the Param.TIME row and the NUM_PEOPLE column.
        //data(i,j) represents the wealth of person j at time i.
        List<List<Double>> result = new ArrayList<>();
        for (int t = 0; t < Params.TIME; t++) {
            //Find the best pathch for everyone and move
            for (Person person : people) {
                person.turnTowardsGrainAndMove(this);
            }
            //Harvest the food. If there are multiple people on a patch, divide it equally.
            for (ArrayList<Patch> row : patches) {
                for (Patch patch : row) {
                    patch.harvest();
                }
            }
            //Everyone metabolizes, ages, and potentially dies.
            for (Person person : people) {
                person.eatAgeDie(this);
            }
            //Each patch grows grain, but only if it has been long enough since the last grain growth.
            if (grainGrowthClockTicks == 0){
                for (ArrayList<Patch> row : patches) {
                    for (Patch patch : row) {
                        patch.growGrain();
                    }
                }
            } else {
                grainGrowthClockTicks--;
            }
            //Collect the wealth of everyone and store it in the result array
            List<Double> epochWealthOfEveryone = people.stream().map(v -> v.wealth).collect(Collectors.toList());
            result.add(epochWealthOfEveryone);
        }
        //Write the result to a csv file
        String filePath = String.format("result/%s%s%s", "wealth-distribution-", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")), ".csv");
        Utils.writeInCSV(filePath, result);
    }

    //Set up the initial amounts of grain each patch has
    private void setupPatches(){
        //give some patches the highest amount of grain possible these patches are the "best land"
        for (int i = 0; i < Params.MAX_X; i++) {
            ArrayList<Patch> column = new ArrayList<>();
            for (int j = 0; j < Params.MAX_Y; j++) {
                Patch patch = new Patch();
                column.add(patch);
            }
            patches.add(column);
        }

        //spread that grain around the window a little and put a little back into the patches that are the "best land" found above
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < Params.MAX_X; j++) {
                for (int k = 0; k < Params.MAX_Y; k++) {
                    Patch patch = patches.get(j).get(k);
                    if (patch.maxGrainHere != 0){
                        patch.grainHere = patch.maxGrainHere;
                        diffuse(patch, 0.25, getNeighbors(j, k));
                    }
                }
            }

        }

        //spread the grain around some more
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < Params.MAX_X; j++) {
                for (int k = 0; k < Params.MAX_Y; k++) {
                    diffuse(patches.get(j).get(k), 0.25, getNeighbors(j, k));
                }
            }
        }

        //round grain levels to whole numbers, initial grain level is also maximum
        for (ArrayList<Patch> row : patches) {
            for (Patch patch : row) {
                patch.grainHere = Math.floor(patch.grainHere);
                patch.maxGrainHere = patch.grainHere;
            }
        }
    }

    //set up the initial values for the turtle variables
    private void setupPeople(){
        for (int i = 0; i < Params.NUM_PEOPLE; i++) {
            Person p = new Person();
            people.add(p);
            patches.get(p.x).get(p.y).peopleHere.add(p);
        }
    }

    //Diffuse value from the center to the surrounding
    private void diffuse(Patch patch, double number, ArrayList<Patch> neighbors){
        //Calculate the shares to be given to neighbors
        double totalDiffusionGrain = patch.grainHere * number;
        //Calculate the value of the current patch after diffusion
        patch.grainHere -= ((double) neighbors.size() / 8) * totalDiffusionGrain;
        //Calculate the value of the neighbor after diffusion
        for (Patch neighbor : neighbors) {
            neighbor.grainHere += totalDiffusionGrain / 8;
        }
    }

    //Get the neighbors of a patch
    private ArrayList<Patch> getNeighbors(int patchX, int patchY){
        ArrayList<Patch> neighbors = new ArrayList<>();
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                if (patchX + i >= 0 && patchX + i < Params.MAX_X
                        && patchY + j >= 0 && patchY + j < Params.MAX_Y
                        && !(i == 0 && j == 0)){
                    neighbors.add(patches.get(patchX + i).get(patchY + j));
                }
            }
        }
        return neighbors;
    }

}
