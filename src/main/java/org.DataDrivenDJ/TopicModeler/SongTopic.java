package org.DataDrivenDJ.TopicModeler;
import java.util.ArrayList;
import java.util.List;

public class SongTopic {
	
	private String topicName = "";
	private List<String> associatedTerms = new ArrayList<String>();
	
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public List<String> getAssociatedTerms() {
		return associatedTerms;
	}
	public void setAssociatedTerms(List<String> associatedTerms) {
		this.associatedTerms = associatedTerms;
	}
	
	public void addAssociatedTerm(String termToAdd)
	{
		associatedTerms.add(termToAdd);
	}

}
