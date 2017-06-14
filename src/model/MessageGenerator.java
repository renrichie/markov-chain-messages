package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * This is the result of the individual pieces put together. 
 * The message generator will read in the given input, create a list of words and their next possible states, 
 * and use it to create a message. The message is not guaranteed to be coherent.
 * @author Richie Ren
 *
 */
public class MessageGenerator {

	public static void main(String[] args) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setOAuthConsumerKey("YOUR KEY");
	    cb.setOAuthConsumerSecret("YOUR KEY");
	    cb.setOAuthAccessToken("YOUR KEY");
	    cb.setOAuthAccessTokenSecret("YOUR KEY");

	    Twitter twitter = new TwitterFactory(cb.build()).getInstance();

	    int pageno = 1;
	    String user = "realDonaldTrump";
	    List<Status> statuses = new ArrayList<>();

	    while (true) {

	      try {

	        int size = statuses.size(); 
	        Paging page = new Paging(pageno++, 100);
	        statuses.addAll(twitter.getUserTimeline(user, page));
	        if (statuses.size() == size)
	          break;
	      }
	      catch(TwitterException e) {

	        e.printStackTrace();
	      }
	    }
	    for (Status s : statuses) {
	    	System.out.println("@" + s.getUser().getScreenName() + " - " + s.getText());
	    	System.out.println();
	    }
	    System.out.println("Total: "+statuses.size());
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
			newWord.setIsCapital(sentenceStarter);
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
	private boolean contains(String word) {
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
		if (listOfWords.isEmpty()) {
			return "";
		}
		
		ArrayList<String> possibleSentenceStarts = new ArrayList<>();
		String retVal = "",
			   currentWord = "";
		
		// Adds all the possible words into an array list
		for (MarkovChain mc : listOfWords) {
			if (mc.isCapital()) {
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
		
		char lastCharTemp = currentWord.charAt(currentWord.length() - 1);
		needToCapitalize = (lastCharTemp == '!' || lastCharTemp == '.' || lastCharTemp == '?' || lastCharTemp == '"' || lastCharTemp == '\u201d' || lastCharTemp == ')');
		
		// TODO: Possibly move the creation of a sentence into a new function and simply call it in this function
		while (currentNumWords != numberOfWords) {
			if (!contains(currentWord)) {
				currentWord = weightedWordSelection();
			}
			else {
				currentWord = getNextWord(currentWord);
			}
			
			String wordNoNonAlphanumerics = currentWord.replaceAll("[^A-Za-z0-9]", "");
			
			if (wordNoNonAlphanumerics.length() == 0) {
				continue;
			}
			
			char firstChar = currentWord.charAt(0);
			
			// TODO: Maybe add some way to recognize proper nouns and leave them capitalized
			// Capitalizes the word
			if ((firstChar == '\u201c' || firstChar == '"') && currentWord.length() >= 2) {
				currentWord = currentWord.substring(0,1) + currentWord.substring(1,2).toUpperCase() + currentWord.substring(2);
			}
			else if (needToCapitalize) {
				currentWord = currentWord.substring(0,1).toUpperCase() + currentWord.substring(1);
				needToCapitalize = false;
			}
			else if (wordNoNonAlphanumerics.equalsIgnoreCase("I")) {
				currentWord = currentWord.toUpperCase();
			}
			else {
				currentWord = currentWord.toLowerCase();
			}
			
			// Checks to see if it is the end of a sentence
			char lastChar = currentWord.charAt(currentWord.length() - 1);
			needToCapitalize = (lastChar == '!' || lastChar == '.' || lastChar == '?' || lastChar == '"' || lastChar == '\u201d' || lastChar == ')');
			
			retVal += currentWord + " ";
			currentNumWords++;
		}
		
		retVal = retVal.substring(0,1).toUpperCase() + retVal.substring(1, retVal.length() - 1);
		
		// Adds a period for punctuation, otherwise randomizes the ending punctuation mark
		if (Character.isLetterOrDigit(retVal.charAt(retVal.length() - 1))) {
			return retVal + ".";
		}
		
		char[] punctuation = { '.', '!', '?' };
		char endingPunctuation = punctuation[ThreadLocalRandom.current().nextInt(0, punctuation.length)];
		
		return retVal.substring(0, retVal.length() - 1) + endingPunctuation;
	}
	
	/**
	 * Clears all markov chains from the generator and then calls Java's garbage collector.
	 */
	public void clearInput() {
		listOfWords.clear();
		System.gc();
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