package com.roadmaptracker.service;

import com.roadmaptracker.model.LearningItem;
import com.roadmaptracker.model.Phase;
import com.roadmaptracker.model.Topic;

import java.util.ArrayList;
import java.util.List;

public class TrackerService {
    private List<Phase> phases =  new ArrayList<>();

    public TrackerService(){

    }

    public void addPhase(Phase phase){
        phases.add(phase);
    }

    public List<Phase> getPhases(){
        return phases;
    }

    public int getTotalItems(){
        int count = 0;
        for(Phase phase : phases){
            for(Topic topic : phase.getTopics()){
                count += topic.getItems().size();
            }
        }
        return count;
    }

    public int getCompletedItems(){
        int count = 0;
        for(Phase phase : phases){
            for(Topic topic : phase.getTopics()){
                for(LearningItem item : topic.getItems()){
                    if(item.getStatus().equals("Done")){
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
