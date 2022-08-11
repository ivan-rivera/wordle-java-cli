package test.dev.wordle.game;

import main.dev.wordle.common.Config;
import main.dev.wordle.game.Colours;
import main.dev.wordle.game.State;
import main.dev.wordle.game.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class StateTest {

    private static final String WORD = "PILOT";
    private static final String SIMILAR_WORD = "PICOT";
    private static final String GUESS_WITH_FULL_AND_PARTIAL_MATCH = "PLACE";
    private static final String NON_WORD = "PAAAA";
    private static final Config config;
    private static final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private static State state;

    @Spy
    private static Validator spiedValidator = new Validator(WORD);

    static {
        try {
            config = new Config();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        state = new State(WORD);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    /**
     * Count the number of hidden characters in the printed output
     * @return count of hidden characters
     */
    private long countPrintedHidden() {
        return outputStreamCaptor.toString().chars().filter(c -> c == '*').count();
    }

    /**
     * Ensure that given an empty state, the show method does not reveal any letters
     */
    @Test
    @DisplayName("Test show method with all letters hidden")
    void testShowHidden() {
        state.show();
        assertEquals(countPrintedHidden(), WORD.length(), "unexpected number of hidden characters");
    }

    /**
     * Ensure that guessed or partially guessed letters are displayed
     */
    @Test
    @DisplayName("Test show method with some letters revealed")
    void testShowSomeGuessed() {
        state.update(GUESS_WITH_FULL_AND_PARTIAL_MATCH);
        state.show();
        String expectedStart =
                Colours.CORRECT + "P" + Colours.RESET +
                Colours.PARTIAL + "L" + Colours.RESET;
        String printed = outputStreamCaptor.toString();
        assertTrue(printed.startsWith(expectedStart), "does not start with expected string");
        assertTrue(printed.contains("Eliminated letters: A,C,E"), "does not show eliminated letters");
    }

    /**
     * Ensure that the state switches to finished state after reaching guess limit
     *
     * Note that we need to modify the validator method in order to not get caught
     * in validation errors (which get tested elsewhere). Technically, we could
     * come up with a set of words that would pass validation while failing the
     * game (as desired) but this is not robust because the guess count may be
     * changed in the settings which would require us to update the test later
     */
    @Test
    @DisplayName("Test update method with exceeding guess count")
    void testUpdateExceedGuessCount() {
        State spiedState = new State(WORD, spiedValidator);
        doReturn(true).when(spiedValidator).validate(anyString());
        for (int i = 0; i < config.GUESSES; i++) {
            spiedState.update(GUESS_WITH_FULL_AND_PARTIAL_MATCH);
        }
        assertTrue(spiedState.finished, "state should be finished");
        assertFalse(spiedState.correct, "state should not be correct");
    }

    /**
     * Ensure that invalid input does not update the state (guess count, used chars)
     */
    @Test
    @DisplayName("Test update method with invalid input")
    void testUpdateInvalidInput() {
        state.update(NON_WORD);
        assertFalse(state.finished, "should not be finished");
        assertEquals(0, state.guesses, "guesses should not get incremented");
        assertTrue(state.eliminatedChars.isEmpty(), "eliminated chars should not get updated");
        assertTrue(state.discoveredChars.isEmpty(), "discovered characters should not count");
    }

    /**
     * Ensure that a correct guess switches the state to finished and correct to true
     */
    @Test
    @DisplayName("Test update method with correct guess")
    void testUpdateGuessCorrectly() {
        state.update(WORD);
        assertTrue(state.finished, "state should be finished");
        assertTrue(state.correct, "state should be correct");
    }

    /**
     * Ensure that the hint method reveals a new letter
     */
    @Test
    @DisplayName("Test that hint method reveals a new letter")
    void testHintRevealsLetter() {
        state.hint();
        state.show();
        assertEquals(countPrintedHidden(), WORD.length() - 1, "one of the letters should be revealed");
    }

    /**
     * Ensure that the hint method does not reveal any letters when there is only one remaining
     */
    @Test
    @DisplayName("Test hint method when only one unrevealed letter remains")
    void testHintOneLetterRemains() {
        state.update(SIMILAR_WORD);
        state.hint();
        state.show();
        assertEquals(countPrintedHidden(), 1, "one letter should remain hidden");
        assertEquals(state.guesses, 1, "only one guess should be used up");
        assertFalse(state.finished, "the state should not be finished yet");
    }

    /**
     * Ensure that the hint method can only be called once
     */
    @Test
    @DisplayName("Test hint method when called twice")
    void testHintTwice() {
        state.hint();
        state.hint();
        state.show();
        assertEquals(countPrintedHidden(), WORD.length() - 1, "one of the letters should be revealed");
    }
}
