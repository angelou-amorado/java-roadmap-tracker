package com.roadmaptracker.util;

import com.roadmaptracker.model.Phase;
import com.roadmaptracker.model.Topic;
import com.roadmaptracker.model.LearningItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String FILE_PATH = "roadmap_data.txt";
    private static final String SEPARATOR = "|";

    public static void saveData(List<Phase> phases) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))){
            for (Phase phase : phases) {
                writer.write("PHASE" + SEPARATOR + phase.getTitle());
                writer.newLine();
                for (Topic topic : phase.getTopics()) {
                    writer.write("TOPIC" + SEPARATOR + topic.getTitle());
                    writer.newLine();
                    for (LearningItem li : topic.getItems()) {
                        writer.write(String.join(SEPARATOR,
                                "ITEM",
                                li.getTitle(),
                                li.getStatus(),
                                li.getNotes() == null ? "" : li.getNotes(),
                                li.getDateCompleted() == null ? "" : li.getDateCompleted()
                        ));
                        writer.newLine();
                    }
                }
            }
            System.out.println("Learning Items saved to " + FILE_PATH);
        }catch (IOException e){
            System.err.println("Error saving learning items: " + e.getMessage());
        }
    }
    public static List<Phase> loadData() {
        List<Phase> phases = new ArrayList<>();
        Phase currentPhase = null;
        Topic currentTopic = null;
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("No saved data found. Starting fresh.");
            return new ArrayList<>();
        }
        try(BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))){
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                    String[] parts = line.split("\\" + SEPARATOR);
                if (parts[0].equals("PHASE")) {
                    currentPhase = new Phase(parts[1]);
                    phases.add(currentPhase);
                } else if (parts[0].equals("TOPIC")) {
                    currentTopic = new Topic(parts[1]);
                    currentPhase.addTopic(currentTopic);
                } else if (parts[0].equals("ITEM")) {
                    LearningItem li = new LearningItem();
                    li.setTitle(parts[1]);
                    li.setStatus(parts[2]);
                    li.setNotes(parts[3]);
                    li.setDateCompleted(parts[4]);
                    currentTopic.addItem(li);
                }

            }
            System.out.println("Library loaded from " + FILE_PATH);
        }catch (IOException e){
            System.err.println("Error saving learning items: " + e.getMessage());
        }
        return phases;
    }
}
