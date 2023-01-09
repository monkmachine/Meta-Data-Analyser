package org.dsc.metadataanalyser.utilties;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dsc.metadataanalyser.dbProviders.PostgresDB;
import org.dsc.metadataanalyser.dbProviders.SQLLiteDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DBConnection {
    private String dBType;
    private final String[] supportedDbs = {"Postgres", "SQL Server", "Oracle", "SQLite"};
    private String user = "postgres";
    private String password = "1";
    private final ObservableList<String> dbDropDownOptions = FXCollections.observableArrayList(supportedDbs);
    private Connection con;
    private String jDBCUrl;
    private PostgresDB pg = new PostgresDB();
    private SQLLiteDB sqlLiteDB = new SQLLiteDB();

    Map<String, String> jDBCUrls = Stream.of(new String[][]{
            {"Postgres", "jdbc:postgresql://localhost:5432/MetaData"},
            {"SQL Server", "jdbc:sqlserver://host:port;databaseName=xxxx"},
            {"Oracle", "jdbc:oracle:thin:@host:port:xxxx"},
            {"SQLite", "jdbc:sqlite:sample.db"},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    public DBConnection() {
    }

    public String jDBCUrlFormat(String db) {
        return jDBCUrls.get(db);
    }

    public void setJDBCUrl(String jDBCUrl) {
        this.jDBCUrl = jDBCUrl;
    }

    public String getDbType() {
        return dBType;
    }

    public void setDbType(String dBType) {
        if (inSupportedDbs(dBType)) {
            this.dBType = dBType;
        }
    }

    public ObservableList<String> getDbDropDownOptions() {
        return dbDropDownOptions;
    }

    private boolean inSupportedDbs(String dBType) {
        return Arrays.asList(supportedDbs).contains(dBType);
    }


    public void setDBConnection() throws SQLException {
        if (con == null) {
            con = DriverManager.getConnection(jDBCUrl, user, password);
        } else {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Connection Failed to close");
            }
            con = DriverManager.getConnection(jDBCUrl, user, password);
        }
    }


    public void runStatement(String name, String key, String value) throws SQLException {
        if (this.dBType == "Postgres") {
            pg.runStatement(con, name, key, value);
        } else if (this.dBType == "SQLite") {
            sqlLiteDB.runStatement(con, name, key, value);
        } else {
            throw new SQLException("");
        }

    }

    public ResultSet runSelectStatement() throws SQLException {
        if (this.dBType == "Postgres") {
            return pg.runStatement(con);
        } else if (this.dBType == "SQLite") {
            return sqlLiteDB.runStatement(con);
        } else {
            throw new SQLException("");
        }
    }

    public void commit() throws SQLException {
        con.commit();
    }

    public void close() throws SQLException {
        con.close();
    }
}
