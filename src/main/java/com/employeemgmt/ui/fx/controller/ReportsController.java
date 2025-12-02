package com.employeemgmt.ui.fx.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import com.employeemgmt.ui.ReportRow;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import com.employeemgmt.model.Payroll;
import com.employeemgmt.model.Employee;
import com.employeemgmt.service.EmployeeService;
import com.employeemgmt.service.ReportService;




public class ReportsController extends BaseController {

    @FXML private ComboBox<Integer> cmbYear;
    @FXML private ComboBox<Integer> cmbMonth;
    @FXML private TableView<Map.Entry<String, BigDecimal>> tblJob;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,String> jobNameColumn;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,BigDecimal> colJobTotal;

    @FXML private TableView<Map.Entry<String, BigDecimal>> tblDiv;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,String> colDivName;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>,BigDecimal> colDivTotal;

    // === New fields for FTE Info + Pay History search ===
    @FXML private TextField txtEmpSearch;
    @FXML private Label lblEmpInfo;
    @FXML private TableView<Payroll> tblEmpHistory;
    @FXML private TableColumn<Payroll, String> colHistStart;
    @FXML private TableColumn<Payroll, String> colHistEnd;
    @FXML private TableColumn<Payroll, Number> colHistAmount;




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

        // === Employee pay history table columns ===
        colHistStart.setCellValueFactory(v -> {
            var d = v.getValue().getPayPeriodStart();
            return new SimpleStringProperty(d != null ? d.toString() : "");
        });

        colHistEnd.setCellValueFactory(v -> {
            var d = v.getValue().getPayPeriodEnd();
            return new SimpleStringProperty(d != null ? d.toString() : "");
        });

        colHistAmount.setCellValueFactory(v ->
                new SimpleObjectProperty<>(v.getValue().getAmount()));


    }

    @FXML
    private void onRun(){
        int y = cmbYear.getValue();
        int m = cmbMonth.getValue();

        try{
            var job = ServiceRegistry.reports().getTotalPayByJobTitle(y, m);
            var div = ServiceRegistry.reports().getTotalPayByDivision(y, m);

            tblJob.setItems(FXCollections.observableArrayList(job.entrySet()));
            tblDiv.setItems(FXCollections.observableArrayList(div.entrySet()));


        } catch(SQLException e){
            error("Report failed", e);
        }
    }

    @FXML
    private void onBack(){ NavigationManager.showMainMenu(); }

    @FXML
    private void onSearchEmployee() {
        String query = txtEmpSearch.getText();
        if (query == null || query.isBlank()) {
            info("Please enter ID, name, or SSN.");
            clearEmployeeHistory();
            return;
        }

        try {
            EmployeeService empService = ServiceRegistry.employees();
            Employee employee = null;
            String trimmed = query.trim();

            // If all digits, try ID first
            if (trimmed.matches("\\d+")) {
                int id = Integer.parseInt(trimmed);
                var optById = empService.findById(id);
                if (optById.isPresent()) {
                    employee = optById.get();
                } else if (trimmed.length() == 9) {
                    // If 9 digits and ID not found, try as SSN
                    var optBySsn = empService.findBySSN(trimmed);
                    if (optBySsn.isPresent()) {
                        employee = optBySsn.get();
                    }
                }
            }

            // Otherwise / still null → treat as name fragment
            if (employee == null) {
                var matches = empService.findByNameFragment(trimmed);
                if (matches.isEmpty()) {
                    info("No employee found for: " + trimmed);
                    clearEmployeeHistory();
                    return;
                }
                if (matches.size() > 1) {
                    info("More than one employee matches '" + trimmed +
                            "'. Please refine your search (include last name or use ID/SSN).");
                    clearEmployeeHistory();
                    return;
                }
                employee = matches.get(0);
            }

            ReportService reports = ServiceRegistry.reports();
            var history = reports.getPayHistoryForEmployee(employee.getEmployeeId());
            history.sort(Comparator.comparing(Payroll::getPayPeriodStart).reversed());

            lblEmpInfo.setText(
                    "ID: " + employee.getEmployeeId() +
                            "   Name: " + nullToEmpty(employee.getFirstName()) + " " + nullToEmpty(employee.getLastName()) +
                            "   SSN: " + nullToEmpty(employee.getSsn()) +
                            "   Email: " + nullToEmpty(employee.getEmail())
            );

            tblEmpHistory.setItems(FXCollections.observableArrayList(history));

        } catch (SQLException ex) {
            error("Search failed", ex);
            clearEmployeeHistory();
        }
    }

    private void clearEmployeeHistory() {
        if (lblEmpInfo != null) {
            lblEmpInfo.setText("No employee selected.");
        }
        if (tblEmpHistory != null) {
            tblEmpHistory.getItems().clear();
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }


}