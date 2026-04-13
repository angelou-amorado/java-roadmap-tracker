package com.roadmaptracker.model;

public class LearningItem {
    private String title;
    private String status;
    private String notes;
    private String dateCompleted;

    public LearningItem(String title, String status, String notes, String dateCompleted) {
        this.title = title;
        this.status = status;
        this.notes = notes;
        this.dateCompleted = dateCompleted;
    }

    public LearningItem() {

    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getDateCompleted() {
        return dateCompleted;
    }
    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }
}
