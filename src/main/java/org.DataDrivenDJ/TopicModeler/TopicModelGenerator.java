package org.DataDrivenDJ.TopicModeler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.util.HashMap;

public class TopicModelGenerator {


	private static int numWordsToAdd = 3;
	private static ArrayList<AlbumTopic> documents ;
	public void addSong(long songId, String lyrics) {
		AlbumTopic a1 = new AlbumTopic(songId,lyrics);
		documents.add(a1);
	}

	public TopicModelGenerator() {
		documents = new ArrayList<AlbumTopic>();
	}

	public static void runLDA() throws IOException {
		//LDAGibbs topicModeler = new LDAGibbs(5, 5, documents);
		LDAGibbs topicModeler = new LDAGibbs(5, documents,2,0.01,10000);

		topicModeler.generateTopics();
		//topicModeler.getIntegWordArray();
		topicModeler.ldaRandomTopicAssignment();
		//System.out.println("After Initialization is run ");
		/*topicModeler.getSongTopic();
		topicModeler.getWordTopic();
		topicModeler.getTopicModel();*/
		topicModeler.ldaGibbsSampling();
		//System.out.println("After Topic Modeling is run");
		/*topicModeler.getSongTopic();
		topicModeler.getWordTopic();*/
		topicModeler.calculateThetaAndPhi();
		/*topicModeler.getTheta();
		topicModeler.getPhi();*/

		//Search xml document and if number of words hit a threshold assign a topic
		final DocumentUtility docU = new DocumentUtility();
		final List<SongTopic> topicDocument = docU.getTopicsFromDisk();
		
		for(int doc_id = 0; doc_id < documents.size(); doc_id++) {
			int topic = topicModeler.getMostLikelyTopic(doc_id);
			List<Integer> tokens = topicModeler.getWordsFromTopic(topic);
			List<String> words = topicModeler.convertTokensToWords(tokens);
            

			double threshold = 0.3;
			int single_topic = 0;

			for(int i = 0; i < topicDocument.size(); i++) {

				
				double matchCount = 0;
				List<String> wordsOfTopic = topicDocument.get(i).getAssociatedTerms();
				//System.out.println(wordsOfTopic);
				for(int j = 0; j < wordsOfTopic.size(); j++) {
					for(int k = 0; k < words.size(); k++) {
						if(words.get(k).equals(wordsOfTopic.get(j))) {
							matchCount++;
						}
					}
				}
				matchCount = matchCount / wordsOfTopic.size();
				//System.out.format("topic_id = %d, topic = %s, count = %f\n" , topic, topicDocument.get(i).getTopicName(),matchCount);
				
				if (matchCount >= threshold) {
					documents.get(doc_id).addTopicWeight(topicDocument.get(i).getTopicName(), topicModeler.getWeightForTopic(doc_id,topic));
					single_topic = 1;
					break;
				}
				/*else
				{
					//returns a sorted list of top topic words with their weights
					List<LDAGibbs.WeightedWord> wordArray = topicModeler.getTopWordsAboveThreshold(topic);
					for(int m = 0; m < numWordsToAdd; m++)
					{
						int topicWeight =  (int) (wordArray.get(wordArray.size() - m - 1).getWeight() * 100);
						documents.get(doc_id).addTopicWeight(wordArray.get(wordArray.size() - m - 1).getWord(), topicWeight);
					}
				}*/
			}

			if(single_topic == 0) {
				List<LDAGibbs.WeightedWord> wordArray = topicModeler.getTopWordsAboveThreshold(topic);
				for(int m = 0; m < numWordsToAdd; m++) {
					int topicWeight =  (int) (wordArray.get(wordArray.size() - m - 1).getWeight() * 100);
					documents.get(doc_id).addTopicWeight(wordArray.get(wordArray.size() - m - 1).getWord(), topicWeight);
					
				}
			}
		}

		/*for (int docId = 0; docId < documents.size(); docId++) {
                    long songId = documents.get(docId).getSongId();
                    HashMap<String, Integer> topicWeight = documents.get(docId).getTopicWeight();
                    for (String topicName : topicWeight.keySet()) {                        
                        int weight = topicWeight.get(topicName);
                        System.out.format("doc_id = %d, songId = %d, topic = %s, weight = %d\n" , docId,songId,topicName,weight);
                    }                    
                }*/
	}

	public ArrayList<AlbumTopic> getDocuments() {

		return this.documents;
	}


}
