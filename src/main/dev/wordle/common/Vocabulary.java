package main.dev.wordle.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * A class for handling the vocabulary.
 */
public class Vocabulary {

    private static Vocabulary INSTANCE;

    public List<String> words;

    private Vocabulary() throws IOException {
        this.words = load();
    }

    /**
     * Vocabulary singleton
     */
    public static Vocabulary getInstance() throws IOException {
        if (INSTANCE == null) { INSTANCE = new Vocabulary(); }
        return INSTANCE;
    }

    /**
     * Loads a list of words from the English dictionary.
     * The file contains roughly ~500K words.
     *
     * @return A list of words.
     * @throws IOException If the file cannot be read.
     */
    private static List<String> load() throws IOException {
        return Files.readAllLines(Paths.get("resources/words.txt"), StandardCharsets.UTF_8);
    }

    /**
     * Filters a list of words to retain only those that match a required length
     * @param length The required length.
     */
    public void filter(int length) {
        this.words = words.stream().filter(word -> word.length() == length).toList();
    }

    /**
     * Returns whether a word is contained in a dictionary
     * @return true if the word appears in the dictionary, false otherwise
     */
    public boolean contains(String word) {
        return words.contains(word.toLowerCase());
    }

    /**
     * Sample a random word from the vocabulary.
     * @return A random word.
     */
    public String sample() {
        return words.get((int) (Math.random() * this.words.size())).toUpperCase();
    }

}
