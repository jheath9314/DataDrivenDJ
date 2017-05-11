package org.DataDrivenDJ.TopicModeler;
import java.util.*;
import java.lang.Math.*;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.*;

public class LDAGibbs {

	private int vocabSize;
	private int numTopics;
	private ArrayList<AlbumTopic> documents = null;
	// Number of songs in an album
	private int D;
	// Topic assignment array topic[D][N]; N is number of words in a song. D is
	// number of songs in an album
	private int topicModel[][];
	// Number of instances of a word w in topic numTopics
	// word_topic[vocabSize][numTopics]
	private int wordTopic[][];
	// Number of words in each song D, assigned to topic numTopics
	// song_topic[D][numTopics]
	private int songTopic[][];
	// Total words in each song
	private int songTotalWords[];
	// Total words in each topic
	private int topicTotalWords[];

	// Hyper-parameters of LDS : apriori
	private double alpha;
	private double beta;

	// Post run normalization parameters
	// Probability that a document belongs to a particular topic - Normalizes
	// songTopic
	// theta[D][numTopics]
	private double theta[][];

	// Probability that a given word belongs to a particular topic - Normalizes
	// wordTopic
	// phi[vocabSize][numTopics]
	private double phi[][];

	// Gibbs Sampling iterations
	private int numIterations;

	// Word map translates the hashed integer value back to the word
	// private HashMap wordMap = new HashMap();

	// integWordArray is a 2d array containing all of the integer tokens for a
	// song.

	// Threshold for probability of a word belonging to a topic to be accepted.
	private double threshold = 0.01;

	private TokenGenerator tkGen = new TokenGenerator(vocabSize);

	ArrayList<ArrayList<Integer>> integWordArray = new ArrayList<ArrayList<Integer>>();

	/*****************************************
	 * Notes: Some variables are redundant but can be refactored out after
	 * algorithm is stable. They're left for consistency with the develpment
	 * plan.
	 * 
	 * D (number of songs) is documents.length()
	 * 
	 * 
	 ***********************************************/

	LDAGibbs(int numTopics, ArrayList<AlbumTopic> documents, double alpha, double beta, int numIterations) {
		this.vocabSize = vocabSize;
		this.numTopics = numTopics;
		this.documents = documents;
		this.alpha = alpha;
		this.beta = beta;
		this.numIterations = numIterations;
		D = documents.size();

		// Define LDA arrays
		topicModel = new int[this.D][];
		songTopic = new int[this.D][this.numTopics];
		songTotalWords = new int[this.D];
		topicTotalWords = new int[this.numTopics];
		theta = new double[this.D][this.numTopics];

	}

	public int getVocabSize() {
		return vocabSize;
	}

	public void setVocabSize(int vocabSize) {
		this.vocabSize = vocabSize;
	}

	public int getNumTopics() {
		return numTopics;
	}

