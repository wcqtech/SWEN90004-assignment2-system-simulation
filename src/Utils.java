import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class Utils {
    public static void writeInCSV(String path, List<List<Double>> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            for (List<Double> row : data) {
                StringJoiner sj = new StringJoiner(",");
                for (Object item : row) {
                    sj.add(item.toString());
                }
                writer.write(sj.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
