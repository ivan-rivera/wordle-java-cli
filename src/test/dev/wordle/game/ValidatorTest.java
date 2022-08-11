package test.dev.wordle.game;

import main.dev.wordle.game.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatorTest {

    private Validator validator;
    private HashSet<Character> usedChars;

    @BeforeEach
    void setUp() {
        validator = new Validator("PILOT");
        usedChars = new HashSet<Character>(Arrays.asList('A', 'B'));
    }

    /**
     * Test basic functionality of the validator
     */
    @Test
    @DisplayName("Check the validator function")
    void testValidateBasic() {
        assertTrue(validator.validate("PILOT"), "valid input not recognised");
        assertFalse(validator.validate("WORD"), "invalid input not detected");
        assertFalse(validator.validate("word!"), "invalid input not detected");
    }

    /**
     * Test whether the validator recognises eliminated characters
     */
    @Test
    @DisplayName("Check the validator function with eliminated chars")
    void testValidateEliminated() {
        validator.setEliminated(usedChars);
        assertTrue(validator.validate("PILOT"), "valid input not recognised");
        assertFalse(validator.validate("ARISE"), "invalid input not detected");
    }

    /**
     * Test whether the validator is able to detect inputs that do not appear in the word dictionary
     */
    @Test
    @DisplayName("Check the validator function with a non-word entry")
    void testValidateNotWord() {
        assertTrue(validator.validate("PILOT"), "valid input not recognised");
        assertFalse(validator.validate("TRWEZ"), "invalid input not detected");
    }

}
