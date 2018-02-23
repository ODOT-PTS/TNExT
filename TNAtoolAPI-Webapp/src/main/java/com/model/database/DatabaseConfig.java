// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Analysis Software Tool.
//
//    Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU  General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.

// Creates a static hashmap of all the databases' required connection information from dbinfo.csv

package com.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.URI;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class DatabaseConfig {
    // Class methods
    private static String lastPath;
    private static HashMap<String, DatabaseConfig> dbIndex;

    public static String ConfigurationDirectory() {
        return System.getProperty("edu.oregonstate.tnatool.ConfigurationDirectory");
    }

    public static void loadDefault() {
        String path = ConfigurationDirectory() + "/admin/resources/dbInfo.csv";
        loadFromCsvPath(path);
    }

    public static void loadFromCsvPath(String path) {
        // discard existing
        dbIndex = new HashMap<String, DatabaseConfig>();
        // load csv
        System.out.println("DatabaseConfig.loadFromCsvPath: " + path);
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(path));
            String[] line;
            while ((line = reader.readNext()) != null) {
                System.out.println("DatabaseConfig.loadFromCsvPath: read dbIndex: " + line[0] + " dbName: " + line[1]
                        + " connectionUrl: " + line[4]);
                DatabaseConfig d = new DatabaseConfig(line);
                dbIndex.put(d.getDatabaseIndex(), d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Save last path
        lastPath = path;
    }

    public static void reload() {
        loadFromCsvPath(lastPath);
    }

    public static DatabaseConfig getConfig(int index) {
        return getConfig(Integer.toString(index));
    }

    public static DatabaseConfig getConfig(String index) {
        if (lastPath == null) { loadDefault(); }
        return dbIndex.get(index);
    }

    // Instance methods
    private String databaseIndex;
    private String dbName;
    private String spatialConfigPath;
    private String configPath;
    private String connectionUrl;
    private String username;
    private String password;
    private String censusMappingSource;
    private String gtfsMappingSource1;
    private String gtfsMappingSource2;

    public DatabaseConfig() {
    }

    public DatabaseConfig(String[] row) {
        fromArray(row);
    }

    public String toString() {
        return "<db index: " + getDatabaseIndex() + " name: " + getDbName() + " url: " + getConnectionUrl() + ">";
    }

    // Database connections
	public Connection getConnection() {
        Connection response = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL DataSource unable to load PostgreSQL JDBC Driver");
        }
        try {
            response = DriverManager.getConnection(getConnectionUrl(), getUsername(), getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }
    
    // to/from CSV
    public String[] toArray() {
        // backwards compat
        String[] row = { getDatabaseIndex(), getDbName(), getSpatialConfigPath(), getConfigPath(), getConnectionUrl(),
                getUsername(), getPassword(), getCensusMappingSource(), getGtfsMappingSource1(),
                getGtfsMappingSource2() };
        return row;
    }

    public void fromArray(String[] row) {
        setDatabaseIndex(row[0]);
        setDbName(row[1]);
        setSpatialConfigPath(row[2]);
        setConfigPath(row[3]);
        setConnectionUrl(row[4]);
        setUsername(row[5]);
        setPassword(row[6]);
        setCensusMappingSource(row[7]);
        setGtfsMappingSource1(row[8]);
        setGtfsMappingSource2(row[9]);
    }

    // For external clients
    public String getHost() {
        // jdbc:postgresql://db:5432/winter16
        URI uri = URI.create(getConnectionUrl().replace("jdbc:",""));
        return uri.getHost();
    }

    public String getPort() {
        URI uri = URI.create(getConnectionUrl().replace("jdbc:",""));
        return Integer.toString(uri.getPort());
    }
    
    public String getDatabase() {
        URI uri = URI.create(getConnectionUrl().replace("jdbc:",""));
        return uri.getPath().substring(1);        
    }

    // Getters
    public String getDatabaseIndex() {
        return databaseIndex;
    }

    public String getDbName() {
        return dbName;
    }

    public String getSpatialConfigPath() {
        return spatialConfigPath;
    }

    public String getConfigPath() {
        return configPath;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCensusMappingSource() {
        return censusMappingSource;
    }

    public String getGtfsMappingSource1() {
        return gtfsMappingSource1;
    }

    public String getGtfsMappingSource2() {
        return gtfsMappingSource2;
    }

    public void setDatabaseIndex(String value) {
        this.databaseIndex = value;
    }

    public void setDbName(String value) {
        this.dbName = value;
    }

    public void setSpatialConfigPath(String value) {
        this.spatialConfigPath = value;
    }

    public void setConfigPath(String value) {
        this.configPath = value;
    }

    public void setConnectionUrl(String value) {
        this.connectionUrl = value;
    }

    public void setUsername(String value) {
        this.username = value;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    public void setCensusMappingSource(String value) {
        this.censusMappingSource = value;
    }

    public void setGtfsMappingSource1(String value) {
        this.gtfsMappingSource1 = value;
    }

    public void setGtfsMappingSource2(String value) {
        this.gtfsMappingSource2 = value;
    }
}
