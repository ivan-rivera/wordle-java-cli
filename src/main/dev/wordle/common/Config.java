package main.dev.wordle.common;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * A class for handling the configurations.
 * Note that the word list was obtained from here:
 * <a href="https://github.com/dwyl/english-words/blob/master/words_alpha.txt">this word list</a>
 */
public class Config {

    public int GUESSES;
    public int MIN_WORD_LENGTH;
    public int MAX_WORD_LENGTH;
    public int DISPLAY_SOLUTIONS;
    public static final String HELP_STRING = ":HELP";
    public static final String QUIT_STRING = ":QUIT";
    public static final String HINT_STRING = ":HINT";
    public static final String DEBUG_STRING = ":DEBUG";
    public static final String welcomeTextFile = "resources/welcome.txt";
    public static final String helpTextFile = "resources/help.txt";

    public Config() throws IOException {
        load();
    }

    /**
     * Load config files and parse them into properties.
     * @throws IOException in case any of the files cannot be found
     */
    public void load() throws IOException {
        try {
            Properties properties = new Properties();
            properties.load(Files.newBufferedReader(Paths.get("resources/config.properties")));
            GUESSES = Integer.parseInt(properties.getProperty("GUESSES"));
            MIN_WORD_LENGTH = Integer.parseInt(properties.getProperty("MIN_WORD_LENGTH"));
            MAX_WORD_LENGTH = Integer.parseInt(properties.getProperty("MAX_WORD_LENGTH"));
            DISPLAY_SOLUTIONS = Integer.parseInt(properties.getProperty("DISPLAY_SOLUTIONS"));
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

}
