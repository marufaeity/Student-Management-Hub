package studentmanger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.sql.*;

public class AttendanceController {

    @FXML private TableView<AttendanceRecord> attendanceTable;
    @FXML private TableColumn<AttendanceRecord, Integer> idCol;
    @FXML private TableColumn<AttendanceRecord, String> nameCol;
    @FXML private TableColumn<AttendanceRecord, Boolean> presentCol;

    @FXML private TableView<AttendanceHistory> historyTable;
    @FXML private TableColumn<AttendanceHistory, Integer> histIdCol;
    @FXML private TableColumn<AttendanceHistory, String> histNameCol;
    @FXML private TableColumn<AttendanceHistory, Boolean> histPresentCol;
    @FXML private TableColumn<AttendanceHistory, String> histDateCol;

    private final ObservableList<AttendanceRecord> attendanceData = FXCollections.observableArrayList();
    private final ObservableList<AttendanceHistory> historyData = FXCollections.observableArrayList();

    private Connection connection;

    public void initialize() {
        // Connect to database
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:student_system.db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS Attendance (" +
                    "id INTEGER, " +
                    "name TEXT, " +
                    "present BOOLEAN, " +
                    "date TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Enable editing on table
        attendanceTable.setEditable(true);
        presentCol.setEditable(true);

        // Set up attendance table
        idCol.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        presentCol.setCellValueFactory(data -> data.getValue().presentProperty());
        presentCol.setCellFactory(CheckBoxTableCell.forTableColumn(presentCol));

        for (officer student : FXMLDocumentController.getStudentListStatic()) {
            attendanceData.add(new AttendanceRecord(student.getId(), student.getName()));
        }

        attendanceTable.setItems(attendanceData);

        // Set up history table
        histIdCol.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        histNameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        histPresentCol.setCellValueFactory(data -> data.getValue().presentProperty());
        histPresentCol.setCellFactory(CheckBoxTableCell.forTableColumn(histPresentCol));
        histDateCol.setCellValueFactory(data -> data.getValue().dateProperty());

        historyTable.setItems(historyData);

        loadAttendanceHistory();
    }

    @FXML
    private void saveAttendance() {
        String insertSQL = "INSERT INTO Attendance (id, name, present, date) VALUES (?, ?, ?, ?)";
        String date = java.time.LocalDate.now().toString();

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            for (AttendanceRecord record : attendanceData) {
                preparedStatement.setInt(1, record.getId());
                preparedStatement.setString(2, record.getName());
                preparedStatement.setBoolean(3, record.isPresent());
                preparedStatement.setString(4, date);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Attendance saved to database!");
            alert.showAndWait();

            loadAttendanceHistory(); // Refresh the history view
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save attendance.");
            alert.showAndWait();
        }
    }

    private void loadAttendanceHistory() {
        historyData.clear();

        String query = "SELECT * FROM Attendance ORDER BY date DESC";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                boolean present = resultSet.getBoolean("present");
                String date = resultSet.getString("date");

                historyData.add(new AttendanceHistory(id, name, present, date));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
private void deleteAttendanceHistory() {
    AttendanceHistory selected = historyTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a record to delete.");
        alert.showAndWait();
        return;
    }

    String deleteSQL = "DELETE FROM Attendance WHERE id = ? AND date = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
        preparedStatement.setInt(1, selected.getId());
        preparedStatement.setString(2, selected.getDate());
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            historyData.remove(selected);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Attendance record deleted.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Record not found or already deleted.");
            alert.showAndWait();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete attendance record.");
        alert.showAndWait();
    }
}

}
