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

// Creates the required xml connection files for all databases from databaseParams.csv
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

import com.model.database.Databases;


@WebServlet(urlPatterns = "/TNAtoolAPI-Webapp")
public class MainMap extends HttpServlet {
	// GET
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		request.getRequestDispatcher("/TNAtoolAPI-Webapp/index.jsp").forward(request, response);
		
	}
	
	public void setDatabaseParams() throws IOException{		
        // Ed 2017-09-12
        //
        // Note:
        //
        // This appears to automatically "rewrite" the dbInfo.csv file and
        // hibernate xml files based on credentials provided in the
        // databaseParams.csv file.
        //
        // I think it would be simpler and better to set up credentials using
        // either java properties, or environment variables if the Java JDBC
        // library provides a way to do that.
		//
		// Ian 2018-02-20 - Don't do this - config modifications *must* always be explicit.
	}
}
