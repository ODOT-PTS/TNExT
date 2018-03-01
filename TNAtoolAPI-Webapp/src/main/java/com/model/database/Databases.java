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

	// getDbInfo
	public static HashMap<String, String[]> getDbInfo() {
		HashMap<String, String[]> infoMap = DatabaseConfig.toInfoMap();
		System.out.println("The number of databases in dbInfo.csv is is: " + String.valueOf(infoMap.get("databaseIndex").length));
		return infoMap;
	}
	
	public static void updateDbInfo(boolean b) {
  	System.err.format("Databases::updateDbInfo() called.\n"); //Ed 2017-09-12 for logging xml use.
	}

	public static void deactivateDB(int i){
		// todo ian: remove from DatabaseConfig and call getDbInfo again
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
}
