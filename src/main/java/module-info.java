module org.dsc.metadataanalyser {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.core;
    requires java.net.http;


    opens org.dsc.metadataanalyser to javafx.fxml;
    exports org.dsc.metadataanalyser;
    exports org.dsc.metadataanalyser.components;
    opens org.dsc.metadataanalyser.components to javafx.fxml;
}