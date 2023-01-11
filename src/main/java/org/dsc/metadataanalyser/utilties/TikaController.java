package org.dsc.metadataanalyser.utilties;

import java.io.File;
import java.io.IOException;


public class TikaController implements Runnable{

    private static TikaController instance;

    public Process getProcess() {
        return process;
    }

    private Process process;

    public void setCmd(String[] cmd) {
        this.cmd = cmd;
    }

    private String [] cmd;

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    private String directory;
    private TikaController(){}

    public static synchronized TikaController getInstance() {
        if (instance == null) {
            instance = new TikaController();
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            ProcessBuilder pb =new ProcessBuilder(cmd);
            pb.directory(new File(directory));
            process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        process.toHandle();


    }
}
