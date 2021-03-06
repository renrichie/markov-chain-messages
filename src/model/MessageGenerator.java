package model;

import java.applet.Applet;
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
public class MessageGenerator extends Applet {

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
	        System.exit(-1);
	      }
	    }
	    for (Status s : statuses) {
	    	System.out.println("@" + s.getUser().getScreenName() + " - " + s.getText());
	    	System.out.println();
	    }
	    System.out.println("Total: "+statuses.size());
	}

	private ArrayList<MarkovChain> listOfWords;
	private final String[] commonArticles = new String[] {"A", "An", "The"};
	private final int minWordsInSentence = 3;
	private boolean analyzingInput;
	
	public MessageGenerator() {
		listOfWords = new ArrayList<>();
		analyzingInput = false;
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
		if (listOfWords.isEmpty()) {
			return "";
		}
		
		ArrayList<String> possibleSentenceStarts = new ArrayList<>();
		String retVal = "",
			   currentWord = "";
		
		int currentNumWordsSentence = 0;
		
		// Adds all the possible words into an array list
		for (MarkovChain mc : listOfWords) {
			if (mc.isCapital()) {
				possibleSentenceStarts.add(mc.getWord());
			}
		}
		
		do {
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
			
			char lastCharacterFirstWord = currentWord.charAt(currentWord.length() - 1);
			if (!(lastCharacterFirstWord == '!' || lastCharacterFirstWord == '.' || lastCharacterFirstWord == '?')) {
				currentNumWordsSentence++;
			}
		} while (currentNumWordsSentence == 0);
		
		retVal += currentWord + " ";
		int numWordsTotal = ThreadLocalRandom.current().nextInt(10, 30);
		int currentNumWords = 1;
		
		while (currentNumWords != numWordsTotal) {
			char lastChar = currentWord.charAt(currentWord.length() - 1);
			boolean endOfSentence = (lastChar == '!' || lastChar == '.' || lastChar == '?');
			
			// Keeps generating words until a word without punctuation is given if the sentence has not reached the minimum size yet
			do {
				currentWord = getNextWord(currentWord);
				lastChar = currentWord.charAt(currentWord.length() - 1);
				endOfSentence = (lastChar == '!' || lastChar == '.' || lastChar == '?');
			} while (currentNumWordsSentence < minWordsInSentence && endOfSentence);
			
			retVal += currentWord + " ";
			currentNumWords++;
			currentNumWordsSentence++;
			
			// Resets the count for words in the current sentence
			if (currentNumWordsSentence > minWordsInSentence && endOfSentence) {
				currentNumWordsSentence = 0;
			}
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
	 * Returns whether or not the message generator is currently busy analyzing a profile.
	 * @return a boolean indicating its current state
	 */
	public boolean isAnalyzing() {
		return this.analyzingInput;
	}
	
	/**
	 * Reads in the specified user's Twitter timeline and parses it for input.
	 * @param user - the Twitter user to be analyzed
	 * @return an int indicating the function's error code; 0 = normal operation, -1 = no such user exists
	 */
	public int readFromTwitter(String user) {
		if (analyzingInput) {
			return 0;
		}
		
		if (!checkIfUserExists(user)) {
			return -1;
		}
		
		
		// Starts a new thread
		// Anonymous Thread method taken from ELITE at https://stackoverflow.com/questions/30286705/
		new Thread() {
			public void run() {
				analyzingInput = true;
				clearInput();
				System.gc();

				// Creates a builder using Twitter auth keys
				ConfigurationBuilder cb = new ConfigurationBuilder();
				readInAPIKeys(cb);

				// Pulls the Tweets from the specified user's profile
				Twitter twitter = new TwitterFactory(cb.build()).getInstance();

				int pageno = 1;
				List<Status> statuses = new ArrayList<>();

				while (true) {
					try {
						int size = statuses.size(); 
						Paging page = new Paging(pageno++, 100);
						statuses.addAll(twitter.getUserTimeline(user, page));
						if (statuses.size() == size)
							break;
					}
					catch(TwitterException te) {
						analyzingInput = false;
						return;
					}
				}

				if (statuses.isEmpty()) {
					analyzingInput = false;
					return;
				}

				// Puts in the text as input for the markov chains
				String text = "";

				for (Status s : statuses) {
					text += s.getText() + " ";
				}

				String[] input = text.split("\\s+");

				for (int i = 0; i < input.length - 1; i++) {
					addInput(input[i], input[i + 1]);
				}

				analyzingInput = false;
			}
		}.start();
		
		return 0;
	}
	
	/**
	 * Reads in the Twitter API keys; used to reduce code duplication
	 * @param cb - a ConfigurationBuilder to read the keys into
	 */
	private void readInAPIKeys(ConfigurationBuilder cb) {
		try {
			Scanner keyReader = new Scanner(new File("assets/keys"));
			cb.setOAuthConsumerKey(keyReader.nextLine());
			cb.setOAuthConsumerSecret(keyReader.nextLine());
			cb.setOAuthAccessToken(keyReader.nextLine());
			cb.setOAuthAccessTokenSecret(keyReader.nextLine());
			keyReader.close();
		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Checks to see if the Twitter user exists
	 * @param user - the username of the Twitter user
	 * @return a boolean indicating if the user exists or not
	 */
	private boolean checkIfUserExists(String user) {
		// Creates a builder using Twitter auth keys
		ConfigurationBuilder cb = new ConfigurationBuilder();
		readInAPIKeys(cb);

		// Pulls the Tweets from the specified user's profile
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();
		
		// Check if the user exists
		try{
			twitter.showUser(user);
		}
		catch (TwitterException te) {
			if (te.getStatusCode() == 404) {
				return false;
			}
		}
		
		return true;
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
	private String getPossibleWord(String word) {
		for (MarkovChain mc : listOfWords) {
			if (mc.getWord().equalsIgnoreCase(word)) {
				return mc.getPossibleState();
			}
		}
		
		return ".";
	}
	
	/**
	 * Generates the next word in the sentence based on the current word. 
	 * If the current word is not in the data structure, it performs a weighted selection on all the words of the input.
	 * @param currentWord - a String containing the current word in the sentence
	 * @return a String with the next word in the sentence
	 */
	private String getNextWord(String currentWord) {
		String wordNoNonAlphanumerics = currentWord.replaceAll("[^A-Za-z0-9]", "");

		// Generates a word that is not entirely non-Alphanumerics
		while (wordNoNonAlphanumerics.length() == 0) {
			currentWord = weightedWordSelection();
			wordNoNonAlphanumerics = currentWord.replaceAll("[^A-Za-z0-9]", "");
		}
		
		char lastChar = ' ';
		boolean needToCapitalize = false;
		
		// Checks to see if it is the beginning of a new sentence
		if (currentWord.length() > 0) {
			lastChar = currentWord.charAt(currentWord.length() - 1);
			needToCapitalize = (lastChar == '!' || lastChar == '.' || lastChar == '?');
		}
		
		// Chooses a word
		if (!contains(currentWord)) {
			currentWord = weightedWordSelection();
		}
		else {
			currentWord = getPossibleWord(currentWord);
		}

		char firstChar = currentWord.charAt(0);

		// Capitalizes the word
		if (needToCapitalize) {
			if ((firstChar == '\u201c' || firstChar == '"') && currentWord.length() >= 2) {
				currentWord = currentWord.substring(0,1) + currentWord.substring(1,2).toUpperCase() + currentWord.substring(2);
			}
			else {
				currentWord = currentWord.substring(0,1).toUpperCase() + currentWord.substring(1);
			}
			needToCapitalize = false;
		}
		else if (wordNoNonAlphanumerics.equalsIgnoreCase("I")) {
			currentWord = currentWord.toUpperCase();
		}
		
		return currentWord;
	}
}