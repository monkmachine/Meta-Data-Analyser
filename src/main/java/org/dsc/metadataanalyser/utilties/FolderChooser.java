package org.dsc.metadataanalyser.utilties;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

public class FolderChooser {
    private File selectedFile;
    public File directoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        Window mainStage = null;
        return directoryChooser.showDialog(null);
    }
}
