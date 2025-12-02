package com.employeemgmt.ui.fx.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import com.employeemgmt.ui.ReportRow;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;


public class ReportsController extends BaseController {

    @FXML private ComboBox<Integer> cmbYear;
    @FXML private ComboBox<Integer> cmbMonth;
    @FXML private TableView<Map.Entry<String, BigDecimal>> tblJob;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,String> jobNameColumn;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,BigDecimal> colJobTotal;

    @FXML private TableView<Map.Entry<String, BigDecimal>> tblDiv;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,String> colDivName;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,BigDecimal> colDivTotal;

    // Employee FTE + Pay history table
    @FXML private TableView<ReportRow> tblEmp;
    @FXML private TableColumn<ReportRow, String> colEmpName;
    @FXML private TableColumn<ReportRow, String> colEmpDivision;
    @FXML private TableColumn<ReportRow, String> colEmpJobTitle;
    @FXML private TableColumn<ReportRow, String> colEmpStart;
    @FXML private TableColumn<ReportRow, String> colEmpEnd;
    @FXML private TableColumn<ReportRow, Number> colEmpAmount;


    @FXML
    public void initialize(){
        int y = YearMonth.now().getYear();
        cmbYear.getItems().addAll(y-2,y-1,y,y+1);
        cmbMonth.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
        cmbYear.setValue(y);
        cmbMonth.setValue(YearMonth.now().getMonthValue());

        jobNameColumn.setCellValueFactory(v-> new javafx.beans.property.SimpleStringProperty(v.getValue().getKey()));
        colJobTotal.setCellValueFactory(v-> new javafx.beans.property.SimpleObjectProperty<>(v.getValue().getValue()));

        colDivName.setCellValueFactory(v-> new javafx.beans.property.SimpleStringProperty(v.getValue().getKey()));
        colDivTotal.setCellValueFactory(v-> new javafx.beans.property.SimpleObjectProperty<>(v.getValue().getValue()));

        // Employee FTE + pay history columns
        colEmpName.setCellValueFactory(v ->
                new SimpleStringProperty(v.getValue().getEmployeeName()));

        colEmpDivision.setCellValueFactory(v ->
                new SimpleStringProperty(v.getValue().getDivisionName()));

        colEmpJobTitle.setCellValueFactory(v ->
                new SimpleStringProperty(v.getValue().getJobTitle()));

        colEmpStart.setCellValueFactory(v -> {
            var date = v.getValue().getPayPeriodStart();
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });

        colEmpEnd.setCellValueFactory(v -> {
            var date = v.getValue().getPayPeriodEnd();
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });

        colEmpAmount.setCellValueFactory(v ->
                new SimpleObjectProperty<>(v.getValue().getAmount()));
    }

    @FXML
    private void onRun(){
        int y = cmbYear.getValue();
        int m = cmbMonth.getValue();

        try{
            var job = ServiceRegistry.reports().getTotalPayByJobTitle(y, m);
            var div = ServiceRegistry.reports().getTotalPayByDivision(y, m);
            var emp = ServiceRegistry.reports().getEmployeePayForMonth(y, m);

            tblJob.setItems(FXCollections.observableArrayList(job.entrySet()));
            tblDiv.setItems(FXCollections.observableArrayList(div.entrySet()));
            tblEmp.setItems(FXCollections.observableArrayList(emp));

        } catch(SQLException e){
            error("Report failed", e);
        }
    }

    @FXML
    private void onBack(){ NavigationManager.showMainMenu(); }
}