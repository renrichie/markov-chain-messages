package model;

import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

/**
 * This is the result of the individual pieces put together. 
 * The message generator will read in the given input, create a list of words and their next possible states, 
 * and use it to create a message. The message is not guaranteed to be coherent.
 * @author Richie Ren
 *
 */
public class MessageGenerator {

	public static void main(String[] args) {
		String test = "The quick brown fox jumped over the lazy dog";
		String[] input = test.split("\\s+");
		
		MessageGenerator msgGen = new MessageGenerator();
		
		for (int i = 0; i < input.length - 1; i++) {
			msgGen.addInput(input[i], input[i + 1]);
		}
		
		for (MarkovChain mc : msgGen.listOfWords) {
			System.out.printf("Current word: %s | Three attempts at possible responses: %s, %s, %s\n", mc.getWord(), mc.getPossibleState(), mc.getPossibleState(), mc.getPossibleState());
			
		}
	}
	// TODO: Switch this back to private; was only set to public for light testing
	public ArrayList<MarkovChain> listOfWords;
	
	public MessageGenerator() {
		listOfWords = new ArrayList<>();
	}
	
	/**
	 * Adds the word to the list of words and the word immediately following it as a possible state of the first. 
	 * Updates the number of occurrences of the second word as a possible state of the first word. 
	 * @param firstWord - the first word that is considered the main one in that respective chain
	 * @param wordAfter - the second word that is a possible state of the first word
	 */
	public void addInput(String firstWord, String wordAfter) {
		if (wordAfter == null) {
			return;
		}

		firstWord = firstWord.trim();
		wordAfter = wordAfter.trim();
		
		boolean sentenceStarter = Character.isUpperCase(firstWord.charAt(0));
		
		firstWord = firstWord.toLowerCase();
		wordAfter = wordAfter.toLowerCase();
		
		// The word has yet to be represented
		if (!contains(firstWord)) {
			MarkovChain newWord = new MarkovChain(firstWord);
			newWord.addState(wordAfter);
			newWord.setIsFirst(sentenceStarter);
			listOfWords.add(newWord);
			
		}
		// Not the first occurrence of the word
		else {
			for (MarkovChain mc : listOfWords) {
				if (mc.getWord().equals(firstWord)) {
					// Update the number of occurrences
					mc.addState(wordAfter);
					return;
				}
			}
		}
	}
	
	/**
	 * Checks to see if the word is already exists as a markov chain. 
	 * @param word - a String containing the word to check for
	 * @return true if the word is already represented in a chain, false otherwise
	 */
	public boolean contains(String word) {
		for (MarkovChain mc : listOfWords) {
			if (mc.getWord().equals(word)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Generates a text message using the markov chains.
	 * @return a String containing the message
	 */
	public String generateText() {
		ArrayList<String> possibleSentenceStarts = new ArrayList<>();
		
		// Adds all the possible words into an array list
		for (MarkovChain mc : listOfWords) {
			if (mc.isFirst()) {
				possibleSentenceStarts.add(mc.getWord());
			}
		}
		
		// TODO: finish writing the algorithm
		// Generate the first word of a sentence from a list of common ones like "The" or "A"
		// Possibly move the creation of a sentence into a new function and simply call it in this function
		if (possibleSentenceStarts.isEmpty()) {
			
		}
		else {
			
		}
		
		return "";
	}
	
	/**
	 * Generates and returns a possible word based on the possible states of the given word. 
	 * If the word does not exist in any of the chains, it returns a period.
	 * @param word - a String containing the current word in the sentence
	 * @return a String with a possible response that follows the current word
	 */
	private String getNextWord(String word) {
		for (MarkovChain mc : listOfWords) {
			if (mc.getWord().equals(word)) {
				return mc.getPossibleState();
			}
		}
		
		return ".";
	}
}
