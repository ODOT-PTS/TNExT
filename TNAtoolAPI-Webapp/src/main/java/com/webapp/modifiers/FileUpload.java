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

// Includes all the methods used in the playground interface (deprecated)
package com.webapp.modifiers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.mail.*;
import javax.mail.internet.*;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.model.database.*;
import com.model.database.queries.UpdateEventManager;
import com.model.database.queries.objects.AddRemoveFeed;
import com.model.database.queries.objects.UserSession;

/**
 * Servlet implementation class FileUpload
 */
@MultipartConfig
public class FileUpload extends HttpServlet {
	private final static String basePath = "C:/Users/Administrator/git/TNAsoftware/";
	private final static String psqlPath = "C:/Program Files/PostgreSQL/9.3/bin/";
	private static final long serialVersionUID = 1L;
	private static final String dbURL = Databases.connectionURLs[Databases.connectionURLs.length-1];
	private static final String dbUSER = Databases.usernames[Databases.usernames.length-1];
	private static final String dbPASS = Databases.passwords[Databases.passwords.length-1];
	private static final int DBINDEX = Databases.dbsize-1;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileUpload() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection c = null;
		Statement statement = null;
		ResultSet rs = null;
		response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    JSONObject obj = new JSONObject();
	    
	    String username = request.getParameter("username");
		String feedDel = request.getParameter("feedname");
		String listName = request.getParameter("list");
		String setSessionUser = request.getParameter("setSessionUser");
		String getSessionUser = request.getParameter("getSessionUser");
		String endSessionUser = request.getParameter("endSessionUser");
		String getURLpath = request.getParameter("getURLpath");
		String email = request.getParameter("email");
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String updateDel = request.getParameter("updateDel");
		String removeDel = request.getParameter("removeDel");
		String inDeleted = request.getParameter("inDeleted");
		String justAddedFeeds = request.getParameter("justAddedFeeds");
		String runPlayground = request.getParameter("runPlayground");
		String getIp = request.getParameter("getIp");
		
