package com.employeemgmt.ui.fx.controller;

import javafx.scene.control.Alert;

public abstract class BaseController {

    protected void info(String msg){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.show();
    }

    protected void error(String msg, Exception e){
        e.printStackTrace();
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg + "\n" + e.getMessage());
        a.show();
    }
}