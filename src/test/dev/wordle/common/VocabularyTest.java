package test.dev.wordle.common;

import main.dev.wordle.common.Vocabulary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VocabularyTest {

    private static final Vocabulary vocabulary;
    private static final String WORD = "PILOT";

    static {
        try {
            vocabulary = Vocabulary.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test core functionality of the vocabulary class")
    void test() {
        vocabulary.filter(WORD.length());
        String sampled = vocabulary.sample();
        assertTrue(vocabulary.contains(WORD), "Vocabulary should contain " + WORD);
        assertEquals(sampled.length(), WORD.length(), "unexpected length of sampled word");
    }
}
