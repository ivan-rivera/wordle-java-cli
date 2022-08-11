package main.dev.wordle.solver;

import main.dev.wordle.common.Config;
import main.dev.wordle.common.Vocabulary;
import picocli.CommandLine;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

@CommandLine.Command(
        name = "solver",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = """
                Given a partial word, this program will give you suggestions for the full word
                Example:
                Let us suppose the word is "PILOT" and you've tried "PLACE", so you've eliminated
                letters A, C, E, you've discovered that P is in the first place and that L is not
                in the 2nd or 1st place but is somewhere in the word. Here is how you'd construct
                your request:
                Example: java -jar wordle.jar solver -w "Pl***" -e "ACE"
                Capital letters tell us about the "green" or fully discovered letters, lowercase
                letter tell us about partially discovered letters (yellow) and asterisk tells us
                about what has not been discovered yet. -e should contain all the eliminated letters
                and the order does not matter
                """
)
public class Solver implements Runnable {

    @CommandLine.Option(
            names = {"-w", "--word"},
            description = "Discovered letters; caps=green, lower=yellow, asterisk=undiscovered"
    )
    private static String word;

    @CommandLine.Option(
            names={"-e", "--eliminated"},
            description="Eliminated letters (in any order, no separators)"
    )
    private static String eliminated;
    private static final Vocabulary vocabulary;
    private static final Config config;

    static {
        try {
            vocabulary = Vocabulary.getInstance();
            config = new Config();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Solver()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println("\nSolving...\n");
        String eliminatedFmt = eliminated.toUpperCase();
        if (!Validator.validate(word, eliminatedFmt)) {
           System.out.println("Invalid input\n");
           return;
        }
        showCandidates(word, eliminatedFmt);
    }

    /**
     * Display candidate words that match the pattern
     */
    private static void showCandidates(String word, String eliminated) {
        System.out.println("\nCandidates:\n");
        vocabulary.filter(word.length());
        vocabulary
                .words
                .stream()
                .filter(candidate -> !containsEliminated(candidate, eliminated))
                .filter(candidate -> matchesDiscovered(candidate, word))
                .filter(candidate -> matchesPartiallyDiscovered(candidate, word))
                .limit(config.DISPLAY_SOLUTIONS)
                .forEach(System.out::println);
    }

    /**
     * Check if the word contains one of eliminated strings
     */
    private static boolean containsEliminated(String word, String eliminated) {
        String rx = String.format(".*[%s].*", eliminated.toUpperCase());
        return word.toUpperCase().matches(rx);
    }

    /**
     * Check is the word contains discovered letters
     */
    private static boolean matchesDiscovered(String candidate, String word) {
        String fullMatchRx = word.replaceAll("[^A-Z]", ".").toLowerCase();
        return candidate.matches(fullMatchRx);
    }

    /**
     * Check for partial matches. We want to make sure that the word contains these letters but not in
     * the same index where the previously discovered letters already are nor in the eliminated places
     * where the partial discovery was observed.
     */
    private static boolean matchesPartiallyDiscovered(String candidate, String word) {
        String lowerWord = candidate.toLowerCase();
        Map<Character, Set<Integer>> possibilities = new HashMap<Character, Set<Integer>>();
        Set<Integer> discovered = new HashSet<Integer>();
        Set<Integer> availableIndices = new HashSet<Integer>();

        Map<Integer, Character> map = IntStream.range(0, word.length()).boxed().collect(toMap(i -> i, word::charAt));

        for (Map.Entry<Integer, Character> entry : map.entrySet()) {
            if (Character.isUpperCase(entry.getValue())) {
                discovered.add(entry.getKey());
            }
        }

        for (int i = 0; i < word.length(); i++) {
            if (!discovered.contains(i)) {
                availableIndices.add(i);
            }
        }

        for (Map.Entry<Integer, Character> entry : map.entrySet()) {
            if (Character.isLowerCase(entry.getValue())) {
                Set<Integer> availableWithoutThis = new HashSet<Integer>(availableIndices);
                availableWithoutThis.remove(entry.getKey());
                possibilities.put(entry.getValue(), availableWithoutThis);
                if (!lowerWord.contains(entry.getValue().toString())) {
                    return false;
                }
            }
        }

        for (int i = 0; i < candidate.length(); i++) {
            if (possibilities.containsKey(lowerWord.charAt(i))) {
                if (!possibilities.get(lowerWord.charAt(i)).contains(i)) {
                    return false;
                }
            }
        }
        return true;
    }

}
