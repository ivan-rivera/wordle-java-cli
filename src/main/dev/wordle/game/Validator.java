package main.dev.wordle.game;

import main.dev.wordle.common.Vocabulary;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Validator {

    public final List<Character> word;
    private Set<Character> eliminatedChars = new HashSet<Character>();
    private Set<Character> partiallyDiscoveredChars = new HashSet<Character>();
    private Map<Integer, Character> discoveredLetters = new HashMap<Integer, Character>();
    private static final Vocabulary vocabulary;
    static {
        try {
            vocabulary = Vocabulary.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Validator(String input) {
        word = toArray(input);
    }

    public void setEliminated(Set<Character> eliminated) {
        eliminatedChars = eliminated;
    }

    public void setDiscoveredLetters(Map<Integer, Character> discovered) {
        discoveredLetters = discovered;
    }

    public void setPartiallyDiscoveredLetters(Set<Character> partiallyDiscovered) {
        partiallyDiscoveredChars = partiallyDiscovered;
    }

    public boolean validate(String input) {
        if (!containsLettersOnly(input)) {
            System.out.println("Input must contains only letters\n");
            return false;
        }
        if (!isOfExpectedLength(input)) {
            System.out.printf("Input must contain %d letters only\n", word.size());
            return false;
        }
        if (!notUsingEliminatedChars(input)) {
            System.out.println("You have already used these letters\n");
            return false;
        }
        if (!isWord(input)) {
            System.out.println("Input is not a recognised English word\n");
            return false;
        }
        if (!usesDiscoveredLetters(input)) {
            System.out.println("You have to use previously discovered letters\n");
            return false;
        }
        if (!usesPartiallyDiscoveredLetters(input)) {
            System.out.println("You have to use previously partially discovered letters\n");
            return false;
        }
        return true;
    }

    private boolean notUsingEliminatedChars(String input) {
        Set<Character> inputChars = new HashSet<Character>(toArray(input));
        inputChars.retainAll(eliminatedChars);
        return inputChars.isEmpty();
    }

    private boolean isOfExpectedLength(String input) {
        return input.length() == word.size();
    }

    private static boolean containsLettersOnly(String input) {
        return input.matches("[A-Z]+");
    }

    private static List<Character> toArray(String input) {
        return input.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
    }

    private boolean isWord(String input) {
        return vocabulary.contains(input);
    }

    private boolean usesDiscoveredLetters(String input) {
        List<Character> inputArray = toArray(input);
        for (int i = 0; i < inputArray.size(); i++) {
            if (discoveredLetters.containsKey(i)) {
                if (inputArray.get(i) != discoveredLetters.get(i)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean usesPartiallyDiscoveredLetters(String input) {
        HashSet<Character> inputChars = new HashSet<Character>(toArray(input));
        inputChars.retainAll(partiallyDiscoveredChars);
        return inputChars.size() == partiallyDiscoveredChars.size();
    }
}
