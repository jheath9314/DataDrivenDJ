package org.DataDrivenDJ.TopicModeler;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.util.*;
import org.dom4j.*;
import org.DataDrivenDJ.nlp.LyricsTokenizer;
import org.dom4j.io.SAXReader;


public class DocumentUtility {

	String baseFilePath = "./build/resources/main/";

	public void removeFunctionalWords(ArrayList<AlbumTopic> documents) {
		// Get list of functional words
		Scanner scan = null;
		ArrayList<String> wordList = new ArrayList<String>();
		File functionalWordsFile = new File(this.getClass().getClassLoader().getResource( "Topics.xml").getFile());


		try {
			scan = new Scanner(functionalWordsFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scan.hasNextLine()) {
			// Remove functional words line by line
			// In order to expand list of function words add the word to the
			// text file
			// Removing apostrophes because they're a pain

			// TODO:
			// This currently does not handle differing case between the text
			// file
			// With the exclusion words and our lyrics
			String scanTemp = scan.nextLine();
			for (int i = 0; i < documents.size(); i++) {

				String lyrics0 = documents.get(i).getSongLyrics().replaceAll("[^A-Za-z0-9' ']", "");
				String lyrics1 = lyrics0.replaceAll("'", "");
				String lyrics2 = lyrics1.replaceAll("\\b" + scanTemp + "\\b", "");
				String lyrics3 = lyrics2.replaceAll(" +", " ");
				long songId = documents.get(i).getSongId();
				AlbumTopic modSong = new AlbumTopic(songId,lyrics3.toLowerCase());
				documents.set(i,modSong);

      
			}

			// System.out.println(documents.toString());
		}
		//System.out.println(documents.toString());
	}

	public void removeFunctionalWordsV2(ArrayList<AlbumTopic> documents) throws IOException {

		LyricsTokenizer lyricsTokenizer = LyricsTokenizer.fromFile();
		//System.out.format(" Document Size before removeal = %d\n", documents.size());
		
		for (int i = 0; i < documents.size(); i++) {

			String lyrics0 = documents.get(i).getSongLyrics().replaceAll("[^A-Za-z' ']", "");
			String lyrics1 = lyrics0.replaceAll("'", "");
			String lyrics2 = lyrics1.replaceAll(" +", " ");
			List<String> lyricWords = lyricsTokenizer.tokenize(lyrics2);

			String lyrics4 = String.join(" ",lyricWords);
			
			long songId = documents.get(i).getSongId();
			AlbumTopic modSong = new AlbumTopic(songId,lyrics4.toLowerCase());
			documents.set(i,modSong);
			//System.out.println(lyrics4);

  
		}

		//System.out.format(" Document Size after removeal = %d\n", documents.size());	
		
	}

	public List<SongTopic> getTopicsFromDisk() {

		List<SongTopic> topics = null;
		try {
			System.out.println("Working Directory = " +
					System.getProperty("user.dir"));

			File inputFile = new File(this.getClass().getClassLoader().getResource( "Topics.xml").getFile());
			SAXReader reader = new SAXReader();
			Document document = reader.read(inputFile);

			System.out.println("Root element :" + document.getRootElement().getName());

			List<Node> nodes = document.selectNodes("//Topics/topic");
			topics = new ArrayList<SongTopic>();

			Iterator iter = nodes.iterator();

			while (iter.hasNext()) {
				Element element = (Element) iter.next();
				SongTopic myTopic = new SongTopic();
				myTopic.setTopicName(element.selectSingleNode("name").getText());
				List<Element> wordList = element.selectNodes("word");

				for (int i = 0; i < wordList.size(); i++) {
					myTopic.addAssociatedTerm(wordList.get(i).getTextTrim());
				}

				topics.add(myTopic);

			}

		} catch (DocumentException e) {

			e.printStackTrace();

		} finally {

		}
		return topics;

	}
}

