// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Explorer Tool.
//
//    Transit Network Explorer Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU  General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Explorer Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Transit Network Explorer Tool.  If not, see <http://www.gnu.org/licenses/>.

package com.model.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.URI;
import java.net.URL;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
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

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class DatabaseConfig {
    final static Logger logger = Logger.getLogger(DatabaseConfig.class);    
    private static TreeMap<Integer, DatabaseConfig> dbConfigs;
    private static String[] fields = "databaseIndex,dbnames,spatialConfigPaths,ConfigPaths,connectionURL,username,password,censusMappingSource,gtfsMappingSource1,gtfsMappingSource2,defaultDate".split(",");
    static {
        try {
            loadDbInfo(); 
        } catch (IOException e) {
            logger.error("Fatal error, could not read dbInfo.csv", e);
        }
    }

    // Class methods

    // ian todo: make private
    public static String getPath(String...args) {
        String path = Paths.get(getConfigurationDirectory(), args).toString();
        logger.info(String.format("getPath: resolved -> %s", args, path));
        return path;
    }

    public static File getResource(String path) {
        URL url = DatabaseConfig.class.getClassLoader().getResource(path);
        File f = new File(url.getFile());
        return f;
    }

    public static String getDownloadDirectory() {
        return getPath("admin", "downloadables");
    }

    public static void loadDbInfo() throws IOException {
        File csvfile = new File(getDbInfoCsvPath());
        try { 
            loadFromCsv(csvfile);
        } catch (IOException e) {
            throw e;
        }
    }

    public static void saveDbInfo() throws IOException {
        File csvfile = new File(getDbInfoCsvPath());
        try {
            writeToCsv(csvfile);
        } catch (IOException e) { 
            throw e;
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

    public static String[] getFields() {
        return fields;
    }

    public static void updateConfig(DatabaseConfig dbConfig) {

    }

    public static void addConfig(DatabaseConfig dbConfig) {
        // ian: todo: validate
        int i = dbConfig.getDatabaseIndex();
        if (dbConfigs.containsKey(i)) {
            logger.warn(String.format("addConfig: index already exists: %s", i));
        } else {
            logger.info(String.format("addConfig: adding dbConfig: %s", dbConfig.toString()));
            dbConfigs.put(i, dbConfig);
        }
    }

    public static void removeConfig(String index) {
        removeConfig(Integer.parseInt(index));
    }

    public static void removeConfig(int index) {
        if (dbConfigs.containsKey(index)) { 
            dbConfigs.remove(index);
            logger.info(String.format("removeConfig: removing index: %s", index));
        } else {
            logger.warn(String.format("removeConfig: index does not exist: %s", index));
        }
    }

	public static void deactivateDb(int i) {
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

    /* private methods */

    private static String getDbInfoCsvPath() {
        // To deprecate - direct access should not be allowed
        return getPath("admin", "resources", "dbInfo.csv");
    }

    private static String getConfigurationDirectory() {
        String path = System.getProperty("edu.oregonstate.tnatool.ConfigurationDirectory");
        if ( path != null) {
            path = Paths.get(path).toString();
            logger.info(String.format("ConfigurationDirectory property: %s", path));
        } else {
            path = DatabaseConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            logger.info(String.format("ConfigurationDirectory fallback: %s", path));
        }
        return path;
    }

    private static void loadFromCsv(File csvfile) throws IOException {
        // discard existing
        dbConfigs = new TreeMap<Integer, DatabaseConfig>();
        // load csv
        logger.info(String.format("Reading dbInfo.csv: %s", csvfile.getPath()));
        CSVReader reader = null;
        // todo: ian: load from map, ignore databaseIndex column?
        try {
            reader = new CSVReader(new FileReader(csvfile));
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (!tryParseInt(line[0])) { continue; }
                DatabaseConfig dbConfig = new DatabaseConfig(line);
                logger.info(String.format("Read dbConfig: %s", dbConfig.toString()));
                dbConfigs.put(dbConfig.getDatabaseIndex(), dbConfig);
            }    
        } catch (IOException e) {
            logger.error("Error reading dbInfo.csv", e);
            throw e;
        }
    }

    private static void writeToCsv(File csvfile) throws IOException {
        logger.info(String.format("Writing dbInfo.csv: %s", csvfile.getPath()));
        BufferedWriter bw;
        try {
            FileWriter fw = new FileWriter(csvfile);
            bw = new BufferedWriter(fw);
            CSVWriter csvWriter = new CSVWriter(bw,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            logger.info(String.format("Writing header: %s", fields.toString()));
            csvWriter.writeNext(fields);
            for (DatabaseConfig db : getConfigs()) {
                logger.info(String.format("Writing index %s: %s", db.getDatabaseIndex(), db.toArray().toString()));
                csvWriter.writeNext(db.toArray());
            }
            bw.close();
        } catch (IOException e) {
            logger.error("Error writing dbInfo.csv", e);
            throw e;
        }
    }

    private static boolean tryParseInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {}
        return false;
    }

    /* ************************************************** */
    /* ************************************************** */
    /* ************************************************** */

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
    private String defaultDate = "";

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
            logger.error("PostgreSQL DataSource unable to load PostgreSQL JDBC Driver");
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
        setDefaultDate(row[10]);
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
        m.put("defaultDate", getDefaultDate());
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
        setDefaultDate(m.get("defaultDate"));
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

    public String getDefaultDate() {
        return defaultDate;
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

    public void setDefaultDate(String value) {
        this.defaultDate = value;
    }
}
