package org.dsc.metadataanalyser.dbProviders;

import java.sql.*;


public class PostgresDB {
    private String url;
    private final String user = "postgres";
    private final String password = "1";
    private Connection con;



    public void setUrl(String url) {
        this.url = url;
    }

    private static final String SQL_INSERT = "INSERT INTO \"MetaData\".\"MetaData\"(\"FileName\", \"MetaDataKey\", \"Value\")VALUES (?, ?, ?)";

    public void setCon() throws SQLException {

         con = DriverManager.getConnection(url, user, password);
        //System.out.println(url);
        this.con = DriverManager.getConnection(url, user, password);

    }
    public void runStatement (String file, String metaDataKey, String value) throws SQLException {
        PreparedStatement st = con.prepareStatement(SQL_INSERT);
        st.setString(1, file);
        st.setString(2, metaDataKey);
        st.setString(3, value.replaceAll("\u0000", ""));
        con.setAutoCommit(false);
        Statement stmt = con.createStatement();
        st.executeUpdate();
        stmt.close();
    }
    public ResultSet runStatement () throws SQLException {
        Statement  st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return st.executeQuery("select cast(value as Integer),text from (SELECT count(\"MetaDataKey\") as value,\"MetaDataKey\" as text FROM \"MetaData\".\"MetaData\" where \"Active\" IS null group by \"MetaDataKey\") t1 order by value desc limit 100");
    }
    public void pgCloseConection() throws SQLException {
        con.close();
    }
    public void commit() throws SQLException {
        con.commit();
    }
}