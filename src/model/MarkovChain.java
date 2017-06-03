package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

/**
 * The fundamental structure to my implementation of the markov chain. 
 * It is capable of adding possible states and generating a response based off 
 * of the hashmap containing possible states, where the number of occurrences of 
 * the word in relation affects the possibility of being chosen. 
 * A markov chain is essentially a probabilistic model where the current state 
 * affects what possible states can follow afterward.
 * @author Richie Ren
 *
 */
public class MarkovChain {
	
	private String word;
	private int occurrences;
	private boolean capitalized;
	private boolean properNoun;
	private HashMap<String,Integer> possibleStates;
	
	public MarkovChain(String word) {
		this.word = word;
		this.occurrences = 1;
		this.capitalized = false;
		this.properNoun = false;
		this.possibleStates = new HashMap<>();
	}
	
	/**
	 * Adds the word to the list of possible responses to the current word contained in the chain.
	 * For example, in "The quick brown fox jumps over the lazy dog," the word "the" would have 
	 * "quick" and "lazy" as possible states. 
	 * 
	 * @param word - a String containing a possible response to the current word in the chain
	 */
	public void addState(String word) {
		// Increases the number of occurrences of the word
		if (possibleStates.containsKey(word)) {
			possibleStates.put(word, possibleStates.get(word) + 1);
		}
		// The first occurrence of the word
		else {
			possibleStates.put(word, 1);
		}
	}
	
	/**
	 * Returns the word contained in the chain.
	 * @return a String representation of the word
	 */
	public String getWord() {
		return this.word;
	}
	
	/**
	 * Indicates whether or not the current word was capitalized in the sentence.
	 * @return true if the word was capitalized, false otherwise
	 */
	public boolean isCapital() {
		return this.capitalized;
	}
	
	/**
	 * Sets the boolean flag that indicates whether or not the word was capitalized.
	 * @param value - a boolean value indicating its status
	 */
	public void setIsCapital(boolean value) {
		this.capitalized = value;
	}
	
	/**
	 * Indicates whether or not the current word is potentially a proper noun.
	 * @return true if the word is proper, false otherwise
	 */
	public boolean isProperNoun() {
		return this.properNoun;
	}
	
	/**
	 * Sets the boolean flag that indicates whether or not the word is a proper noun.
	 * @param value - a boolean value indicating its status
	 */
	public void setIsProperNoun(boolean value) {
		this.properNoun = value;
	}
	
	/**
	 * Generates a response based on the possible states of the current word. 
	 * The number of occurrences affects the chance of a specific state being chosen. 
	 * This is performed using a weighted selection via probability mass function. 
	 * Requires the ApacheCommons math library from http://commons.apache.org/proper/commons-math/index.html.
	 * @return a String that is one of many possible responses to the current word
	 */
	public String getPossibleState() {
		if (possibleStates.isEmpty()) {
			return "";
		}
		
		ArrayList<Pair<String,Double>> possibleStatesList = new ArrayList<>();
		
		// Adds all the states into an array list
		Set<String> keys = possibleStates.keySet();
		for (String key : keys) {
			possibleStatesList.add(new Pair<String, Double>(key, 0.0 + possibleStates.get(key)));
		}
		
		EnumeratedDistribution<String> weightedSelection = new EnumeratedDistribution<>(possibleStatesList);
		
		return weightedSelection.sample();
	}
	
	/**
	 * Increases the int containing the number of times the word has occurred in the input.
	 */
	public void addOccurrence() {
		this.occurrences++;
	}
	
	/**
	 * Returns the number of times that the word has occurred in the input.
	 * @return an int containing the number of occurrences
	 */
	public int getOccurrences() {
		return this.occurrences;
	}
}