// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Analysis Software Tool.
//
//    Transit Network Analysis Software Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Analysis Software Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Analysis Software Tool.  If not, see <http://www.gnu.org/licenses/>.

// Alireza
package com.webapp.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@WebServlet(urlPatterns = "/TNAtoolAPI-Webapp")
public class MainMap extends HttpServlet {
	// GET
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		setDatabaseParams();		
		request.getRequestDispatcher("/TNAtoolAPI-Webapp/index.jsp").forward(request, response);
		
	}
	
	public void setDatabaseParams() throws IOException{		
		ClassLoader classLoader = getClass().getClassLoader();
		File databaseParamsFile = new File(classLoader.getResource("admin/resources/databaseParams.csv").getFile());
		
		BufferedReader reader = new BufferedReader(new FileReader(databaseParamsFile));
		reader.readLine();
		String[] params = reader.readLine().trim().split(",");
		reader.close();
		
		File connectionFolder = new File(classLoader.getResource("com/model/database/connections/spatial").getFile());
		File[] listOfFiles = connectionFolder.listFiles();
		String[] dbNames = new String[listOfFiles.length];
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  dbNames[i] = listOfFiles[i].getName().split("\\.")[0];
	    	  parseConnectionFiles(listOfFiles[i],params,dbNames[i]);
	      }
	    }
	    connectionFolder = new File(classLoader.getResource("com/model/database/connections/transit").getFile());
	    listOfFiles = connectionFolder.listFiles();
		dbNames = new String[listOfFiles.length];
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  dbNames[i] = listOfFiles[i].getName().split("\\.")[0];
	    	  parseConnectionFiles(listOfFiles[i],params,dbNames[i]);
	      }
	    }
	    File inputFile = new File(classLoader.getResource("admin/resources/dbInfo.csv").getFile());
		File tempFile = new File(classLoader.getResource("admin/resources").getPath()+"/dbInfoTmp.csv");
	    setDatabaseInfoFile(params,inputFile,tempFile);
	}
	
	private void setDatabaseInfoFile(String[] params, File inputFile, File tempFile) throws IOException{
		
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		String currentLine;
		writer.write(reader.readLine() + System.getProperty("line.separator"));
		String[] line;
		while((currentLine = reader.readLine()) != null) {
			line = currentLine.split(",");
			line[4] = "jdbc:postgresql://"+params[0]+":"+params[1]+"/"+line[4].split("/")[3];
			line[5] = params[2];
			line[6] = params[3];
	    	currentLine = "";
	    	for(int k=0;k<line.length-1;k++){
    			currentLine+=line[k]+",";
    		}
    		currentLine+=line[line.length-1];
    		writer.write(currentLine + System.getProperty("line.separator"));
		}
		writer.close(); 
		reader.close(); 
		inputFile.delete();
		tempFile.renameTo(inputFile);
	}
	
	private void parseConnectionFiles(File xmlFile, String[] params, String dbName){
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            
            NodeList props = doc.getElementsByTagName("property");
            Element prop = null;
             
            prop = (Element) props.item(1);
            prop.getFirstChild().setNodeValue("jdbc:postgresql://"+params[0]+":"+params[1]+"/"+dbName);
            prop = (Element) props.item(2);
            prop.getFirstChild().setNodeValue(params[2]);
            prop = (Element) props.item(3);
            prop.getFirstChild().setNodeValue(params[3]);
            
            XMLSerializer serializer = new XMLSerializer();
            serializer.setOutputCharStream(new java.io.FileWriter(xmlFile));
            OutputFormat format = new OutputFormat();
            format.setStandalone(true);
            serializer.setOutputFormat(format);
            serializer.serialize(doc);
            
        } catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
