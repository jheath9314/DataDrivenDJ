
package org.DataDrivenDJ.TopicModeler;
import java.util.HashMap;

public class AlbumTopic {

	private long songId;
	private String songLyrics;
	private HashMap<String, Integer> topicWeight = new HashMap<String, Integer>();

	AlbumTopic(long songId, String songLyrics) {
		this.songId = songId;
		this.songLyrics = new String(songLyrics);
	}

	public void addTopicWeight(String topic, Integer weight) {

		this.topicWeight.put(topic,weight);
	}

	public long getSongId() {
		return this.songId;
	}
	public void setSongLyrics(String songLyrics) {
		this.songLyrics = songLyrics;
	}

	public String getSongLyrics() {
		return this.songLyrics;
	}
    
    public HashMap<String,Integer> getTopicWeight() {
		return this.topicWeight;
	}


}