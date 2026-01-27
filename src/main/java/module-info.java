module org.dsc.metadataanalyser {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.core;
    requires java.net.http;

    opens org.dsc.metadataanalyser to javafx.fxml;

    exports org.dsc.metadataanalyser;
    exports org.dsc.metadataanalyser.components;
    exports org.dsc.metadataanalyser.dataClasses;

    opens org.dsc.metadataanalyser.components to javafx.fxml;
}