	public void setNumTopics(int numTopics) {
		this.numTopics = numTopics;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public void setnumIterations(int numIterations) {
		this.numIterations = numIterations;
	}

	public ArrayList<AlbumTopic> getDocuments() {
		return documents;
	}

	public void setDocuments(ArrayList<AlbumTopic> documents) {
		this.documents = documents;
	}

	public void generateTopics() throws IOException {
		DocumentUtility docUtil = new DocumentUtility();
		//docUtil.removeFunctionalWords(documents);
		docUtil.removeFunctionalWordsV2(documents);
		tokenizeWords();
		//docUtil.removeFunctionalWordsV2(documents);

		// ldaRandomTopicAssignment();
		// ldaGibbsSampling();

	}

	public void getIntegWordArray() {

		System.out.println("Printing IntegAray");

		for (int i = 0; i < this.integWordArray.size(); i++) {
			for (int j = 0; j < this.integWordArray.get(i).size(); j++) {
				System.out.print(this.integWordArray.get(i).get(j));
				System.out.print("\t");
			}
			System.out.println("\t");
		}
	}

	public void getSongTopic() {

		System.out.println("Printing SongTopic");
		for (int songCnt = 0; songCnt < this.D; songCnt++) {
			for (int topicCnt = 0; topicCnt < this.numTopics; topicCnt++) {
				System.out.format("%d\t", songTopic[songCnt][topicCnt]);
			}
			System.out.println("\n");
		}
	}

	public void getWordTopic() {

		System.out.println("Printing WordTopic");

		for (int wordCnt = 0; wordCnt < this.vocabSize; wordCnt++) {
			for (int topicCnt = 0; topicCnt < this.numTopics; topicCnt++) {
				System.out.format("%d\t", wordTopic[wordCnt][topicCnt]);
			}
			System.out.println("\n");
		}
	}

	public void getTopicModel() {

		System.out.println("Printing TopicModel");

		int wordsInSong;
		for (int songCnt = 0; songCnt < this.D; songCnt++) {
			wordsInSong = this.integWordArray.get(songCnt).size();
			for (int wordCnt = 0; wordCnt < wordsInSong; wordCnt++) {
				System.out.format("%d\t", topicModel[songCnt][wordCnt]);
			}
			System.out.println("\n");
		}
	}

	public void getSongTotalWords() {

		System.out.println("Printing total words in a Song");

		for (int songCnt = 0; songCnt < this.D; songCnt++) {
			System.out.format("Words in Song %d = %d\n", songCnt, this.songTotalWords[songCnt]);

		}
	}

	public void getTheta() {

		System.out.println("Printing SongTopic Normalized : Theta");
		for (int songCnt = 0; songCnt < this.D; songCnt++) {
			for (int topicCnt = 0; topicCnt < this.numTopics; topicCnt++) {
				System.out.format("%f\t", this.theta[songCnt][topicCnt]);
			}
			System.out.println("\n");

		}
	}

	public void getPhi() {

		System.out.println("Printing WordTopic Normalized : Phi");

		for (int wordCnt = 0; wordCnt < this.vocabSize; wordCnt++) {
			for (int topicCnt = 0; topicCnt < this.numTopics; topicCnt++) {
				System.out.format("%f\t", this.phi[wordCnt][topicCnt]);
			}
			System.out.println("\n");

		}
	}

	public int getNumberOfWordsInSong(String song) {
		String trimmed = song.trim();
		return trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
	}

	public void tokenizeWords() {
		ArrayList<String[]> wordArray = new ArrayList<String[]>();

		// Delimit the words by a single space.
		for (int i = 0; i < documents.size(); i++) {
			wordArray.add(i, documents.get(i).getSongLyrics().split(" "));
		}

		// Replace individual words with integer tokens, storing the mappings in
		// wordMap

		for (int i = 0; i < wordArray.size(); i++) {
			integWordArray.add(new ArrayList<Integer>());

			for (int j = 0; j < wordArray.get(i).length; j++) {

				//System.out.println(wordArray.get(i)[j]);
				integWordArray.get(i).add(tkGen.generateToken(wordArray.get(i)[j]));

			}
		}
		this.vocabSize = tkGen.getCurrentVocabCount();
		wordTopic = new int[this.vocabSize][this.numTopics];
		phi = new double[this.vocabSize][this.numTopics];
	}

	public void ldaRandomTopicAssignment() {

		int wordsInSong;
		int randomTopic;

		for (int songCnt = 0; songCnt < this.D; songCnt++) {
			wordsInSong = this.integWordArray.get(songCnt).size();
			this.topicModel[songCnt] = new int[wordsInSong];
			// Record total words in a given song
			this.songTotalWords[songCnt] = wordsInSong;

			for (int wordCnt = 0; wordCnt < wordsInSong; wordCnt++) {
				// random assignment of topic
				randomTopic = (int) (Math.random() * this.numTopics);
				this.topicModel[songCnt][wordCnt] = randomTopic;
				// Increment all arrays pertaining to LDS
				this.wordTopic[(integWordArray.get(songCnt)).get(wordCnt)][randomTopic]++;
				this.songTopic[songCnt][randomTopic]++;
				this.topicTotalWords[randomTopic]++;
			}
		}
	}

	public void ldaGibbsSampling() {

		int gibbsSampledTopic;
		for (int gibbsCnt = 0; gibbsCnt < this.numIterations; gibbsCnt++) {
			for (int songCnt = 0; songCnt < this.D; songCnt++) {

				for (int wordCnt = 0; wordCnt < this.songTotalWords[songCnt]; wordCnt++) {
					gibbsSampledTopic = gibbsProbabilityDistribution(songCnt, wordCnt);
					this.topicModel[songCnt][wordCnt] = gibbsSampledTopic;

				}

			}

		}

	}

	public int gibbsProbabilityDistribution(int songCnt, int wordCnt) {

		// Decrement all LDA arrays
		int previousTopic = this.topicModel[songCnt][wordCnt];
		this.wordTopic[(integWordArray.get(songCnt)).get(wordCnt)][previousTopic]--;
		this.songTopic[songCnt][previousTopic]--;
		this.topicTotalWords[previousTopic]--;
		this.songTotalWords[songCnt]--;

		// Gibbs Probability Distribution using multinomial sampling
		double gibbsProb[] = new double[this.numTopics];
		for (int topicCnt = 0; topicCnt < this.numTopics; topicCnt++) {
			gibbsProb[topicCnt] = (wordTopic[(integWordArray.get(songCnt)).get(wordCnt)][topicCnt] + this.beta)
					/ (topicTotalWords[topicCnt] + this.vocabSize * this.beta)
					* (songTopic[songCnt][topicCnt] + this.alpha)
					/ (songTotalWords[songCnt] + this.numTopics * this.alpha);
		}

		// Cumulative probability distribution

		for (int topicCnt = 1; topicCnt < this.numTopics; topicCnt++) {
			gibbsProb[topicCnt] = gibbsProb[topicCnt] + gibbsProb[topicCnt - 1];
		}

		double scaledTopic = Math.random() * gibbsProb[this.numTopics - 1];
		int updatedTopic;
		for (updatedTopic = 0; updatedTopic < this.numTopics; updatedTopic++) {
			if (scaledTopic < gibbsProb[updatedTopic])
				break;
		}

		// Increment all LDA variables
		this.wordTopic[(integWordArray.get(songCnt)).get(wordCnt)][updatedTopic]++;
		this.songTopic[songCnt][updatedTopic]++;
		this.topicTotalWords[updatedTopic]++;
		this.songTotalWords[songCnt]++;

		return updatedTopic;

	}

	public void calculateThetaAndPhi() {
		for (int songCnt = 0; songCnt < this.D; songCnt++) {
			for (int topicCnt = 0; topicCnt < this.numTopics; topicCnt++) {
				this.theta[songCnt][topicCnt] = (this.songTopic[songCnt][topicCnt] + this.alpha)
						/ (this.songTotalWords[songCnt] + this.numTopics * this.alpha);
			}
		}

		for (int wordCnt = 0; wordCnt < this.vocabSize; wordCnt++) {
			for (int topicCnt = 0; topicCnt < this.numTopics; topicCnt++) {
				this.phi[wordCnt][topicCnt] = (this.wordTopic[wordCnt][topicCnt] + this.beta)
						/ (this.topicTotalWords[topicCnt] + this.vocabSize * this.beta);
			}
		}
	}

	// Get top two mostly likely topics for a document. Pull all words from that
	// topic. Look for words in Topics object holding mappings
	public int getMostLikelyTopic(int doc) {
		double largest = -1;
		double secondLargest = -1;
		int largestIndex = -1;
		/*
		 * if (this.numTopics < numTopics) { System.err.
		 * println("Too many topics requested, defaulting to max Topics collected"
		 * ); numTopics = this.numTopics; }
		 */

		for (int i = 0; i < numTopics; i++) {
			if (theta[doc][i] > largest) {
				secondLargest = largest;
				largest = theta[doc][i];
				largestIndex = i;
			} else if (theta[doc][i] > secondLargest) {
				// Update second largest here if needed
			}
		}
		return largestIndex;
	}

	public List<Integer> getWordsFromTopic(int topic) {

		List<Integer> wordArray = new ArrayList<Integer>();

		for (int i = 0; i < vocabSize; i++) {
			if (phi[i][topic] > threshold) {
				wordArray.add(i);
			}
		}
		return wordArray;
	}

	public List<WeightedWord> getTopWordsAboveThreshold(int topic) {

		List<WeightedWord> wordArray = new ArrayList<WeightedWord>();

		for (int i = 0; i < vocabSize; i++) {
			if (phi[i][topic] > threshold) {
				wordArray.add(new WeightedWord(phi[i][topic], tkGen.getStringFromToken(i)));
			}

			Collections.sort(wordArray, new Comparator<WeightedWord>() {
				@Override
				public int compare(WeightedWord o1, WeightedWord o2) {
					if (o1.weight < o2.weight)
					{
						return -1;
					}
					if (o1.weight > o2.weight)
					{
						return 1;
					}
					return 0;
				}
			});
		}
		return wordArray;
	}

	public List<String> convertTokensToWords(List<Integer> tokenArray) {
		List<String> wordArray = new ArrayList<String>();

		for (int i = 0; i < tokenArray.size(); i++) {
			wordArray.add(this.tkGen.getStringFromToken(i));
		}

		return wordArray;
	}

	public int getWeightForTopic(int docId, int topicId) {

		int weight = (int) (this.theta[docId][topicId] * 100);

		return weight;
	
	}

	public class WeightedWord
	{
		private double weight = 0;
		private String word = "";

		WeightedWord(double weight, String word)
		{
			this.weight = weight;
			this.word = word;
		}

		public double getWeight()
		{
			return weight;
		}

		public String getWord()
		{
			return word;
		}

	}
	
	 
}




