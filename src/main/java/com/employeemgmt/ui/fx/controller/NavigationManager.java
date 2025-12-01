package com.employeemgmt.ui.fx.controller;

import java.net.URL;

import com.employeemgmt.model.Employee;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationManager {

    private static Stage stage;

    // Called once inside App.start()
    public static void init(Stage primary) {
        stage = primary;
        showMainMenu();
    }

    private static void go(String fxml) {
        try {
            URL view = NavigationManager.class.getClassLoader()
                  .getResource("com/employeemgmt/ui/fx/" + fxml);

            if (view == null) {
                System.err.println("FXML NOT FOUND → " + fxml);
                throw new NullPointerException("Missing FXML: " + fxml);
            }

            Parent root = FXMLLoader.load(view);   // <-- fixed static reference
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.err.println("NAVIGATION ERROR → " + fxml);
            e.printStackTrace();
        }
    }

    // ==============================
    // MAIN SCREENS
    // ==============================
    
    public static void showMainMenu()       { go("main_menu.fxml"); }
    public static void showEmployeeSearch() { go("employee_search.fxml"); }
    public static void showEmployeeDetail() { go("employee_detail.fxml"); }
    public static void showAddEmployee()    { go("employee_form.fxml"); }
    public static void showReports()        { go("reports.fxml"); }
    public static void showAdjust()         { go("salary_adjustment.fxml"); }

    // ==============================
    // METHODS YOUR CONTROLLERS CALL
    // ==============================

    public static void showSearch() { showEmployeeSearch(); }

    // "Edit employee" opens same form but later we preload fields
    public static void showEmployeeFormEdit(Employee emp) {
        // TODO: pass employee object through static holder or FXMLLoader controller injection
        showAddEmployee();
    }

    // ==============================
    // ERROR HANDLER
    // ==============================
    public static void error(String msg, Exception e) {
        System.err.println("\n❗ NAVIGATION ERROR: " + msg + "\n");
        if(e!=null) e.printStackTrace();
    }
}