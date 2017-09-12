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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.iterators.EntrySetMapIterator;

public class Databases {
	public static HashMap<String, String[]> infoMap = getDbInfo();

	// public static String path;
    
    public static String ConfigurationDirectory()   {
        System.err.format(
                "Loading configuration directory from property edu.oregonstate.tnatool.ConfigurationDirectory: %s, "
                +  "or falling back to getProtectionDomain().getCodeSource().getLocation().getPath() + ../../src/main/resources/ %s\n", 
                System.getProperty("edu.oregonstate.tnatool.ConfigurationDirectory"),
                Databases.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        return
            ( System.getProperty("edu.oregonstate.tnatool.ConfigurationDirectory") != null ) 
            ? System.getProperty("edu.oregonstate.tnatool.ConfigurationDirectory") + '/'
            : Databases.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "../../src/main/resources/";
    }

    public static String dbInfoCsvPath() { 
        return ConfigurationDirectory() + "/admin/resources/dbInfo.csv"; 
    }

	public static HashMap<String, String[]> getDbInfo() {

		HashMap<String, String[]> infoMap = new HashMap<String, String[]>();

		try {
			//path = Databases.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            // Ed 2017-09-12
            //
            // FIXME. this is set in getDbInfo(), but other methods may want
            // access to this configuration directory.
            //
            // So this code only works if the users of this class call
            // getDbInfo() before calling other methods which may use the
            // configuration directory.
            //
            // It's probably reasonable to add this code instead to the static { } block for this class.
            //
            //
            System.err.format("ConfigurationDirectory(): %s\n", ConfigurationDirectory());
            System.err.format("dbInfoCsvPath(): %s\n",          dbInfoCsvPath());

            // Ed 2017-09-12 test setting properties from Tomcat configuration.
            System.err.format(
                    "Testing property from catlina.properties file, edu.oregonstate.tnatool.ConfigurationDirectory: %s\n", 
                    System.getProperty("edu.oregonstate.tnatool.ConfigurationDirectory"));
            System.err.format(
                    "Testing property from catlina.properties file, edu.oregonstate.tnatool.NoneSuch: %s\n", 
                    System.getProperty("edu.oregonstate.tnatool.NoneSuch"));

            // System.err.println("Attempting to load dbInfo.csv from: " + ConfigurationDirectory() + "/admin/resources/dbInfo.csv");
            System.err.println("Attempting to load dbInfo.csv from: " + dbInfoCsvPath() );
            //System.err.println("Attempting to load dbInfo.csv from: " + path + "../../src/main/resources/admin/resources/dbInfo.csv");

			BufferedReader reader = null;

            reader = new BufferedReader(new FileReader(dbInfoCsvPath()));
            
            String[] keys = reader.readLine().trim().split(",");
			ArrayList<String[]> elem = new ArrayList<String[]>();
			String line = reader.readLine();
			while (line != null && !line.equals("")) {
				elem.add(line.trim().split(","));
				line = reader.readLine();
			}
			Collections.sort(elem, new Comparator<String[]>() {
				public int compare(String[] l1, String[] l2) {
					return Integer.parseInt(l1[0]) - Integer.parseInt(l2[0]);
				}
			});
			reader.close();
			for (int i = 0; i < keys.length; i++) {
				String[] tmp = new String[elem.size()];
				for (int j = 0; j < elem.size(); j++) {
					tmp[j] = elem.get(j)[i];
				}
				infoMap.put(keys[i], tmp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("The database size is : "
				+ String.valueOf(infoMap.get("databaseIndex").length));
		return infoMap;
	}
	
	public static void updateDbInfo(boolean b) {
        System.err.format("Databases::updateDbInfo() called.\n"); //Ed 2017-09-12 for logging xml use.

		dbsize = infoMap.get("databaseIndex").length;
		spatialConfigPaths = infoMap.get("spatialConfigPaths");
		ConfigPaths = infoMap.get("ConfigPaths");
		if(b){
			String connectionPath = ConfigurationDirectory() ; // path + "../../src/main/resources/"; Ed 2017-09-12
			for (int k=0; k<ConfigPaths.length; k++){
				ConfigPaths[k] = connectionPath+ConfigPaths[k];
			}
		}
			    
		dbnames = infoMap.get("dbnames");
		connectionURLs = infoMap.get("connectionURL");
		usernames = infoMap.get("username");
		passwords = infoMap.get("password");		
	}

	public static void deactivateDB(int i){
		String[] newElement;
		for(Map.Entry<String, String[]> entry : infoMap.entrySet()) {
			newElement = new String[entry.getValue().length-1];
			for(int j=0;j<i;j++){
				newElement[j] = entry.getValue()[j];
			}
			for(int j=i;j<entry.getValue().length-1;j++){
				newElement[j] = entry.getValue()[j+1];
			}
			infoMap.put(entry.getKey(), newElement);
		}
	}
	
	public static int dbsize = infoMap.get("databaseIndex").length;
	public final static int defaultDBIndex = 0;
	public static String[] spatialConfigPaths = infoMap.get("spatialConfigPaths");
	public static String[] ConfigPaths        = infoMap.get("ConfigPaths");
	static{

        System.err.format("Databases::static{} called.\n"); //Ed 2017-09-12 for logging xml use.

		String connectionPath = ConfigurationDirectory(); // path + "../../src/main/resources/"; // Ed 2017-09-12

		for (int k=0; k<ConfigPaths.length; k++){
			ConfigPaths[k] = connectionPath + ConfigPaths[k];
            System.err.format("Databases::static{} ConfigPath[%d] is %s\n", k, ConfigPaths[k]); //Ed 2017-09-12 for logging xml use.
		}	    
	}		 
	public static String[] dbnames = infoMap.get("dbnames");
	public static String[] connectionURLs = infoMap.get("connectionURL");
	public static String[] usernames = infoMap.get("username");
	public static String[] passwords = infoMap.get("password");

}
