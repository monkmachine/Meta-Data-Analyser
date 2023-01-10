package org.dsc.metadataanalyser.dbProviders;

import java.sql.*;

public class SQLLiteDB {
    private static final String SQL_INSERT = "INSERT INTO MetaData(\"FileName\", \"MetaDataKey\", \"Value\")VALUES (?, ?, ?)";
    private static final String KeyAnalysis ="select cast(value as Integer) as value,text from (SELECT count(MetaDataKey) as value,MetaDataKey as text FROM MetaData group by MetaDataKey)order by value desc limit 100";
    private static final String DuplicatesStatement = "select * from (SELECT count(Value) as \"count\", Value from MetaData where MetaDataKey = 'X-TIKA:digest:SHA256' GROUP by Value) where count > 1 order by \"count\" desc";
    public void runKeyAnalysisStatement(Connection con, String file, String metaDataKey, String value) throws SQLException {
        PreparedStatement st = con.prepareStatement(SQL_INSERT);
        st.setString(1, file);
        st.setString(2, metaDataKey);
        st.setString(3, value.replaceAll("\u0000", ""));
        con.setAutoCommit(false);
        Statement stmt = con.createStatement();
        st.executeUpdate();
        stmt.close();
    }
    public ResultSet runKeyAnalysisStatement(Connection con) throws SQLException {
        Statement  st = con.createStatement();
        return st.executeQuery(KeyAnalysis);
    }

    public ResultSet runDuplicatesStatement(Connection con) throws SQLException {
        Statement  st = con.createStatement();
        return st.executeQuery(DuplicatesStatement);
    }
}
