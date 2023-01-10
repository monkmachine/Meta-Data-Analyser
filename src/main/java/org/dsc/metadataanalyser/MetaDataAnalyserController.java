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
import javafx.util.Duration;
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
    private Thread metaScrapeThread;
    @FXML
    private TableView<MetaData> tableView = new TableView<>();
    @FXML
    private TableColumn<MetaData, Number> count = new TableColumn<>("count");
    @FXML
    private TableColumn<MetaData, String> metaDataKey = new TableColumn<>("Value");
    @FXML
    private PieChart pie;
    @FXML
    ResultSet rs;
    private final ObservableList<MetaData> data = FXCollections.observableArrayList();
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
        jdbcDetails = new jdbcDetails(jdbcUrl.getText(),userId.getText(), password.getText(),dbDropDown.getSelectionModel().getSelectedItem());
        setConnectionTestLabel();
    }

    @FXML
    public void onPopulateData() {
        populateData();
    }

    @FXML
    protected void onRunProcess() {
        runTikaProcess();
    }

    public void killThread() {
        if(metaScrapeThread != null){
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

    public void populateData() {
        jdbcDetails = new jdbcDetails(jdbcUrl.getText(),userId.getText(), password.getText(),dbDropDown.getSelectionModel().getSelectedItem());
        dbCon.setJdbcDetails(jdbcDetails);
        onTestConnection();
        try {
            rs = dbCon.runSelectStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        setMetaTableView();
        MetaPieChart mpc = new MetaPieChart();
        try {
            rs.beforeFirst();
        } catch (SQLException e) {
            try {
                rs = dbCon.runSelectStatement();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            pie = mpc.setUpPieChart(rs, pie, pieData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMetaTableView() {
        count.setCellValueFactory(cellData -> cellData.getValue().getCountProperty());
        metaDataKey.setCellValueFactory(cellData -> cellData.getValue().metaDataKey);
        try {
            while (true) {
                try {
                    if (!rs.next()) break;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                data.add(new MetaData(rs.getInt("value"), rs.getString("text"), ""));

            }
            tableView.setItems(data);
            tableView.getItems().forEach(data -> {
                        Tooltip tooltip = new Tooltip();
                        Duration duration = Duration.millis(1);
                        tooltip.setShowDelay(duration);
                        tooltip.setText(data.getMetaDataKey());

                    }
            );
            System.out.println(tableView.getColumns());
            tableView.setOnMouseClicked((MouseEvent event) -> {
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    System.out.println(tableView.getSelectionModel().getSelectedItem());
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        jdbcDetails = new jdbcDetails(jdbcUrl.getText(),userId.getText(), password.getText(),dbDropDown.getSelectionModel().getSelectedItem());
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
            jdbcDetails = new jdbcDetails(jdbcUrl.getText(),userId.getText(), password.getText(),dbDropDown.getSelectionModel().getSelectedItem());
            dbCon.setJdbcDetails(jdbcDetails);
            try {
                dbCon.setDBConnection();
            } catch (SQLException e) {
                progressTxt.setText(e.getMessage());
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