import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Markov.java
 * CST 338
 *
 * Author: Rasna Husain
 * Date: July 22, 2025
 *
 * This program implements a Markov chain for text generation.
 * It reads a text file, builds a collection of words and their
 * subsequent words, and then uses this data to generate new text.
 * This assignment is based on one from CS5 at Harvey Mudd.
 */

public class Markov {

    // Constant Fields
    public static final String BEGINS_SENTENCE = "__$";

    // Updated PUNCTUATION_MARKS to match the test's expectation of ".!?"
    public static final String PUNCTUATION_MARKS = ".!?";


    // Members
    private HashMap<String, ArrayList<String>> words;
    private String prevWord;

    /**
     * The constructor for this class will initialize the HashMap words with the key BEGINS_SENTENCE
     * and a value of a new ArrayList.
     * This method will also set prevWord = BEGINS_SENTENCE.
     */
    public Markov() {
        words = new HashMap<>();
        words.put(BEGINS_SENTENCE, new ArrayList<>());
        prevWord = BEGINS_SENTENCE;
    }

    /**
     * This is a getter that returns the list of words.
     *
     * @return The HashMap containing words and their following words.
     */
    public HashMap<String, ArrayList<String>> getWords() {
        return words;
    }

    /**
     * This method takes a String called filename that represents the file that will be parsed
     * into the words HashMap. This method opens the file and calls the addLine method to parse
     * the individual lines from the filename.
     * This method should catch any errors that are generated from file operations.
     *
     * @param filename The name of the file to be parsed.
     */
    public void addFromFile(String filename) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                addLine(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + filename);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while reading the file: " + e.getMessage());
        }
    }

    /**
     * This is a simple method that performs two very important operations.
     * First it ensures that the line being read (the passed in String parameter) is not 0 length.
     * Once a String is determined to have content, the String is split into individual words.
     * These words are then passed to the addWord(String) method.
     *
     * @param line The line of text to be processed.
     */
    public void addLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return;
        }
        // Split by whitespace. This means punctuation stays attached to words,
        // which is handled by endsWithPunctuation.
        // We also handle potential multiple spaces between words from badLineTest
        String[] lineWords = line.trim().split("\\s+");
        for (String word : lineWords) {
            if (!word.isEmpty()) { // Ensure no empty strings from multiple spaces
                addWord(word);
            }
        }
    }

    /**
     * This is the method that does most of the processing for this application.
     * The first thing that is done is the previous word is checked to see if it ends with punctuation.
     * If the previous word ends with punctuation then the current word is added under the BEGINS_SENTENCE
     * key in the words HashMap.
     * If the previous word did NOT end with punctuation then the HashMap words is checked to see if
     * the previous word has an entry as a key in the words HashMap. If the previous word is not present
     * as a key in the HashMap, it will need to be added as a key.
     * If the previous word is present as a key in the HashMap, the current word is added to the ArrayList
     * that uses the previous word as a key.
     * Finally set prevWord equal to current word.
     *
     * @param currentWord The word to be added to the Markov chain.
     */
    public void addWord(String currentWord) {
        if (currentWord == null || currentWord.isEmpty()) {
            return;
        }

        if (endsWithPunctuation(prevWord)) {
            // If the previous word ended in punctuation, the current word starts a new sentence.
            words.get(BEGINS_SENTENCE).add(currentWord);
        } else {
            // Otherwise, add the current word as a follower of the previous word.
            // computeIfAbsent is used for concise handling of adding prevWord as a key if it doesn't exist
            words.computeIfAbsent(prevWord, k -> new ArrayList<>()).add(currentWord);
        }
        prevWord = currentWord;
    }

    /**
     * This method is responsible for building a sentence from the contents of the HashMap words.
     * It will make use of the method randomWord(String) described below.
     * This method first picks a random word from the values stored under the key BEGINS_SENTENCE.
     * This word becomes the current word.
     * If the current word does not end in punctuation, it is added to the sentence being constructed
     * along with a space and a new random word is selected.
     * If the current word DOES end in punctuation, it is added to the String being constructed
     * and no additional word is chosen.
     * The String being built is then returned.
     *
     * @return A randomly generated sentence.
     */
    public String getSentence() {
        StringBuilder sentence = new StringBuilder();
        String currentWord = randomWord(BEGINS_SENTENCE);

        // Keep adding words until we hit null (no follower) or a word ending in punctuation.
        while (currentWord != null) {
            sentence.append(currentWord);
            if (endsWithPunctuation(currentWord)) {
                break; // End of sentence
            }
            sentence.append(" ");
            currentWord = randomWord(currentWord);
        }
        return sentence.toString();
    }

    /**
     * This method takes a word as a parameter. That word is used as a key to retrieve an ArrayList
     * of words from the HashMap words.
     * A random word is chosen from the ArrayList and returned.
     *
     * @param word The word to use as a key to find the next word.
     * @return A random word from the list of words that follow the given word, or null if no words are found.
     */
    public String randomWord(String word) {
        ArrayList<String> nextWords = words.get(word);
        if (nextWords == null || nextWords.isEmpty()) {
            return null;
        }
        Random rand = new Random();
        return nextWords.get(rand.nextInt(nextWords.size()));
    }

    /**
     * This method is static.
     * This method takes a String and checks if the last character of the String exists in PUNCTUATION_MARKS.
     * If the last character does exist in PUNCTUATION_MARKS the method returns true, otherwise it returns false.
     * If you get an error add a try catch.
     * This method should catch any errors that may occur when checking for punctuation.
     * If an error is caught, print the word that caused the error along with an error message.
     * This is unlikely to occur however. If the IDE complains you can skip this step.
     *
     * @param word The word to check for ending punctuation.
     * @return True if the word ends with punctuation, false otherwise.
     */
    public static boolean endsWithPunctuation(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        try {
            char lastChar = word.charAt(word.length() - 1);
            return PUNCTUATION_MARKS.indexOf(lastChar) != -1;
        } catch (IndexOutOfBoundsException e) {
            // This would only happen if word.length() - 1 is invalid, which is caught by isEmpty()
            System.err.println("Error checking punctuation for word: '" + word + "' - " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while checking punctuation for word: '" + word + "' - " + e.getMessage());
            return false;
        }
    }

    /**
     * The toString will return the toString of the HashMap words.
     *
     * @return A string representation of the words HashMap.
     */
    @Override
    public String toString() {
        return words.toString();
    }
}