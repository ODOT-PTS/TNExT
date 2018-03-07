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

package com.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.URI;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class DatabaseConfig {
    private static TreeMap<Integer, DatabaseConfig> dbConfigs;
    private static String[] fields = "databaseIndex,dbnames,spatialConfigPaths,ConfigPaths,connectionURL,username,password,censusMappingSource,gtfsMappingSource1,gtfsMappingSource2".split(",");

    static {
        if (dbConfigs == null) { loadDefault(); }
    }

    // Class methods
    // ian todo: make private
    public static String getPath(String...args) {
        return Paths.get(getConfigurationDirectory(), args).toString();
    }

    public static String getConfigurationDirectory() {
        String path = System.getProperty("edu.oregonstate.tnatool.ConfigurationDirectory");
        if ( path != null) {
            path = Paths.get(path).toString();
            System.err.format("Configuration directory property: %s", path);
        } else {
            path = DatabaseConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            System.err.format("Configuration directory fallback: %s", path);
        }
        return path;
    }

    public static String getUploadDirectory() {
        return getPath("admin", "uploads");
    }

    public static String getDownloadDirectory() {
        return Paths.get("admin", "downloadables").toString();
        // return getPath("admin", "downloadables");
    }

    public static String getDbInfoCsvPath() {
        // To deprecate - direct access should not be allowed
        return getPath("admin", "resources", "dbInfo.csv");
    }

    public static void loadDefault() {
        System.err.println("DatabaseConfigs.loadDefault()");
        File csvfile = new File(getDbInfoCsvPath());
        loadFromCsv(csvfile);
    }

    public static void loadFromCsv(File csvfile) {
        // discard existing
        dbConfigs = new TreeMap<Integer, DatabaseConfig>();
        // load csv
        System.err.println("DatabaseConfig.loadFromCsvPath: " + csvfile.getPath());
        CSVReader reader = null;
        // todo: ian: load from map, ignore databaseIndex column?
        try {
            reader = new CSVReader(new FileReader(csvfile));
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (!tryParseInt(line[0])) { continue; }
                System.err.println("DatabaseConfig.loadFromCsvPath: read dbConfigs: " + line[0] + 
                        " dbName: " + line[1]
                        + " connectionUrl: " + line[4]);
                DatabaseConfig d = new DatabaseConfig(line);
                dbConfigs.put(d.getDatabaseIndex(), d);
            }    
        } catch (IOException e) {
            System.err.println("DatabaseConfig.loadFromCsv: Error reading file");
            e.printStackTrace();
        }
    }

    public static DatabaseConfig getConfig(int index) {
        return dbConfigs.get(index);
    }

    public static DatabaseConfig getConfig(String index) {
        return dbConfigs.get(Integer.parseInt(index));
    }

    public static DatabaseConfig[] getConfigs() {
        return dbConfigs.values().toArray(new DatabaseConfig[0]);
    }

    public static int getConfigSize() {
        return dbConfigs.size();
    }

    public static DatabaseConfig getLastConfig() {
        return dbConfigs.lastEntry().getValue();
    }

	public static void deactivateDb(int i){
		// String[] newElement;
		// for(Map.Entry<String, String[]> entry : infoMap.entrySet()) {
		// 	newElement = new String[entry.getValue().length-1];
		// 	for(int j=0;j<i;j++){
		// 		newElement[j] = entry.getValue()[j];
		// 	}
		// 	for(int j=i;j<entry.getValue().length-1;j++){
		// 		newElement[j] = entry.getValue()[j+1];
		// 	}
		// 	infoMap.put(entry.getKey(), newElement);
		// }
	}

    public static void activateDb() {
        // pass
    }

    private static boolean tryParseInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {}
        return false;
    }

    // Instance methods
    private Integer databaseIndex = -1;
    private String dbName = "";
    private String spatialConfigPath = "";
    private String configPath = "";
    private String connectionUrl = "";
    private String username = "";
    private String password = "";
    private String censusMappingSource = "";
    private String gtfsMappingSource1 = "";
    private String gtfsMappingSource2 = "";

    public DatabaseConfig() {
    }

    public DatabaseConfig(String[] row) {
        fromArray(row);
    }

    public String toString() {
        return "<db index: " + getDatabaseIndex().toString() + " name: " + getDbName() + " url: " + getConnectionUrl() + ">";
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

    public String[] toArray() {
        String[] row = new String[fields.length];
        HashMap<String, String> map = toMap();
        for (int i=0; i < fields.length; i++) {
            row[i] = map.get(fields[i]);
        }
        return row;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> m = new HashMap<String, String>();
        // "databaseIndex,dbnames,spatialConfigPaths,ConfigPaths,connectionURL,username,password,censusMappingSource,gtfsMappingSource1,gtfsMappingSource2".split(",");
        m.put("databaseIndex", getDatabaseIndex().toString());
        m.put("dbnames", getDbName());
        m.put("spatialConfigPaths", getSpatialConfigPath());
        m.put("ConfigPaths", getConfigPath());
        m.put("connectionURL", getConnectionUrl());
        m.put("username", getUsername());
        m.put("password", getPassword());
        m.put("censusMappingSource", getCensusMappingSource());
        m.put("gtfsMappingSource1", getGtfsMappingSource1());
        m.put("gtfsMappingSource2", getGtfsMappingSource2());
        return m;
    }

    public void fromMap(HashMap<String, String> m) {
        setDatabaseIndex(m.get("databaseIndex"));
        setDbName(m.get("dbnames"));
        setSpatialConfigPath(m.get("spatialConfigPaths"));
        setConfigPath(m.get("ConfigPaths"));
        setConnectionUrl(m.get("connectionURL"));
        setUsername(m.get("username"));
        setPassword(m.get("password"));
        setCensusMappingSource(m.get("censusMappingSource"));
        setGtfsMappingSource1(m.get("gtfsMappingSource1"));
        setGtfsMappingSource2(m.get("gtfsMappingSource2"));
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
    public Integer getDatabaseIndex() {
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

    public void setDatabaseIndex(Integer value) {
        this.databaseIndex = value;
    }
    public void setDatabaseIndex(String value) {
        setDatabaseIndex(Integer.parseInt(value));
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
