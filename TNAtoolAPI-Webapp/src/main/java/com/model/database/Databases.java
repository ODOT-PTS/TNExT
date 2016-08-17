package com.model.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Databases {
	public static HashMap<String, String[]> infoMap = getDbInfo();
	public static String path;
	public static HashMap<String, String[]> getDbInfo() {

		HashMap<String, String[]> infoMap = new HashMap<String, String[]>();
		try {
			path = Databases.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			BufferedReader reader = new BufferedReader(new FileReader(
					path+"../../src/main/webapp/resources/admin/dbInfo.csv"));
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
