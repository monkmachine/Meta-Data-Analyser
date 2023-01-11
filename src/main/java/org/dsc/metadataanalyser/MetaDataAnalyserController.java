package org.dsc.metadataanalyser;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.dsc.metadataanalyser.components.MetaPieChart;
import org.dsc.metadataanalyser.dataClasses.MetaData;
import org.dsc.metadataanalyser.dataClasses.jdbcDetails;
import org.dsc.metadataanalyser.utilties.*;

import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class MetaDataAnalyserController implements Initializable {
    @FXML
    public CheckBox dataDeleteChkBox;
    @FXML
    public PasswordField password;
    @FXML
    public TextField jdbcUrl;
    @FXML
    public TextField userId;
    @FXML
    public Button testConnection;
    @FXML
    public Label connectionTest;
    @FXML
    public Label progressTxt;
    @FXML
    public Label inSelectedFolder;
    @FXML
    public Label outSelectedFolder;
    @FXML
    public Button runProcess;
    public Button stopProcess;
    @FXML
    ComboBox<String> dbDropDown;
    @FXML
    private ProgressBar bar;
    @FXML
    private TableView<MetaData> dupesDrillTableView = new TableView<>();
    @FXML
    private TableView<MetaData> keysCountTableView = new TableView<>();
    @FXML
    private TableView<MetaData> dupesTableView = new TableView<>();
    @FXML
    private TableView<MetaData> keysValueDrillTableView = new TableView<>();
    @FXML
    private TableColumn<MetaData, String> valuesDrillCol = new TableColumn<>("Value");
    @FXML
    private TableColumn<MetaData, String> KeyDrillCol = new TableColumn<>("MetaDataKey");
    @FXML
    private TableColumn<MetaData, Number> countCol = new TableColumn<>("count");
    @FXML
    private TableColumn<MetaData, String> metaKeyCol = new TableColumn<>("Value");
    @FXML
    private TableColumn<MetaData, Number> dupesCountCol = new TableColumn<>("count");
    @FXML
    private TableColumn<MetaData, String> dupesValueCol = new TableColumn<>("Value");
    @FXML
    private TableColumn<MetaData, String> dupesFileNameCol = new TableColumn<>("FileName");
    @FXML
    private PieChart keysPieChart;
    @FXML
    ResultSet rs;
    private Thread metaScrapeThread;
    private final ObservableList<MetaData> data = FXCollections.observableArrayList();
    private final ObservableList<MetaData> duplicatesData = FXCollections.observableArrayList();
    private final ObservableList<MetaData> duplicatesFileNamesdata = FXCollections.observableArrayList();
    private final ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
    private final DBConnection dbCon = new DBConnection();
    private final FolderChooser fc = new FolderChooser();
    private jdbcDetails jdbcDetails;

    public void initialize(URL location, ResourceBundle resources) {
        dbDropDown.setItems(dbCon.getDbDropDownOptions());
        dbDropDown.getSelectionModel().selectFirst();
        jdbcUrl.setText(dbCon.jDBCUrlFormat(dbDropDown.getSelectionModel().getSelectedItem()));
    }

    @FXML
    protected void onInSelectedFolderButtonClick() {
        setSelectedFolder("in");
    }

    @FXML
    protected void onOutSelectedFolderButtonClick() {
        setSelectedFolder("out");
    }

    @FXML
    public void onChangeDbDropDown() {
        jdbcUrl.setText(dbCon.jDBCUrlFormat(dbDropDown.getSelectionModel().getSelectedItem()));
    }

    @FXML
    public void onTestConnection() {
        jdbcDetails = new jdbcDetails(jdbcUrl.getText(), userId.getText(), password.getText(), dbDropDown.getSelectionModel().getSelectedItem());
        setConnectionTestLabel();
    }

    @FXML
    public void onPopulateData() throws SQLException {
        populateData();
    }

    @FXML
    protected void onRunProcess() {
        runTikaProcess();
    }

    public void killThread() {
        if (metaScrapeThread != null) {
            metaScrapeThread.interrupt();
            bar.progressProperty().unbind();
            bar.progressProperty().set(0);
            try {
                dbCon.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            progressTxt.setText("Process Stopped");
        }
    }

    public void populateData() throws SQLException {
        jdbcDetails = new jdbcDetails(jdbcUrl.getText(), userId.getText(), password.getText(), dbDropDown.getSelectionModel().getSelectedItem());
        dbCon.setJdbcDetails(jdbcDetails);
        onTestConnection();
        setKeysCountTableView();
        setKeysPieChart();
        setDupesTableView();
    }

    private void setKeysCountTableView() throws SQLException {
        data.clear();
        rs = dbCon.runKeyAnalysisStatement();
        countCol.setCellValueFactory(cellData -> cellData.getValue().getCountProperty());
        metaKeyCol.setCellValueFactory(cellData -> cellData.getValue().metaDataKey);
        while (rs.next()) {
            data.add(new MetaData(rs.getInt("value"), rs.getString("text"), "", "", "", "", ""));
        }
        keysCountTableView.setItems(data);
        keysCountTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (keysCountTableView.getSelectionModel().getSelectedItem().getMetaDataKey() != null) {
                    try {
                        setKeysValueDrillTableView(keysCountTableView.getSelectionModel().getSelectedItem().getMetaDataKey());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void setDupesTableView() throws SQLException {
        duplicatesData.clear();
        rs = dbCon.runDuplicatesStatement();
        dupesCountCol.setCellValueFactory(cellData -> cellData.getValue().getCountProperty());
        dupesValueCol.setCellValueFactory(cellData -> cellData.getValue().metaDataKey);
        while (rs.next()) {
            duplicatesData.add(new MetaData(rs.getInt("count"), rs.getString("value"), "", "", "", "", ""));
        }
        dupesTableView.setItems(duplicatesData);
        dupesTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (dupesTableView.getSelectionModel().getSelectedItem().getMetaDataKey() != null) {
                    setDupesDrillTableView(dupesTableView.getSelectionModel().getSelectedItem().getMetaDataKey());
                }
            }
        });
    }

    private void setDupesDrillTableView(String metaDataKey) {
        duplicatesFileNamesdata.clear();
        try {
            rs = dbCon.runDuplicatesDrillStatement(metaDataKey);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        dupesFileNameCol.setCellValueFactory(cellData -> cellData.getValue().fileName);
        try {
            while (true) {
                try {
                    if (!rs.next()) break;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                duplicatesFileNamesdata.add(new MetaData(0, "", "", rs.getString("FileName"), "", "", ""));
            }
            dupesDrillTableView.setItems(duplicatesFileNamesdata);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        dupesDrillTableView.setVisible(true);
    }

    private void setKeysValueDrillTableView(String metaDataKey) throws SQLException {
        duplicatesFileNamesdata.clear();
        try {
            rs = dbCon.runKeysDrilldownStatement(metaDataKey);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        valuesDrillCol.setCellValueFactory(cellData -> cellData.getValue().value);
        KeyDrillCol.setCellValueFactory(cellData -> cellData.getValue().metaDataKey);
        while (rs.next()) {
            duplicatesFileNamesdata.add(new MetaData(0, rs.getString("MetaDataKey"), rs.getString("Value"), "", "", "", ""));
        }
        keysValueDrillTableView.setItems(duplicatesFileNamesdata);
        keysValueDrillTableView.setVisible(true);
    }

    private void setKeysPieChart() throws SQLException {
        MetaPieChart mpc = new MetaPieChart();
        try {
            rs.beforeFirst();
        } catch (SQLException e) {
            rs = dbCon.runKeyAnalysisStatement();
        }
        keysPieChart = mpc.setUpPieChart(rs, keysPieChart, pieData);
    }

    protected void setSelectedFolder(String inOutFolder) {
        File file = fc.directoryChooser();
        setFolderLabel(file, inOutFolder);
    }

    protected void setFolderLabel(File file, String inOutFolder) {
        if (file == null && Objects.equals(inOutFolder, "in")) {
            inSelectedFolder.setText(null);
        } else if (file == null && Objects.equals(inOutFolder, "out")) {
            outSelectedFolder.setText(null);
        } else if (Objects.equals(inOutFolder, "in")) {
            assert file != null;
            inSelectedFolder.setText(file.getAbsolutePath());
        } else {
            assert file != null;
            outSelectedFolder.setText(file.getAbsolutePath());
        }
    }

    protected void setConnectionTestLabel() {
        jdbcDetails = new jdbcDetails(jdbcUrl.getText(), userId.getText(), password.getText(), dbDropDown.getSelectionModel().getSelectedItem());
        dbCon.setJdbcDetails(jdbcDetails);
        try {
            dbCon.setDBConnection();
            connectionTest.setTextFill(Color.rgb(45, 189, 45));
            connectionTest.setText("Success");
        } catch (SQLException e) {
            connectionTest.setTextFill(Color.rgb(163, 42, 42));
            connectionTest.setText(e.getMessage());
        }
        connectionTest.setWrapText(true);
        connectionTest.setStyle("-fx-font-weight: bold;");

    }

    void runTikaProcess() {
        Task<Void> metaTask = new processFolder();
        metaScrapeThread = new Thread(metaTask);
        metaScrapeThread.setDaemon(true);
        bar.progressProperty().bind(metaTask.progressProperty());
        metaScrapeThread.start();

    }

    //TODO this needs refactoring
    class processFolder extends Task<Void> {
        TikaProcessor tp = new TikaProcessor();


        protected Void call() throws Exception {

            tp.setFile(inSelectedFolder.getText());
            File[] pathNames = tp.getFileListToProcess();
            jdbcDetails = new jdbcDetails(jdbcUrl.getText(), userId.getText(), password.getText(), dbDropDown.getSelectionModel().getSelectedItem());
            dbCon.setJdbcDetails(jdbcDetails);
            try {
                dbCon.setDBConnection();
            } catch (SQLException e) {
                progressTxt.setText(e.getMessage());
            }
            if (dataDeleteChkBox.isSelected()) {
                try {
                    dbCon.clearOutDatabase();
                } catch (SQLException e) {
                    progressTxt.setText(e.getMessage());
                }
            }
            int i = 0;
            if (pathNames != null) {
                for (File file : pathNames) {
                    i = i + 1;
                    tp.call(file, dbCon);
                    updateProgress(i, pathNames.length);
                    String progressTxtVal = "Files Processed  " + i + "/" + pathNames.length;
                    Platform.runLater(() -> progressTxt.setText(progressTxtVal));
                }
            }
            dbCon.close();
            Platform.runLater(() -> {
                if (pathNames != null) {
                    Platform.runLater(() -> progressTxt.setText("Process complete " + pathNames.length + " files were processed"));
                }
            });
            return null;
        }
    }


}