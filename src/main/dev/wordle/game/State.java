package main.dev.wordle.game;

import main.dev.wordle.common.Config;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class manages the state of the game.
 * As such, it keeps track of the number of guesses, what the player has discovered about the word
 * and whether the game is over or not.
 */
public class State {

    private final Validator validator;
    private static final Config config;
    private static boolean hintUsed = false;
    private static final String HIDDEN_CHAR = Colours.HIDDEN + "*" + Colours.RESET;
    private static final List<String> view = new ArrayList<String>();
    public static final Set<Character> partiallyDiscoveredChars = new HashSet<Character>();
    public Map<Integer, Character> discoveredChars = new HashMap<Integer, Character>();
    public Set<Character> eliminatedChars = new HashSet<Character>();;
    public List<Character> wordArray;
    public boolean finished = false;
    public boolean correct = false;
    public int guesses = 0;

    static {
        try {
            config = new Config();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Constructs a new state with the given target word.
     * @param input the word to be guessed.
     */
    public State(String input) {
        wordArray = toArray(input);
        validator = new Validator(input);
        initializeView();
    }

    /**
     * The second constructor is primarily used for dependency injection
     * in order to be able to mock the validator for unit testing
     */
    public State(String input, Validator v) {
        wordArray = toArray(input);
        validator = v;
        initializeView();
    }

    /**
     * Shows the visual representation of the word that needs to be guessed.
     * At first, the entire word will be concealed but gradually, as the
     * player guesses more letters, they will be revealed in different
     * colours representing either a full or partial guess. Note that
     * a partial guess means that the letter exists in the word but in a
     * different place
     */
    public void show() {
        System.out.println(String.join("", view));
        System.out.println("Eliminated letters: " + getEliminatedCharsString());
    }

    /**
     * Updating the state. This involves:
     * 1. Checking if the input is valid; if not the state will not be updated
     * 2. incrementing the guess count
     * 3. Updating the view of the word which reveals to the player what they got right
     * @param input - input text from the player
     */
    public void update(String input) {
        String formattedInput = format(input);
        validator.setEliminated(eliminatedChars);
        validator.setDiscoveredLetters(discoveredChars);
        validator.setPartiallyDiscoveredLetters(partiallyDiscoveredChars);
        if (validator.validate(formattedInput)) {
            evaluate(formattedInput);
            guesses++;
            if (guesses == config.GUESSES && !finished) {
                System.out.println("You are out of guesses! The word was: " + getWord());
                finished = true;
            }
        }
    }

    /**
     * Provide a hint to the player by revealing one
     * of the undiscovered letters randomly.
     */
    public void hint() {
        if (hintUsed) {
            System.out.println("You have already used a hint!");
            return;
        }
        if (countHidden() == 1) {
            System.out.println("There is only one letter left to guess!");
            return;
        }
        revealRandomLetter();
    }

    /**
     * Debug method that displays the current state of the game
     */
    public void debug() {
        System.out.println("Current state: ");
        System.out.println("------------------------------");
        System.out.println("Word: " + getWord());
        System.out.println("View: " + view);
        System.out.println("Eliminated: " + getEliminatedCharsString());
        System.out.println("Guesses: " + guesses);
        System.out.println("------------------------------");
    }

    /**
     * Evaluate a guess by comparing it against the target word.
     * Along the way, update the view and the state (what letters have been used and guessed)
     * @param input - the guess made by the player
     */
    private void evaluate(String input) {
        List<Character> inputArray = toArray(input);
        for (int i = 0; i < inputArray.size(); i++) {
            Character correctLetter = wordArray.get(i);
            Character guessedLetter = inputArray.get(i);
            if (guessedLetter == correctLetter) {
                view.set(i, Colours.CORRECT + correctLetter + Colours.RESET);
                discoveredChars.put(i, correctLetter);
            }
            else if ((guessedLetter != correctLetter) & wordArray.contains(guessedLetter)) {
                view.set(i, Colours.PARTIAL + guessedLetter + Colours.RESET);
                partiallyDiscoveredChars.add(guessedLetter);
            }
            else {
                eliminatedChars.add(guessedLetter);
            }
        }
        if (!view.contains(HIDDEN_CHAR)) {
            finished = true;
            correct = true;
            System.out.println("Victory!");
        }
    }

    /**
     * Preprocess input text
     * @param input - raw input text from the player
     * @return - formatted text
     */
    private static String format(String input) {
        return input.toUpperCase();
    }

    /**
     * A utility method that converts a string into an array of characters
     * @param input - the string to be converted
     * @return - an array of characters
     */
    private static List<Character> toArray(String input) {
        return input.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
    }

    /**
     * Get the count of hidden characters remaining in the word
     * @return count of hidden characters
     */
    private int countHidden() {
        int count = 0;
        for (String s : view) {
            if (s.equals(HIDDEN_CHAR)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Reveal a random undiscovered letter
     */
    private void revealRandomLetter() {
        Random random = new Random();
        List<Integer> hiddenIndices = new ArrayList<Integer>();
        for(int i = 0; i < wordArray.size(); i++) {
            if(view.get(i).equals(HIDDEN_CHAR)) {
                hiddenIndices.add(i);
            }
        }
        int randomIndex = random.nextInt(hiddenIndices.size());
        view.set(randomIndex, wordArray.get(randomIndex).toString());
        System.out.println("We have revealed a letter for you");
        hintUsed = true;
        guesses++;
    }

    /**
     * Initialise the view after receiving the word
     */
    private void initializeView() {
        for (int i = 0; i < wordArray.size(); i++) {
            view.add(HIDDEN_CHAR);
        }
    }

    /**
     * Get the string representation of the word that the player is trying to guess
     */
    public String getWord() {
        return String.join("", wordArray.stream().map(Object::toString).toList());
    }

    private String getEliminatedCharsString() {
        return String.join(",", eliminatedChars.stream().map(Object::toString).sorted().toList());
    }

}
