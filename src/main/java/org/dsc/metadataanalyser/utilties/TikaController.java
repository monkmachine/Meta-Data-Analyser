package org.dsc.metadataanalyser.utilties;

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
            process = new ProcessBuilder(cmd).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        process.toHandle();


    }
}
