package com.roadmaptracker.model;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    private String title;
    private List<LearningItem> items;

    public Topic(String title) {
        this.title = title;
        this.items = new ArrayList<>();
    }

    public Topic(){

    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<LearningItem> getItems() {
        return items;
    }
    public void setItems(List<LearningItem> items) {
        this.items = items;
    }

    public void addItem(LearningItem item) {
        items.add(item);
    }
}
