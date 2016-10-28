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
	public static String path;
	public static HashMap<String, String[]> getDbInfo() {

		HashMap<String, String[]> infoMap = new HashMap<String, String[]>();
		try {
			path = Databases.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			BufferedReader reader = new BufferedReader(new FileReader(
					path+"../../src/main/resources/admin/resources/dbInfo.csv"));
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
		dbsize = infoMap.get("databaseIndex").length;
		spatialConfigPaths = infoMap.get("spatialConfigPaths");
		ConfigPaths = infoMap.get("ConfigPaths");
		if(b){
			String connectionPath = path + "../../src/main/resources/";
			for (int k=0; k<ConfigPaths.length; k++){
				ConfigPaths[k] = connectionPath+ConfigPaths[k];
			}
		}
			    
		dbnames = infoMap.get("dbnames");
		connectionURLs = infoMap.get("connectionURL");
		usernames = infoMap.get("username");
		passwords = infoMap.get("password");
		/*for(Map.Entry<String, String[]> entry : infoMap.entrySet()) {
			System.out.println(entry.getKey());
			for(String str: entry.getValue()){
				System.out.println(str);
			}
			
			System.out.println();
		}*/
		
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
	public static String[] spatialConfigPaths = infoMap
			.get("spatialConfigPaths");
	public static String[] ConfigPaths = infoMap.get("ConfigPaths");
	static{
		String connectionPath = path + "../../src/main/resources/";
		for (int k=0; k<ConfigPaths.length; k++){
			ConfigPaths[k] = connectionPath + ConfigPaths[k];
		}	    
	}		 
	public static String[] dbnames = infoMap.get("dbnames");
	public static String[] connectionURLs = infoMap.get("connectionURL");
	public static String[] usernames = infoMap.get("username");
	public static String[] passwords = infoMap.get("password");

}
