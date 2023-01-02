package congard.agh.evolution.simulation;

import com.opencsv.CSVWriter;
import congard.agh.evolution.simulation.world.AbstractWorld;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StatsRecorder implements Closeable {
    private record DayDetails(
        int day,
        int aliveAnimalsCount,
        int plantsCount,
        int freeFieldsCount,
        float avgEnergy,
        float avgLifespan
    ) {
        String[] toArray() {
            return new String[] {
                String.valueOf(day),
                String.valueOf(aliveAnimalsCount), String.valueOf(plantsCount), String.valueOf(freeFieldsCount),
                String.format("%.2f", avgEnergy), String.format("%.2f", avgLifespan)
            };
        }

        static String[] header() {
            return new String[] {
                "day", "aliveAnimalsCount", "plantsCount", "freeFieldsCount", "avgEnergy", "avgLifespan"
            };
        }
    }

    private final AbstractWorld world;
    private final CSVWriter writer;

    public StatsRecorder(File file, AbstractWorld world) throws IOException {
        boolean exists = file.exists();

        writer = new CSVWriter(new FileWriter(file));
        this.world = world;

        if (!exists) {
            writer.writeNext(DayDetails.header());
        }
    }

    public void writeDay() throws IOException {
        writer.writeNext(new DayDetails(
            world.getDay(),
            world.getAliveAnimalsCount(), world.getPlantsCount(), world.getFreeFieldsCount(),
            world.getAvgEnergy(), world.calcAvgLifespan()
        ).toArray());
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}
