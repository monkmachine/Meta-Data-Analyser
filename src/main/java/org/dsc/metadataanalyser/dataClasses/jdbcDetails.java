package org.dsc.metadataanalyser.dataClasses;

public class jdbcDetails {
    private String url;

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    private String user ;

    public String getPassword() {
        return password;
    }

    private String password;

    public jdbcDetails(String url, String text1, String text2) {
        this.url = url;
        this.user = text1;
        this.password=text2;
    }
}
