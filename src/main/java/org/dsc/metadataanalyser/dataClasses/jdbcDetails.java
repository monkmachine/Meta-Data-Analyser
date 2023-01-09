package org.dsc.metadataanalyser.dataClasses;

public class jdbcDetails {
    private final String url;
    private final String user ;
    private final String password;
    private final String dBType;
    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getdBType() {
        return dBType;
    }



    public jdbcDetails(String url, String user, String password, String dBType) {
        this.url = url;
        this.user = user;
        this.password=password;
        this.dBType =dBType;
    }
}
