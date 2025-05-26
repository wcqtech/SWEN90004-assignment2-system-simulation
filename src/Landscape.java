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
        List<List<Double>> result = new ArrayList<>();
        for (int e = 0; e < Params.EPOCH; e++) {
            //先给每个人都找到要去的地方
            for (Person person : people) {
                person.turnTowardsGrainAndMove(this);
            }
            //然后收获粮食，如果一个patch上存在多人，则平分
            for (ArrayList<Patch> row : patches) {
                for (Patch patch : row) {
                    patch.harvest();
                }
            }
            //每个人新陈代谢年龄增长
            for (Person person : people) {
                person.eatAgeDie(this);
            }
            //每个patch重新长grain
            for (ArrayList<Patch> row : patches) {
                if (grainGrowthClockTicks == 0){
                    for (Patch patch : row) {
                        patch.growGrain();
                    }
                    grainGrowthClockTicks = Params.GRAIN_GROWTH_INTER;
                } else {
                    grainGrowthClockTicks--;
                }
            }
            //统计所有人的财富
            List<Double> epochWealthOfEveryone = people.stream().map(v -> v.wealth).collect(Collectors.toList());
            result.add(epochWealthOfEveryone);
        }
        String filePath = String.format("result/%s%s%s", "wealth-distribution-", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")), ".csv");
        Utils.writeInCSV(filePath, result);
    }

    //设置每个patch的初始谷物数量
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
            //遍历所有 maxGrainHere != 0 的patch，并设置其grainHere = maxGrainHere
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

    private void setupPeople(){
        for (int i = 0; i < Params.NUM_PEOPLE; i++) {
            Person p = new Person();
            people.add(p);
            patches.get(p.x).get(p.y).peopleHere.add(p);
        }
    }

    private void diffuse(Patch patch, double number, ArrayList<Patch> neighbors){
        //计算要分给周patches的份额
        double totalDiffusionGrain = patch.grainHere * number;
        //关于份额如何分配详见https://ccl.northwestern.edu/netlogo/docs/dict/diffuse.html
        //当前patch减去要分出去的份额再加上要保留的份额
        patch.grainHere -= ((double) neighbors.size() / 8) * totalDiffusionGrain;
        //周围的patches加上分出去的份额
        for (Patch neighbor : neighbors) {
            //这里patch的grainHere可能会暂时的超过maxGrainHere，不过别担心后面的逻辑中还会统一处理的
            neighbor.grainHere += totalDiffusionGrain / 8;
        }
    }

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
