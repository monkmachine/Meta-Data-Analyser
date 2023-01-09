package org.dsc.metadataanalyser.utilties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;

public class TikaProcessor {
    private final TikaRequest tr = new TikaRequest();
    private String file;

    public File[] getFileListToProcess() {
        File f = new File(this.file);
        return f.listFiles();

    }

    public void call(File file, DBConnection dbCon) throws SQLException {
        tr.setServiceUrl("http://localhost:9998/meta");
        tr.setUploadFile(file.getAbsolutePath());
        InputStream resp;

        try {
            resp = tr.makeTikaRequest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        JsonReader jsonRead;

            jsonRead = new JsonReader();

        Map<String, String> keyValPairs;
        try {
            keyValPairs = jsonRead.processMetaFile(resp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Map.Entry<String, String> entry : keyValPairs.entrySet()) {

            try {
                dbCon.runStatement(file.getName(), entry.getKey(), entry.getValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        dbCon.commit();
    }


    public void setFile(String file) {
        this.file = file;
    }
}

