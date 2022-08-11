package test.dev.wordle.game;

import main.dev.wordle.game.Database;
import main.dev.wordle.game.State;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class is used to read and write the history of performance
 */
public class DatabaseTest {

    private static final String WORD = "PILOT";
    private static final String SIMILAR_WORD = "PLANE";
    public static final String TEST_LOCATION = System.getProperty("user.home") + "/.cache/wordle/test.txt";
    private static final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        Whitebox.setInternalState(Database.class, "location", TEST_LOCATION);
    }

    @AfterEach
    void clean() throws IOException {
        Database.drop();
    }

    @Test
    @DisplayName("Test that DB enables us to write and read Record instances")
    void test() throws IOException {

        assertEquals(TEST_LOCATION, Database.location, "DB location should be set to test");

        List<Database.Record> records1 = Database.read();
        assertTrue(records1.isEmpty(), "records should be empty");

        State state = new State(WORD);
        state.update(SIMILAR_WORD);
        state.update(WORD);
        Database.write(state);

        List<Database.Record> records2 = Database.read();
        assertEquals(1, records2.size(), "there should be 2 records in the DB");

        Database.summarize();
        String output = outputStreamCaptor.toString();
        assertTrue(
                output.contains("your stats: 1 games with 100.00% win rate and 2.00 average guesses"),
                "unexpected output"
        );
    }

}
