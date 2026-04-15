package com.roadmaptracker.ui;

import com.roadmaptracker.model.Phase;
import com.roadmaptracker.service.TrackerService;
import com.roadmaptracker.util.FileManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class MainApp extends Application {
    public static TrackerService trackerService = new TrackerService();

    @Override
    public void start(Stage primaryStage) {
        List<Phase> loaded = FileManager.loadData();
        for (Phase phase : loaded) {
            trackerService.addPhase(phase);
        }
        if(trackerService.getPhases().isEmpty()){
            // Seed data
        }
        MainView mainView = new MainView(primaryStage, trackerService);
        mainView.show();
    }

    @Override
    public void stop() {
        FileManager.saveData(trackerService.getPhases());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
