package org.dsc.metadataanalyser.components;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ResultSetTableView extends TableView {

    private final ResultSet resultSet;

    private final List<String> columnNames = new ArrayList<>();

    public ResultSetTableView(ResultSet rs) throws SQLException {
        super();
        this.resultSet = rs;

        buildData();
    }

    private void buildData() throws SQLException {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {

            final int j = i;
            TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
            //noinspection unchecked
            col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> {
                if (param.getValue().get(j) != null) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                } else {
                    return null;
                }
            });

            //noinspection unchecked
            getColumns().addAll(col);
            this.columnNames.add(col.getText());
        }

        while (resultSet.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(resultSet.getString(i));
            }
            data.add(row);

        }

        //FINALLY ADDED TO TableView
        //noinspection unchecked
        setItems(data);
    }

}