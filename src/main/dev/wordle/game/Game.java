package main.dev.wordle.game;

import main.dev.wordle.common.Config;
import main.dev.wordle.common.Vocabulary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * The main class for the Wordle game.
 *
 * This class is responsible for facilitating the game.
 * This involves welcoming the player, initializing the word,
 * prompting the player for guesses and orchestrating each round.
 */
public class Game {

    private static final Config config;
    private static final Vocabulary vocabulary;
    private static final Scanner scanner = new Scanner(System.in);

    static {
        try {
            config = new Config();
            vocabulary = Vocabulary.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Main method
     */
    public static void main(String[] args) throws IOException {
        welcome();
        State state = initialize();
        play(state);
    }

    /**
     * Round orchestration. This method is responsible for listening for user input
     * and processing the response.
     * @param state - the current state of the game which contains the history of guesses
     * @throws IOException - if any of the files could not be read
     */
    private static void play(State state) throws IOException {
        System.out.println("\n");
        state.show();
        System.out.println("Enter a guess: \n");
        String input = scanner.nextLine();
        switch (input) {
            case Config.HELP_STRING -> help();
            case Config.QUIT_STRING -> quit();
            case Config.HINT_STRING -> state.hint();
            case Config.DEBUG_STRING -> state.debug();
            default -> state.update(input);
        }
        if(state.finished) {
            debrief(state);
        } else {
            play(state);
        }
    }

    /**
     * Initialisation of the game.
     * This method asks user to select the word length, validates the input and then
     * samples a random word from the vocabulary with the selected length.
     * @return a new state object with a clean slate
     */
    private static State initialize() {
        String prompt = "Choose word length (between %d and %d inclusive): \n";
        System.out.printf(prompt, config.MIN_WORD_LENGTH, config.MAX_WORD_LENGTH);
        String wordLengthStr = scanner.nextLine();
        int wordLength = Integer.parseInt(wordLengthStr);
        if (wordLength < config.MIN_WORD_LENGTH || wordLength > config.MAX_WORD_LENGTH) {
            System.out.println("Invalid word length. Please try again.");
            initialize();
        }
        vocabulary.filter(wordLength);
        return new State(vocabulary.sample());
    }

    /**
     * Conclude the game.
     * This method displays the score, the word and saves the result into the database
     */
    private static void debrief(State state) throws IOException {
        Database.write(state);
        Database.summarize();
        quit();
    }

    /**
     * Display the welcome message
     * @throws IOException - if the file with the welcome text could not be read
     */
    private static void welcome() throws IOException {
        String welcome = Files.readString(Paths.get(Config.welcomeTextFile));
        System.out.printf(
                welcome,
                config.GUESSES,
                Config.HELP_STRING,
                Config.QUIT_STRING,
                Config.HINT_STRING
        );
    }

    /**
     * Display the help message
     * @throws IOException - if the file with the help message could not be read
     */
    private static void help() throws IOException {
        String help = Files.readString(Paths.get(Config.helpTextFile));
        System.out.printf(
                help,
                config.MIN_WORD_LENGTH,
                config.MAX_WORD_LENGTH,
                config.GUESSES,
                Config.HINT_STRING
        );
    }

    /**
     * Exit the game
     */
    private static void quit() {
        System.out.println("Bye!");
        System.exit(0);
    }

}
