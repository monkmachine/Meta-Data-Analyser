package org.dsc.metadataanalyser.utilties;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dsc.metadataanalyser.dataClasses.jdbcDetails;
import org.dsc.metadataanalyser.dbProviders.PostgresDB;
import org.dsc.metadataanalyser.dbProviders.SQLLiteDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DBConnection {
    private String dBType;
    private final String[] supportedDbs = {"Postgres", "SQL Server", "Oracle", "SQLite"};
    private String user;
    private String password;
    private final ObservableList<String> dbDropDownOptions = FXCollections.observableArrayList(supportedDbs);
    private Connection con;
    private String jDBCUrl;
    private final PostgresDB pg = new PostgresDB();
    private final SQLLiteDB sqlLiteDB = new SQLLiteDB();

    final Map<String, String> jDBCUrls = Stream.of(new String[][]{
            {"Postgres", "jdbc:postgresql://localhost:5432/MetaData"},
            {"SQL Server", "jdbc:sqlserver://host:port;databaseName=xxxx"},
            {"Oracle", "jdbc:oracle:thin:@host:port:xxxx"},
            {"SQLite", "jdbc:sqlite:sample.db"},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    public String jDBCUrlFormat(String db) {
        return jDBCUrls.get(db);
    }

    public void setJdbcDetails(jdbcDetails jdbcDetails){
        this.jDBCUrl = jdbcDetails.getUrl();
        this.user = jdbcDetails.getUser();
        this.password = jdbcDetails.getPassword();
        this.dBType = jdbcDetails.getdBType();

    }
    public ObservableList<String> getDbDropDownOptions() {
        return dbDropDownOptions;
    }

    public void setDBConnection() throws SQLException {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Connection Failed to close");
            }
        }
        con = DriverManager.getConnection(jDBCUrl, user, password);
    }


    public void runStatement(String name, String key, String value) throws SQLException {
        if (Objects.equals(this.dBType, "Postgres")) {
            pg.runStatement(con, name, key, value);
        } else if (this.dBType.equals("SQLite")) {
            sqlLiteDB.runKeyAnalysisStatement(con, name, key, value);
        } else {
            throw new SQLException("");
        }

    }

    public ResultSet runKeyAnalysisStatement() throws SQLException {
        if (this.dBType.equals("Postgres")) {
            return pg.runStatement(con);
        } else if (this.dBType.equals("SQLite")) {
            return sqlLiteDB.runKeyAnalysisStatement(con);
        } else {
            throw new SQLException("");
        }
    }
    public ResultSet runDuplicatesStatement() throws SQLException {
        if (this.dBType.equals("Postgres")) {
            return pg.runStatement(con);
        } else if (this.dBType.equals("SQLite")) {
            return sqlLiteDB.runDuplicatesStatement(con);
        } else {
            throw new SQLException("");
        }
    }

    public void commit() throws SQLException {
        con.commit();
    }

    public void close() throws SQLException {
        if (con != null) {
            con.close();
        }

    }
}
