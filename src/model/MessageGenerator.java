package model;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
		
		for (int i = 0; i < 10; i++) {
			System.out.println(msgGen.generateText());
		}
	}

	private ArrayList<MarkovChain> listOfWords;
	private String[] commonArticles;
	
	public MessageGenerator() {
		listOfWords = new ArrayList<>();
		commonArticles = new String[3];
		commonArticles[0] = "A";
		commonArticles[1] = "An";
		commonArticles[2] = "The";
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
					mc.addOccurrence();
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
			if (mc.getWord().equalsIgnoreCase(word)) {
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
		String retVal = "",
			   currentWord = "";
		
		// Adds all the possible words into an array list
		for (MarkovChain mc : listOfWords) {
			if (mc.isFirst()) {
				possibleSentenceStarts.add(mc.getWord());
			}
		}
		
		// Chooses the first word of the sentence
		if (possibleSentenceStarts.isEmpty()) {
			// Use common articles to start the sentence
			if (Math.random() <= 0.5) {
				int randomNum = ThreadLocalRandom.current().nextInt(0, commonArticles.length);
				currentWord = commonArticles[randomNum];
			}
			// Choose a random word from the list of words to start the sentence by using a weighted selection based on the number of occurrences
			else {
				currentWord = weightedWordSelection();
			}
		}
		else {
			int randomNum = ThreadLocalRandom.current().nextInt(0, possibleSentenceStarts.size());
			currentWord = possibleSentenceStarts.get(randomNum);
		}
		
		retVal += currentWord + " ";
		int numberOfWords = ThreadLocalRandom.current().nextInt(10, 30);
		int currentNumWords = 1;
		
		// TODO: Possibly move the creation of a sentence into a new function and simply call it in this function
		while (currentNumWords != numberOfWords) {
			if (!contains(currentWord)) {
				currentWord = weightedWordSelection();
			}
			else {
				currentWord = getNextWord(currentWord);
			}
			
			retVal += currentWord + " ";
			currentNumWords++;
		}
		
		return retVal.substring(0,1).toUpperCase() + retVal.substring(1, retVal.length() - 1) + ".";
	}
	
	/**
	 * Randomly chooses a word from the list of all words, where the number of occurrences alters the chance of being selected. 
	 * This is done using a probability mass function.
	 * @return a word randomly selected from the list of words
	 */
	private String weightedWordSelection() {
		ArrayList<Pair<String,Double>> potentialStarters = new ArrayList<>();
		
		for (MarkovChain mc : listOfWords) {
			potentialStarters.add(new Pair<String, Double>(mc.getWord(), 0.0 + mc.getOccurrences()));
		}
		
		EnumeratedDistribution<String> weightedSelection = new EnumeratedDistribution<>(potentialStarters);
		
		return weightedSelection.sample();
	}
	
	/**
	 * Generates and returns a possible word based on the possible states of the given word. 
	 * If the word does not exist in any of the chains, it returns a period.
	 * @param word - a String containing the current word in the sentence
	 * @return a String with a possible response that follows the current word
	 */
	private String getNextWord(String word) {
		for (MarkovChain mc : listOfWords) {
			if (mc.getWord().equalsIgnoreCase(word)) {
				return mc.getPossibleState();
			}
		}
		
		return ".";
	}
}