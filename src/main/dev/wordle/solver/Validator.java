package main.dev.wordle.solver;

import main.dev.wordle.common.Config;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class Validator {

    private static final Config config;

    static {
        try {
            config = new Config();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean validate(String word, String eliminated) {
        if (!eliminatedContainsLettersOnly(eliminated)) {
            System.out.println("--eliminated must contains only letters\n");
            return false;
        }
        if (!containsUniqueLettersOnly(eliminated)) {
            System.out.println("--eliminated must contains only unique letters\n");
            return false;
        }
        if(!wordContainsLettersOrAsterisksOnly(word)) {
            System.out.println("--word must contains only letters or asterisks\n");
            return false;
        }
        if(!wordContainsExpectedLength(word)) {
            System.out.printf(
                    "--word must contain between %d and %d letters\n",
                    config.MIN_WORD_LENGTH,
                    config.MAX_WORD_LENGTH
            );
            return false;
        }
        if(!eliminatedNotInWord(word, eliminated)) {
            System.out.println("--eliminated must not contain any of the letters in --word\n");
            return false;
        }
        return true;
    }

    private static boolean eliminatedContainsLettersOnly(String input) {
        return input.matches("[A-Z]+");
    }

    private static boolean wordContainsLettersOrAsterisksOnly(String input) {
        return input.matches("[A-z*]+");
    }

    private static boolean containsUniqueLettersOnly(String input) {
        return strToSet(input).size() == input.length();
    }

    private static boolean wordContainsExpectedLength(String input) {
        return input.length() >= config.MIN_WORD_LENGTH && input.length() <= config.MAX_WORD_LENGTH;
    }

    private static boolean eliminatedNotInWord(String word, String eliminated) {
        Set<Character> wordChars = strToSet(word);
        wordChars.retainAll(strToSet(eliminated));
        return wordChars.isEmpty();
    }

    private static Set<Character> strToSet(String input) {
        return input.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
    }


}
