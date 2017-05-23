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
 * @author Richie Ren
 *
 */
public class MarkovChain {
	
	private String word;
	private HashMap<String,Integer> possibleStates;
	
	public MarkovChain(String word) {
		this.word = word;
		possibleStates = new HashMap<>();
	}
	
	/**
	 * Adds the word to the list of possible responses to the current word contained in the chain.
	 * For example, in "The quick brown fox jumps over the lazy dog," the word "the" would have 
	 * "quick" and "lazy" as possible states. 
	 * 
	 * @param word - a word containing a possible response to the current word in the chain
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
	 * Generates a response based on the possible states of the current word. 
	 * The number of occurrences affects the chance of a specific state being chosen. 
	 * This is performed using a weighted selection via probability mass function. 
	 * Requires the ApacheCommons math library from http://commons.apache.org/proper/commons-math/index.html.
	 * @return a String that is one of many possible responses to the current word
	 */
	public String getPossibleState() {
		ArrayList<Pair<String,Double>> possibleStatesList = new ArrayList<>();
		
		// Adds all the states into an array list
		Set<String> keys = possibleStates.keySet();
		for (String key : keys) {
			possibleStatesList.add(new Pair<String, Double>(key, 0.0 + possibleStates.get(key)));
		}
		
		EnumeratedDistribution<String> weightedSelection = new EnumeratedDistribution<>(possibleStatesList);
		
		return weightedSelection.sample();
	}
}