		if(getIp!=null){
			
			try {
				obj.put("DBError", getClientIp(request));
				out.print(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(runPlayground!=null){
			try {
				runPlayground(runPlayground);
			} catch (ZipException e) {
				e.printStackTrace();
			}
		}else if(justAddedFeeds!=null){
			FeedNames fn = new FeedNames();
			
			Boolean b = true;
			try {
				c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
				
				statement = c.createStatement();
				rs = statement.executeQuery("SELECT feedname, feedsize "
						+ "FROM gtfs_modified_feeds INNER JOIN gtfs_pg_users "
						+ "ON gtfs_modified_feeds.username=gtfs_pg_users.username "
						+ "where gtfs_modified_feeds.username = '"+justAddedFeeds+"' AND added='t';");
				
				while ( rs.next() ) {
					fn.feeds.add(rs.getString("feedname"));
					fn.names.add(rs.getString("feedsize"));
				}
				b=true;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			} finally {
				if (rs != null) try { rs.close(); } catch (SQLException e) {}
				if (statement != null) try { statement.close(); } catch (SQLException e) {}
				if (c != null) try { c.close(); } catch (SQLException e) {}
			}
			
	    	try {
	    		if(b){
	    			obj.put("sizes", fn.names);
					obj.put("feeds", fn.feeds);
	    		}else{
	    			obj.put("DBError", "");
	    		}
	    		out.print(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(inDeleted!=null){
			boolean b = isInDeleted(inDeleted);
			try {
				obj.put("DBError", new Boolean(b).toString());
				out.print(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(removeDel!=null){
			removeUpdateInfo(removeDel, username);
		}else if(updateDel!=null){
			updateDelete(updateDel,username);
		}else if(getURLpath!=null){
			String URLpath  = request.getRequestURL().toString();
	        int i = URLpath.indexOf(request.getServletPath());
	        URLpath = URLpath.substring(0, i);
			URLpath += ","+DBINDEX;
			
			try {
    			obj.put("URLpath", URLpath);
	    		out.print(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(endSessionUser!=null){//sign out
			HttpSession session = request.getSession(false);
			  if (session != null) {
				  String esu = (String) session.getAttribute("username");
				  try {
						c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
						
						statement = c.createStatement();
						
						statement.executeUpdate("DELETE FROM gtfs_selected_feeds WHERE username = '"+esu+"';");
				  } catch (SQLException e) {
						System.out.println(e.getMessage());
						
					} finally {
						if (rs != null) try { rs.close(); } catch (SQLException e) {}
						if (statement != null) try { statement.close(); } catch (SQLException e) {}
						if (c != null) try { c.close(); } catch (SQLException e) {}
					}
				  session.invalidate();
			  }
			  
		}else if(getSessionUser!=null){//get session id
			UserSession us = new UserSession();
			HttpSession session = request.getSession(false);
			if (session == null || session.getAttribute("username")==null){
				us.User = "admin";
			}else{
				us.User = (String) session.getAttribute("username");
			}
			
			try {
	    		obj.put("username", us.User);
	    		out.print(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(setSessionUser!=null){//set session id
			HttpSession session = request.getSession(true);
            session.setAttribute("username", setSessionUser);
            session.setMaxInactiveInterval(10*60);
            
		}else if(email!=null){// send confirmation email
		      String to = email;
		      final String emailUser = "tnatooltech";
		      final String emailPass = "***";
		      String host = "smtp.gmail.com";
		 
		      Properties properties = System.getProperties();
		      properties.put("mail.smtp.host", host); 
		      properties.put("mail.smtp.user", emailUser);
		      properties.put("mail.smtp.password", emailPass);
		      properties.put("mail.smtp.port", "587"); 
		      properties.put("mail.smtp.auth", "true");            
		      properties.put("mail.smtp.starttls.enable", "true");
		      
		      Session session = Session.getInstance(properties,null);
		      System.out.println("Port: "+session.getProperty("mail.smtp.port"));

		      Transport trans=null;
		      response.setContentType("text/html");

		      try{
		         MimeMessage message = new MimeMessage(session);
		         InternetAddress addressFrom = new InternetAddress(emailUser+"@gmail.com");  
		         message.setFrom(addressFrom);
		         
		         InternetAddress[] addressesTo = {new InternetAddress(emailUser+"@gmail.com")}; 
		         message.setRecipients(Message.RecipientType.TO, addressesTo);
		         
		         Multipart multipart = new MimeMultipart("alternative");
		         BodyPart messageBodyPart = new MimeBodyPart();
		         String htmlMessage = setMessage(username, email, firstname, lastname, request);
		         messageBodyPart.setContent(htmlMessage, "text/html");
		         multipart.addBodyPart(messageBodyPart);
		         message.setContent(multipart);
		         
		         message.setSubject("New GTFS Playground User Account Confirmation ("+username+")");
		         trans = session.getTransport("smtp");
		         trans.connect(host,emailUser,emailPass);
		         trans.sendMessage(message, message.getAllRecipients()); 
		      }catch (MessagingException mex) {
		         mex.printStackTrace();
		      }
		}else if(listName!=null){//list agency names added by admin
			FeedNames fn = new FeedNames();
			
			Boolean b = true;
			try {
				c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
				
				statement = c.createStatement();
				rs = statement.executeQuery("SELECT * FROM gtfs_feed_info "
						+ "where feedname in "
						+ "(select feedname from gtfs_uploaded_feeds where username='admin')");
				
				while ( rs.next() ) {
					fn.feeds.add(rs.getString("feedname"));
					fn.names.add(rs.getString("agencynames"));
					fn.startdates.add(rs.getString("startdate"));
					fn.enddates.add(rs.getString("enddate"));
				}
				b=true;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			} finally {
				if (rs != null) try { rs.close(); } catch (SQLException e) {}
				if (statement != null) try { statement.close(); } catch (SQLException e) {}
				if (c != null) try { c.close(); } catch (SQLException e) {}
			}
			
	    	try {
	    		if(b){
	    			obj.put("names", fn.names);
					obj.put("feeds", fn.feeds);
					obj.put("startdates", fn.startdates);
					obj.put("enddates", fn.enddates);
	    		}else{
	    			obj.put("DBError", "");
	    		}
	    		out.print(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(username==null){//feeds added by other users and made public
			FeedNames fn = new FeedNames();
			
			Boolean b = true;
			try {
				c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
				
				statement = c.createStatement();
				rs = statement.executeQuery("SELECT gtfs_uploaded_feeds.username, gtfs_uploaded_feeds.feedname, agencynames, firstname, lastname, startdate, enddate FROM gtfs_feed_info "
						+ "JOIN gtfs_uploaded_feeds "
						+ "ON gtfs_feed_info.feedname=gtfs_uploaded_feeds.feedname "
						+ "JOIN gtfs_pg_users "
						+ "On gtfs_uploaded_feeds.username=gtfs_pg_users.username "
						+ "where ispublic = 't' and active = 't';");
				
				while ( rs.next() ) {
					fn.ownerUsername.add(rs.getString("username"));
					fn.feeds.add(rs.getString("feedname"));
					fn.names.add(rs.getString("agencynames"));
					fn.ownerFirstname.add(rs.getString("firstname"));
					fn.ownerLastname.add(rs.getString("lastname"));
					fn.startdates.add(rs.getString("startdate"));
					fn.enddates.add(rs.getString("enddate"));
				}
				b=true;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			} finally {
				if (rs != null) try { rs.close(); } catch (SQLException e) {}
				if (statement != null) try { statement.close(); } catch (SQLException e) {}
				if (c != null) try { c.close(); } catch (SQLException e) {}
			}
			
	    	try {
	    		if(b){
	    			obj.put("names", fn.names);
					obj.put("feeds", fn.feeds);
					obj.put("ownerFirstname", fn.ownerFirstname);
					obj.put("ownerLastname", fn.ownerLastname);
					obj.put("ownerUsername", fn.ownerUsername);
					obj.put("startdates", fn.startdates);
					obj.put("enddates", fn.enddates);
	    		}else{
	    			obj.put("DBError", "");
	    		}
	    		out.print(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(feedDel!=null){//delete feed
			feedDel(username, feedDel);
			try {
				obj.put("DBError", "");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			out.print(obj);
		}else{
			FeedNames fn = new FeedNames();
			
			Boolean b = true;
			try {
				c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
				
				statement = c.createStatement();
				rs = statement.executeQuery("SELECT gtfs_uploaded_feeds.feedname, agencynames, ispublic, startdate, enddate "
						+ "FROM gtfs_feed_info JOIN gtfs_uploaded_feeds "
						+ "ON gtfs_feed_info.feedname=gtfs_uploaded_feeds.feedname "
						+ "where username = '"+username+"';");
				
				while ( rs.next() ) {
					fn.feeds.add(rs.getString("feedname"));
					fn.names.add(rs.getString("agencynames"));
					fn.isPublic.add(rs.getString("ispublic"));
					fn.startdates.add(rs.getString("startdate"));
					fn.enddates.add(rs.getString("enddate"));
				}
				b=true;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			} finally {
				if (rs != null) try { rs.close(); } catch (SQLException e) {}
				if (statement != null) try { statement.close(); } catch (SQLException e) {}
				if (c != null) try { c.close(); } catch (SQLException e) {}
			}
			
	    	try {
	    		if(b){
	    			obj.put("names", fn.names);
					obj.put("feeds", fn.feeds);
					obj.put("isPublic", fn.isPublic);
					obj.put("startdates", fn.startdates);
					obj.put("enddates", fn.enddates);
	    		}else{
	    			obj.put("DBError", "");
	    		}
	    		out.print(obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	    out.flush();
	}

	/**
	 * Uploads, adds, and updates gtfs feeds
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    JSONArray json = new JSONArray();
	    JSONObject rs = new JSONObject();
		String feed = "";
		String error = "";
		String username = "";
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		//System.out.println(System.currentTimeMillis());
        if (isMultipart) {
	        FileItemFactory factory = new DiskFileItemFactory();
	
	        ServletFileUpload upload = new ServletFileUpload(factory);
	 
	        try {
		        List<FileItem> items = upload.parseRequest(new ServletRequestContext(request));
		        
		        for(FileItem item: items){
		        	if (item.isFormField()) {
		        		username = item.getString();
		        	}
		        }
		        
		        for(FileItem item: items){
		            if (!item.isFormField()) {
		                String fileName = item.getName();
		                fileName=changeFeedName(fileName, username);
		                File path = new File(basePath + "TNAtoolAPI-Webapp/WebContent/playground/upload/uploaded/"+username);
		                if (!path.exists()) {
		                	boolean status = path.mkdirs();
		                }
		                File uploadedFile = new File(path + "/" + fileName);
		                feed = uploadedFile.getAbsolutePath();
		                item.write(uploadedFile);
		                
		                //changeCSV(feed, username);
		                
		                JSONObject jsono = new JSONObject();
                        jsono.put("name", fileName);
                        jsono.put("size", item.getSize());
                        jsono.put("url", "upload?getfile=" + fileName);
                        jsono.put("thumbnail_url", "upload?getthumb=" + fileName);
                        jsono.put("delete_url", "upload?delfile=" + fileName);
                        jsono.put("delete_type", "GET");
                        json.put(jsono);
                        
                        addUploadInfo(feed, fileName, item.getSize(),username);
		                /*error = addFeed(feed, fileName, item.getSize(),username);
		                System.out.println(error);*/
		            }
		        }
	        } catch (FileUploadException e) {
	        	e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
	        
	        updateQuota(username);
	        
	        try {
				rs.put("files", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			out.print(rs);
			out.flush();
			out.close();
			
	        //error = updateFeeds();
            //System.out.println(error);
        }
	}
	
	public boolean isInDeleted(String feedname){
		boolean b = false;
		Connection c = null;
		Statement statement = null;
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			statement = c.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM gtfs_modified_feeds WHERE feedname='"+feedname+"';");
			if ( rs.next() ) {
				b = true;
			}
					
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
		return b;
	}
	
	public void removeUpdateInfo(String feedname, String username){
		Connection c = null;
		Statement statement = null;
		String filename = "";
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			statement = c.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM gtfs_modified_feeds WHERE feedname='"+feedname+"';");
			if ( rs.next() ) {
				filename  = rs.getString("filename");
			}
			File uploadedFile = new File(filename);
			uploadedFile.delete();
			
			statement.executeUpdate("DELETE FROM gtfs_modified_feeds"
								  + " WHERE feedname='"+feedname+"';");
					
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
		updateQuota(username);
	}
	
	public void updateDelete(String feedname, String username){
		Connection c = null;
		Statement statement = null;
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			statement = c.createStatement();
			statement.executeUpdate("INSERT INTO gtfs_modified_feeds"
								  + " (username,feedname,feedsize,deleted,added,filename)"
								  + " VALUES ('"+username+"','"+feedname+"',0,TRUE,FALSE,'');");
					
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
	}
	
	public void addUploadInfo(String filename, String feedname, long feedsize, String username){
		for(int i=0;i<4;i++){
			feedname = removeLastChar(feedname);
		}
		Connection c = null;
		Statement statement = null;
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			statement = c.createStatement();
			statement.executeUpdate("INSERT INTO gtfs_modified_feeds"
								  + " (username,feedname,feedsize,deleted,added,filename)"
								  + " VALUES ('"+username+"','"+feedname+"','"+feedsize+"',FALSE,TRUE,'"+filename+"');");
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
		updateQuota(username);
	}
	
	public String setMessage(String username, String email, String firstname, String lastname, HttpServletRequest request){
		String message = "The following user has recently registered in the GTFS Playground website<br><br>"
				+ "FIRST NAME: "+firstname+"<br>"
				+ "LAST NAME: "+lastname+"<br>"
				+ "EMAIL ADDRESS: "+email+"<br><br><br><br>";
		
		/*String root = new File(".").getAbsolutePath();
        root = removeLastChar(root);*/
        /*File passFile = new File(basePath + "TNAtoolAPI-Webapp/WebContent/playground/pass.txt");
        BufferedReader bf; */
		String passkey="";
		Connection c = null;
		Statement statement = null;
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			statement = c.createStatement();
			ResultSet rs = statement.executeQuery("SELECT key FROM gtfs_pg_users WHERE username='"+username+"';");
			if ( rs.next() ) {
				passkey = rs.getString("key");
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
			
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
        
        /*try{
        	bf = new BufferedReader(new FileReader(passFile));
            passkey = bf.readLine();
            
            byte[] passByte = passkey.getBytes("UTF-8");
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		passByte = md.digest(passByte);
    		pass = new String(passByte, "UTF-8");
    		bf.close();
        }catch(IOException e){
        	e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}*/
        String URLpath  = request.getRequestURL().toString();
        int i = URLpath.indexOf(request.getServletPath());
        URLpath = URLpath.substring(0, i);
        
		String activate = URLpath+"/modifiers/dbupdate/activateUser?&key="+passkey+"&user="+username;
		message += "Click on the following link to activate the account:<br><br>"+activate+"<br><br><br><br>";
		String deny = URLpath+"/modifiers/dbupdate/denyUser?&key="+passkey+"&user="+username;
		//message += "Click on the following link to deny the account's activation:<br><br>"+deny;
		
		return message;
	}
	
	public String changeFeedName(String feedName, String username){
		String newName="";
		int index = 1;
		
		for(int i=0;i<4;i++){
			feedName = removeLastChar(feedName);
		}
		
		Connection c = null;
		Statement statement = null;
		List<String> names = new ArrayList<String>();
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			statement = c.createStatement();
			ResultSet rs = statement.executeQuery("SELECT gtfs_feed_info.feedname FROM gtfs_feed_info INNER JOIN gtfs_uploaded_feeds "
					+ "ON gtfs_feed_info.feedname=gtfs_uploaded_feeds.feedname "
					+ "WHERE gtfs_uploaded_feeds.username='"+username+"';");
			while ( rs.next() ) {
				names.add(rs.getString("feedname"));
			}
			
			rs = statement.executeQuery("SELECT feedname FROM gtfs_modified_feeds WHERE username='"+username+"';");
			while ( rs.next() ) {
				names.add(rs.getString("feedname"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
			
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
		
		while(true){
			newName = feedName+"-"+username+"-"+index;
			if(names.contains(newName)){
				index++;
			}else{
				break;
			}
		}
		
		return newName+".zip";
	}
	
	public static String addFeed(String feed, String feedName, long fileSize, String username) throws IOException{
		String [] args = new String[5];
		if(feedName.toLowerCase().contains(".zip")){
			for(int i=0;i<4;i++){
				feedName = removeLastChar(feedName);
			}
		}
		
		args[0] = "--driverClass=\"org.postgresql.Driver\"";
		args[1] = "--url=\""+dbURL+"\"";
		args[2] = "--username=\""+dbUSER+"\"";
		args[3] = "--password=\""+dbPASS+"\"";
		args[4] = feed;
		
		boolean b = true;
		try{
			GtfsDatabaseLoaderMain.main(args);
			b = false;
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		if(b){
			return "Upload unsuccessful";
		}
		
		Connection c = null;
		Statement statement = null;
		ResultSet rs = null;
		String defaultId = "";
		String agencyNames = "";
		String agencyIds = "";
		
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			
			statement = c.createStatement();
			rs = statement.executeQuery("SELECT * FROM gtfs_agencies Where defaultid IS NULL;");
			if ( rs.next() ) {
				String tmpAgencyId = rs.getString("id");
				rs = statement.executeQuery("SELECT * FROM gtfs_routes where agencyid = '"+tmpAgencyId+"' limit 1;");
				if ( rs.next() ) {
					defaultId = rs.getString("defaultid");
				}
				statement.executeUpdate("UPDATE gtfs_agencies SET defaultid = '"+defaultId+"' WHERE defaultid IS NULL;");
			}
			
			rs = statement.executeQuery("SELECT * FROM gtfs_agencies Where added IS NULL;");
			
			while ( rs.next() ) {
				defaultId = rs.getString("defaultid");
				agencyNames += rs.getString("name")+",";
				agencyIds += rs.getString("id")+",";
			}
			agencyNames = removeLastChar(agencyNames);
			agencyIds = removeLastChar(agencyIds);
			statement.executeUpdate("UPDATE gtfs_agencies SET added='added' WHERE added IS NULL;");
			
			rs = statement.executeQuery("SELECT * FROM gtfs_feed_info Where defaultid = '"+defaultId+"';");
			if (!rs.next() ){
				rs = statement.executeQuery("SELECT gid FROM gtfs_feed_info;");
				List<String> ids = new ArrayList<String>();
				while ( rs.next() ) {
					ids.add(rs.getString("gid"));
				}
				int gid;
				int Low = 10000;
				int High = 99999;
				do {
					Random r = new Random();
					gid = r.nextInt(High-Low) + Low;
				} while (ids.contains(Integer.toString(gid)));
				String sql = "INSERT INTO gtfs_feed_info "+
							 "(gid,publishername,publisherurl,lang,startdate,enddate,version,defaultid,agencyids,agencynames,feedname) "+
							 "VALUES ("+Integer.toString(gid)+",'N/A','N/A','N/A','N/A','N/A','N/A','"+defaultId+"','"+agencyIds+"','"+agencyNames+"','"+feedName+"')";
				statement.executeUpdate(sql);
			}else{
				statement.executeUpdate("UPDATE gtfs_feed_info SET feedname = '"+feedName+"' WHERE defaultid = '"+defaultId+"';");
			}
			statement.executeUpdate("INSERT INTO gtfs_uploaded_feeds (feedname,username,ispublic,feedsize) "
					+ "VALUES ('"+feedName+"','"+username+"',FALSE,'"+fileSize+"');");
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
		/*updateFeeds();
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
//		UpdateEventManager.updateTables(DBINDEX, defaultId); //to be fixed
		
		return "Feed added and updated";
	}
	
	public static void changeCSV(String feed, String username, String firstname, String lastname) throws IOException, ZipException{
		String path=feed;
		for(int j=0;j<4;j++){
			path = removeLastChar(path); 
		}
		
		int feedIndex = 0;
		
		/*start unzipping*/
		File zipF = new File(path+".zip");
		ZipFile zipFile = new ZipFile(zipF);
		File folder = new File(path);
        zipFile.extractAll(path);
        zipF.delete();
        /*end unzipping*/
        
        /*start csv manipulation of feed's name, agency id, and agency name*/
        File input = new File(path+"/agency.txt");
        File output = new File(path+"/agencyTmp.txt");
		CSVReader reader = new CSVReader(new FileReader(input));
		CSVWriter writer = new CSVWriter(new FileWriter(path+"/agencyTmp.txt"), ',', CSVWriter.NO_QUOTE_CHARACTER);
		String [] nextLine;
		
		int agencyIdIndex=-1;
		String agencyId="";
		String agnecyName="";
		int agencyNameIndex=-1;
		List<String> lineAsList = new ArrayList<String>(Arrays.asList(reader.readNext()));
		for(String s: lineAsList){
			if(s.equals("agency_id") || s.equals("\"agency_id\"")){
	    		agencyIdIndex = lineAsList.indexOf(s);
	    	}else if(s.equals("agency_name") || s.equals("\"agency_name\"")){
	    		agencyNameIndex = lineAsList.indexOf(s);
	    	}
		}
		if(agencyIdIndex==-1){
			lineAsList.add("agency_id");
		}
		
		String[] CSVarray = lineAsList.toArray(new String[lineAsList.size()]);
	    writer.writeNext(CSVarray);
		
		while ((nextLine = reader.readNext()) != null) {
		    lineAsList = new ArrayList<String>(Arrays.asList(nextLine));
		    agnecyName = lineAsList.get(agencyNameIndex);
		    
		    if(agencyIdIndex!=-1){
		    	agencyId = lineAsList.get(agencyIdIndex);
		    	if(lineAsList.get(agencyIdIndex)==null || agencyId.equals("")){
		    		feedIndex = getFeedIndex(agnecyName, username);
		    		lineAsList.set(agencyIdIndex, agnecyName.replace(' ', '-')+"_"+username+"_"+feedIndex);
		    	}else{
		    		feedIndex = getFeedIndex(agencyId, username);
		    		lineAsList.set(agencyIdIndex, agencyId+"_"+username+"_"+feedIndex);
		    	}
		    }else{
		    	feedIndex = getFeedIndex(agnecyName, username);
		    	lineAsList.add(agnecyName.replace(' ', '-')+"_"+username+"_"+feedIndex);
		    }
		    lineAsList.set(agencyNameIndex, agnecyName+" (Added by "+firstname+" "+lastname+" - ("+feedIndex+"))");
		    CSVarray = lineAsList.toArray(new String[lineAsList.size()]);
		    writer.writeNext(CSVarray);
		}
		writer.close();
		reader.close();
		input.delete();
		output.renameTo(input);
		
		//Agency id modification in routes.txt
		File inputRoute = new File(path+"/routes.txt");
        File outputRoute = new File(path+"/routesTmp.txt");
		reader = new CSVReader(new FileReader(inputRoute));
		writer = new CSVWriter(new FileWriter(path+"/routesTmp.txt"), ',', CSVWriter.NO_QUOTE_CHARACTER);
		
		lineAsList = new ArrayList<String>(Arrays.asList(reader.readNext()));
		for(String s: lineAsList){
	    	if(s.equals("agency_id") || s.equals("\"agency_id\"")){
	    		agencyIdIndex = lineAsList.indexOf(s);
	    	}
		}
		if(agencyIdIndex==-1){
			lineAsList.add("agency_id");
		}
		
		CSVarray = lineAsList.toArray(new String[lineAsList.size()]);
	    writer.writeNext(CSVarray);
	    
	    while ((nextLine = reader.readNext()) != null) {
		    lineAsList = new ArrayList<String>(Arrays.asList(nextLine));
		    
		    if(agencyIdIndex!=-1){
		    	agencyId = lineAsList.get(agencyIdIndex);
		    	if(lineAsList.get(agencyIdIndex)==null || agencyId.equals("")){
		    		lineAsList.set(agencyIdIndex, agnecyName.replace(' ', '-')+"_"+username+"_"+feedIndex);
		    	}else{
		    		lineAsList.set(agencyIdIndex, agencyId+"_"+username+"_"+feedIndex);
		    	}
		    }else{
		    	lineAsList.add(agnecyName.replace(' ', '-')+"_"+username+"_"+feedIndex);
		    }
		    
		    CSVarray = lineAsList.toArray(new String[lineAsList.size()]);
		    writer.writeNext(CSVarray);
		}
		writer.close();
		reader.close();
		inputRoute.delete();
		outputRoute.renameTo(inputRoute);
        /*end csv manipulation feed*/
        
        /*start zipping*/
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipF));
        InputStream in = null;
        
        File[] sfiles = folder.listFiles();
        
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        
        for(File f: sfiles){
        	out.putNextEntry(f, parameters);
        	
        	in = new FileInputStream(f);
            byte[] readBuff = new byte[4096];
            int readLen = -1;

            while ((readLen = in.read(readBuff)) != -1) {
            	out.write(readBuff, 0, readLen);
            }
        	
            out.closeEntry();
        	in.close();
        }
        out.finish();
        out.close();
        FileUtils.deleteDirectory(folder);
        /*end zipping*/
	}
	
	public static int getFeedIndex(String agencyId, String username){
		int index =1;
		
		Connection c = null;
		Statement statement = null;
		List<String> feedNames = new ArrayList<String>();
		List<String> names;
		String name;
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			statement = c.createStatement();
			ResultSet rs = statement.executeQuery("SELECT agencyids FROM gtfs_feed_info INNER JOIN gtfs_uploaded_feeds "
					+ "ON gtfs_feed_info.feedname=gtfs_uploaded_feeds.feedname "
					+ "WHERE gtfs_uploaded_feeds.username='"+username+"';");
			while ( rs.next() ) {
				names = Arrays.asList(rs.getString("agencyids").split(","));
				feedNames.addAll(names);
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
			
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
		
		while(true){
			name = agencyId.replace(' ', '-')+"_"+username+"_"+index;
			if(feedNames.contains(name)){
				index++;
			}else{
				break;
			}
		}
		
		return index;
	}
	
	public static void updateQuota(String username){
		Connection c = null;
		Statement statement = null;
		String usedspace="";
		long us;
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			statement = c.createStatement();
			ResultSet rs = statement.executeQuery("SELECT SUM(feedsize) FROM gtfs_uploaded_feeds where username = '"+username+"';");
			if ( rs.next() ) {
				usedspace = rs.getString("sum");
			}
			if(usedspace==null || usedspace.equals("")){
				usedspace = 0+"";
			}
			us = Long.parseLong(usedspace);
			
			rs = statement.executeQuery("SELECT SUM(feedsize) FROM gtfs_modified_feeds where username = '"+username+"';");
			if ( rs.next() ) {
				usedspace = rs.getString("sum");
			}
			if(usedspace==null || usedspace.equals("")){
				usedspace = 0+"";
			}
			us += Long.parseLong(usedspace);
			
			statement.executeUpdate("UPDATE gtfs_pg_users SET usedspace='"+us+"' WHERE username='"+username+"';");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
			
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
	}
	
	public static String updateFeeds(){
		
		Process pr;
		ProcessBuilder pb;
		String[] dbname = dbURL.split("/");
		String name = dbname[dbname.length-1];
		boolean bz = true;
		try {
			pb = new ProcessBuilder("cmd", "/c", "start", basePath+"TNAtoolAPI-Webapp/WebContent/admin/Development/PGSQL/dbUpdate.bat", dbPASS, dbUSER, name,
					psqlPath+"psql.exe",
					basePath+"TNAtoolAPI-Webapp/WebContent/admin/Development/PGSQL/");
			pb.redirectErrorStream(true);
			pr = pb.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "Feed updated";
	}
  	
	public static String removeLastChar(String str) {
    	if (str.length() > 0) {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }
	
	public static String sqlString(String[] ids, String column){
		String sql = "";
		for(int i=0;i<ids.length-1;i++){
			sql += column+" = '"+ids[i]+"' OR ";
		}
		sql += column+" = '"+ids[ids.length-1];
		return sql;
	}

	public static void runPlayground(String b) throws IOException, ZipException{
		
		List<AddRemoveFeed> addFeeds = new ArrayList<AddRemoveFeed>();
		List<AddRemoveFeed> removeFeeds = new ArrayList<AddRemoveFeed>();
		AddRemoveFeed feed;
		Connection c = null;
		Statement statement = null;
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			statement = c.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM gtfs_modified_feeds gmf"
					+ " inner join gtfs_pg_users gpu on gmf.username=gpu.username;");
			while(rs.next()){
				feed = new AddRemoveFeed();
				feed.username = rs.getString("username");
				feed.feedname = rs.getString("feedname");
				feed.firstname = rs.getString("firstname");
				feed.lastname = rs.getString("lastname");
				if(rs.getString("deleted").equals("t")){
					removeFeeds.add(feed);
				}else{
					feed.feedsize = rs.getString("feedsize");
					feed.filename = rs.getString("filename");
					addFeeds.add(feed);
				}
			}
			
			for(AddRemoveFeed f: removeFeeds){
				if(b.equals("true") || checkTime()){
					feedDel(f.username, f.feedname);
					
					statement.executeUpdate("DELETE FROM gtfs_modified_feeds "
							+ "WHERE feedname='"+f.feedname+"';");
					System.out.println("vacuum start");
					statement.executeUpdate("VACUUM");
					System.out.println("vacuum finish");
				}
			}
			/*System.out.println("Post delete vacuum start");
			statement.executeUpdate("VACUUM");
			System.out.println("Post delete vacuum finish");*/
			
			for(AddRemoveFeed f: addFeeds){
				if(b.equals("true") || checkTime()){
					changeCSV(f.filename, f.username, f.firstname, f.lastname);
					addFeed(f.filename, f.feedname, Long.parseLong(f.feedsize),f.username);
					
					statement.executeUpdate("DELETE FROM gtfs_modified_feeds "
							+ "WHERE feedname='"+f.feedname+"';");
					
					System.out.println("vacuum start");
					statement.executeUpdate("VACUUM");
					System.out.println("vacuum finish");
				}
			}			
			/*System.out.println("Post add vacuum start");
			statement.executeUpdate("VACUUM");
			System.out.println("Post add vacuum finish");*/
			
			/*System.out.println("Full vacuum start");
			statement.executeUpdate("VACUUM FULL");
			System.out.println("Full vacuum finish");*/
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
	}
	
	public static boolean checkTime(){
		boolean b=false;
		try {
		    String t1 = "01:25:00";
		    Date time1 = new SimpleDateFormat("HH:mm:ss").parse(t1);
		    Calendar c1 = Calendar.getInstance();
		    c1.setTime(time1);
		    c1.add(Calendar.DATE, 1);

		    String t2 = "06:00:00";
		    Date time2 = new SimpleDateFormat("HH:mm:ss").parse(t2);
		    Calendar c2 = Calendar.getInstance();
		    c2.setTime(time2);
		    c2.add(Calendar.DATE, 1);

		    String current = new SimpleDateFormat("HH:mm:ss").format(new Date());
		    Date d = new SimpleDateFormat("HH:mm:ss").parse(current);
		    Calendar c3 = Calendar.getInstance();
		    c3.setTime(d);
		    c3.add(Calendar.DATE, 1);

		    Date x = c3.getTime();
		    if (x.after(c1.getTime()) && x.before(c2.getTime())) {
		        b = true;
		    }
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}
	
	public static void schedulePlayground(){
		long difference=0;
		try {
			String t = "01:30:00";
		    Date time = new SimpleDateFormat("HH:mm:ss").parse(t);
		    Calendar c = Calendar.getInstance();
		    c.setTime(time);
		    c.add(Calendar.DATE, 1);
		    
		    String current = new SimpleDateFormat("HH:mm:ss").format(new Date());
		    Date now = new SimpleDateFormat("HH:mm:ss").parse(current);
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(now);
		    cal.add(Calendar.DATE, 1);

		    Date x = cal.getTime();
		    
		    if (x.after(c.getTime())) {
		        difference = 86400000 - (x.getTime() - c.getTime().getTime());
		    }else{
		    	difference = c.getTime().getTime() - x.getTime(); 
		    }
		}catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		/*System.out.println("Scheduling..");
		ScheduledExecutorService fScheduler = Executors.newScheduledThreadPool(1);    
		Runnable runPlayground = new RunPlayground();
		fScheduler.scheduleAtFixedRate(runPlayground, difference, 86400000, TimeUnit.MILLISECONDS);
		System.out.println("done");*/
	}
	
	public static final class RunPlayground implements Runnable {
	    @Override public void run() {
	    	try {
				runPlayground("false");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ZipException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	public static void feedDel(String username, String feedDel){
		String agencyId = "";
		String agencyIds = "";
		String[] agencyIdList;
		Connection c = null;
		Statement statement = null;
		ResultSet rs = null;
        File path = new File(basePath + "TNAtoolAPI-Webapp/WebContent/playground/upload/uploaded/"+username);
        File uploadedFile = new File(path + "/" + feedDel + ".zip");
         
		
		String[][] defAgencyIds  = {{"census_congdists_trip_map","agencyid_def"},
									{"census_places_trip_map","agencyid_def"},
									{"census_urbans_trip_map","agencyid_def"},
									{"census_counties_trip_map","agencyid_def"},
									{"census_tracts_trip_map","agencyid_def"},
									{"gtfs_fare_rules","fare_agencyid"},
									{"gtfs_fare_attributes","agencyid"},
									{"gtfs_trip_stops","stop_agencyid_origin"},
									{"gtfs_stop_service_map","agencyid_def"},
									{"gtfs_route_serviceid_map","agencyid_def"},
									{"gtfs_stop_route_map","agencyid_def"},
									{"gtfs_frequencies","defaultid"},
									{"gtfs_pathways","agencyid"},
									{"gtfs_shape_points","shapeid_agencyid"},
									{"gtfs_stop_times","stop_agencyid"},
									{"gtfs_transfers","defaultid"},
									{"tempstopcodes","agencyid"},
									{"tempetriptimes","agencyid"},
									{"tempestshapes","agencyid"},
									{"tempshapes","agencyid"},
									{"gtfs_trips","serviceid_agencyid"},
									{"gtfs_calendar_dates","serviceid_agencyid"},
									{"gtfs_calendars","serviceid_agencyid"},
									{"gtfs_stops","agencyid"},
									{"gtfs_routes","defaultid"},
									{"gtfs_agencies","defaultid"},
									{"gtfs_feed_info","defaultid"}};
		
		try {
			c = DriverManager.getConnection(dbURL, dbUSER, dbPASS);
			statement = c.createStatement();
			
			statement.executeUpdate("DELETE FROM gtfs_selected_feeds WHERE feedname = '"+feedDel+"';");
			statement.executeUpdate("DELETE FROM gtfs_uploaded_feeds WHERE feedname = '"+feedDel+"';");
			
			updateQuota(username);
			
			rs = statement.executeQuery("SELECT defaultid FROM gtfs_feed_info where feedname = '"+feedDel+"';");
			if ( rs.next() ) {
				agencyId = rs.getString("defaultid");
			}
			
			rs = statement.executeQuery("SELECT agencyids FROM gtfs_feed_info where feedname = '"+feedDel+"';");
			if ( rs.next() ) {
				agencyIds = rs.getString("agencyids");
			}
			agencyIdList = agencyIds.split(",");
			
			for(int i=0;i<defAgencyIds.length;i++){
				System.out.println(defAgencyIds[i][0]);
				try{
					if(defAgencyIds[i][0].startsWith("temp")){
						statement.executeUpdate("DELETE FROM "+defAgencyIds[i][0]+" WHERE "+sqlString(agencyIdList,defAgencyIds[i][1])+"';");
						
					}else{
						statement.executeUpdate("ALTER TABLE "+defAgencyIds[i][0]+" DISABLE TRIGGER ALL;");
						statement.executeUpdate("DELETE FROM "+defAgencyIds[i][0]+" WHERE "+defAgencyIds[i][1]+"='"+agencyId+"';");
						statement.executeUpdate("ALTER TABLE "+defAgencyIds[i][0]+" ENABLE TRIGGER ALL;");
					}
					
				}catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			}
			
			uploadedFile.delete();
			/*System.out.println("vacuum start");
			statement.executeUpdate("VACUUM");
			System.out.println("vacuum finish");*/
			//System.out.println(System.currentTimeMillis());
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) {}
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (c != null) try { c.close(); } catch (SQLException e) {}
		}
	}
	
	public static String getClientIp(HttpServletRequest request){
		String ip = request.getHeader("X-Forwarded-For");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
        return ip;  
	}
}

