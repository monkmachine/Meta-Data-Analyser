package org.dsc.metadataanalyser.components;

import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MetaPieChart {


    public PieChart setUpPieChart(ResultSet rs, PieChart pie, ObservableList<PieChart.Data> pieData) throws SQLException {

        rs.beforeFirst();

        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            pieData.add(new PieChart.Data(rs.getString("text"), rs.getInt("value")));
        }
        pie.setData(pieData);
        pie.getData().forEach(data -> {
            Tooltip tooltip = new Tooltip();
            Duration duration = Duration.millis(1);
            tooltip.setShowDelay(duration);
            tooltip.setText(String.valueOf(data.getPieValue()));
            Tooltip.install(data.getNode(), tooltip);
            data.pieValueProperty().addListener((observable, oldValue, newValue) ->
                    tooltip.setText(newValue.toString()));
        });
        pie.setLegendVisible(false);
        return pie;


    }
}
