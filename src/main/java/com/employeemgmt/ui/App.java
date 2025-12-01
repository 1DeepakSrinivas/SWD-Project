package com.employeemgmt.ui;

import com.employeemgmt.ui.fx.controller.NavigationManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        NavigationManager.init(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
