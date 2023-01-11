package org.dsc.metadataanalyser.utilties;

import javafx.stage.DirectoryChooser;

import java.io.File;

public class FolderChooser {
    public File directoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        return directoryChooser.showDialog(null);
    }
}
