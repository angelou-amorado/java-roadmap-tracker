package com.roadmaptracker.model;

import java.util.ArrayList;
import java.util.List;

public class Phase {
    private String title;
    private List<Topic> topics;

    public Phase(String title) {
        this.title = title;
        this.topics = new ArrayList<>();
    }

    public Phase(){

    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<Topic> getTopics() {
        return topics;
    }
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void addTopic(Topic topic) {
        this.topics.add(topic);
    }
}
