package org.dsc.metadataanalyser.dbProviders;

import java.sql.*;

public class PostgresDB {

    private static final String SQL_INSERT = "INSERT INTO \"MetaData\".\"MetaData\"(\"FileName\", \"MetaDataKey\", \"Value\")VALUES (?, ?, ?)";
    private static final String KEY_ANALYSIS = "select cast(value as Integer),text from (SELECT count(\"MetaDataKey\") as value,\"MetaDataKey\" as text FROM \"MetaData\".\"MetaData\" where \"Active\" IS null group by \"MetaDataKey\") t1 order by value desc limit 100";
    private static final String DUPLICATES_STATEMENT = "select * from (SELECT count(\"Value\") as \"count\", \"Value\" from \"MetaData\".\"MetaData\" where \"MetaDataKey\" = 'X-TIKA:digest:SHA256' GROUP by \"Value\") t1 where \"count\" > 1 order by \"count\" desc";
    private static final String DUPLICATES_DRILL_STATEMENT = "SELECT \"FileName\" FROM \"MetaData\".\"MetaData\" WHERE \"Value\" = ?";
    private static final String KEYS_DRILL_STATEMENT = "SELECT DISTINCT (\"Value\"), \"MetaDataKey\" from \"MetaData\".\"MetaData\" where \"MetaDataKey\" = ?";
    private static final String CLEAR_DATABASE = "delete from \"MetaData\".\"MetaData\"";

    public void runStatement(Connection con, String file, String metaDataKey, String value) throws SQLException {
        PreparedStatement st = con.prepareStatement(SQL_INSERT);
        st.setString(1, file);
        st.setString(2, metaDataKey);
        st.setString(3, value.replaceAll("\u0000", ""));
        con.setAutoCommit(false);
        Statement stmt = con.createStatement();
        st.executeUpdate();
        stmt.close();
    }

    public ResultSet runStatement(Connection con) throws SQLException {
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return st.executeQuery(KEY_ANALYSIS);
    }

    public ResultSet runDuplicatesStatement(Connection con) throws SQLException {
        Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return st.executeQuery(DUPLICATES_STATEMENT);
    }

    public ResultSet runDuplicatesDrillStatement(Connection con, String metaDataKey) throws SQLException {
        PreparedStatement st = con
                .prepareStatement(DUPLICATES_DRILL_STATEMENT);
        st.setString(1, metaDataKey);
        return st.executeQuery();
    }

    public ResultSet runKeysDrilldownStatement(Connection con, String metaDataKey) throws SQLException {
        PreparedStatement st = con.prepareStatement(
                KEYS_DRILL_STATEMENT);
        st.setString(1, metaDataKey);
        return st.executeQuery();
    }

    public void clearOutDatabase(Connection con) throws SQLException {
        PreparedStatement st = con.prepareStatement(CLEAR_DATABASE);
        con.setAutoCommit(true);
        Statement stmt = con.createStatement();
        st.executeUpdate();
        stmt.close();
    }

}