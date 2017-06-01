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
		String test = "Her big, brown eyes looked up at me pleadingly, as the first tear began to roll slowly down her soft, six-year-old, innocent face. I was as shocked as she was. I had been called to my daughter’s new school by the remedial teacher. Was I aware my daughter had learning difficulties? Yes I was. Due to constant ear infections Elizabeth had lost her hearing for the best part of two years of her young life. Amongst other things, her speech had been delayed and her reading was behind other children her age. This teacher, this person specially trained to help children, the very person who was supposed to help my daughter reach her full potential, had called me in to school to tell me, and my daughter, that she would be lucky if she ever managed to finished high school. What is a mother supposed to do when even the remedial teacher doesn’t have any faith in your child’s ability? What do you tell your child when she has been told that she is doomed to failure before she has even finished primary school? That was the question I was struggling to answer even as I stood there in that room, listening to this insensitive, authoritarian woman, who had no heart, wishing I had never brought my daughter to this school. I wondered, if this teacher was a mother? No, she couldn’t be. I truly believe you cannot have a child and not have a heart. I took my child’s small hand as we left this cold, killer-of-dreams office, to step outside into the sweet, fresh air. There, I bent down on one knee and, putting my hands on my daughter small but now heavy shoulders, I looked her straight in the eyes. “Elizabeth”, I said sternly, “If you work hard at school, I promise you, you can be anything you want to be.” She smiled and took my hand for the journey home. Elizabeth finished high school. In fact she went on to university. The day Elizabeth was presented with her Degree, a Bachelor in Social Studies, I emailed the school. Of course, I knew the remedial teacher would be long gone but I wanted every teacher there to know, as I would like all teachers to know, that every child has potential ... that no-one should ever tell a child they are beyond hope. Elizabeth’s story is a perfect example ... every child has potential. I hope there is not one parent who will listen to anybody who suggests otherwise.";
		String[] input = test.split("\\s+");
		
		MessageGenerator msgGen = new MessageGenerator();
		
		for (int i = 0; i < input.length - 1; i++) {
			msgGen.addInput(input[i], input[i + 1]);
		}
		
		for (int i = 0; i < 10; i++) {
			System.out.println(msgGen.generateText());
			System.out.println();
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
		boolean needToCapitalize = false;
		
		// TODO: Possibly move the creation of a sentence into a new function and simply call it in this function
		while (currentNumWords != numberOfWords) {
			if (!contains(currentWord)) {
				currentWord = weightedWordSelection();
			}
			else {
				currentWord = getNextWord(currentWord);
			}
			
			// TODO: Maybe add some way to recognize proper nouns and leave them capitalized
			// Capitalizes the word
			if (needToCapitalize) {
				currentWord = currentWord.substring(0,1).toUpperCase() + currentWord.substring(1);
				needToCapitalize = false;
			}
			else if (currentWord.equalsIgnoreCase("I")) {
				currentWord = currentWord.toUpperCase();
			}
			else {
				currentWord = currentWord.toLowerCase();
			}
			
			// Checks to see if it is the end of a sentence
			char lastChar = currentWord.charAt(currentWord.length() - 1);
			if (lastChar == '!' || lastChar == '.' || lastChar == '?' || lastChar == '"' || lastChar == ')') {
				needToCapitalize = true;
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