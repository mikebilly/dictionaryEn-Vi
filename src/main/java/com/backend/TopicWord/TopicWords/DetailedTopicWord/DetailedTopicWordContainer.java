package com.backend.TopicWord.TopicWords.DetailedTopicWord;

import java.util.ArrayList;

public class DetailedTopicWordContainer {
    private String topic;
    private ArrayList<DetailedTopicWord> words;

    public DetailedTopicWordContainer() {

    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ArrayList<DetailedTopicWord> getWords() {
        return words;
    }

    public void setWords(ArrayList<DetailedTopicWord> words) {
        this.words = words;
    }
}
