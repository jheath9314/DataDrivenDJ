package org.DataDrivenDJ.TopicModeler;
import java.util.HashMap;

public class TokenGenerator {
	
	private Integer vocabSize;
	private HashMap<String, Integer> tokenMap = new HashMap<String, Integer>();
	private HashMap<Integer, String> wordMap = new HashMap<Integer, String>();
	private Integer count = 0;

	TokenGenerator(Integer vocabSize) {
		this.vocabSize = vocabSize;
	}

	public Integer generateToken(String in) {
		if (tokenMap.containsKey(in)) {
			return tokenMap.get(in);
		} else {
			tokenMap.put(in, count);
			wordMap.put(count, in);
		}

		return count++;
	}
	
	public int getCurrentVocabCount()
	{
		return count;
	}
	
	public String getStringFromToken(int token)
	{
		return wordMap.get(token);
	}

}

