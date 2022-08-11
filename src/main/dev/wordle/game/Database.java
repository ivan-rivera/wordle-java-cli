package main.dev.wordle.game;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Database {

    /**
     * Record data structure
     */
    public static class Record {
        @CsvBindByPosition(position = 0)
        private String word;
        @CsvBindByPosition(position = 1)
        private boolean victory;
        @CsvBindByPosition(position = 2)
        private int guesses;
    }

    public static String location = System.getProperty("user.home") + "/.cache/wordle/history.txt";

    private static Path getConnection() throws IOException {
        Path path = Paths.get(location);
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
        return path;
    }

    /**
     * Write state to the database
     */
    public static void write(State state) throws IOException {
        Path connection = getConnection();
        String record = createRecordString(state);
        Files.writeString(connection, record + System.lineSeparator(), StandardOpenOption.APPEND);
    }

    /**
     * Read a list of records from the database.
     */
    public static List<Record> read() throws IOException {
        getConnection();
        return new CsvToBeanBuilder<Record>(new FileReader(location))
                .withType(Record.class)
                .withSeparator(',')
                .build()
                .parse();
    }

    /**
     * Create a record from the state
     * @throws IOException - if the file could not be read
     */
    public static void summarize() throws IOException {
        List<Record> records = read();
        String result = String.format(
                "your stats: %d games with %.2f%% win rate and %.2f average guesses\n",
                records.size(),
                getWinRate(records),
                getAverageGuesses(records)
        );
        System.out.println(result);
    }

    /**
     * Build a record that will be saved into the database
     */
    private static String createRecordString(State state) {
        return String.format("%s,%s,%d", state.getWord(), state.correct, state.guesses);
    }

    /**
     * Calculate the win rate of the game
     */
    private static double getWinRate(List<Record> records) {
        int wins = 0;
        for (Record record : records) {
            if (record.victory) {
                wins++;
            }
        }
        return 100.0 * wins / records.size();
    }

    /**
     * Calculate the average number of guesses per game
     */
    private static double getAverageGuesses(List<Record> records) {
        int total = 0;
        for (Record record : records) {
            total += record.guesses;
        }
        return 1.0 * total / records.size();
    }

    /**
     * Delete table if exists
     * @throws IOException - when cannot access the file
     */
    public static void drop() throws IOException {
        Path path = Paths.get(location);
        Files.deleteIfExists(path);
    }

}
