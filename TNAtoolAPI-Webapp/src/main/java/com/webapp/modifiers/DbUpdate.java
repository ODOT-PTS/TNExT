// Copyright (C) 2015 Oregon State University - School of Mechanical,Industrial and Manufacturing Engineering 
//   This file is part of Transit Network Explorer Tool.
//
//    Transit Network Explorer Tool is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Transit Network Explorer Tool is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU  General Public License for more details.
//
//    You should have received a copy of the GNU  General Public License
//    along with Transit Network Explorer Tool.  If not, see <http://www.gnu.org/licenses/>.

// Includes all the methods used for the creation, modification, maintenance, and deletion of databases

package com.webapp.modifiers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.log4j.Logger;

import org.apache.tomcat.util.http.fileupload.FileDeleteStrategy;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.hibernate.JDBCException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.goebl.simplify.Point;
import com.goebl.simplify.Simplify;
import com.model.database.DatabaseConfig;
import com.model.database.Databases;
import com.model.database.DatabaseConfig;
import com.model.database.FeedNames;
import com.model.database.GtfsDatabaseLoaderMain;
import com.model.database.PDBerror;
import com.model.database.UserInfo;
import com.model.database.onebusaway.gtfs.hibernate.ext.GtfsHibernateReaderExampleMain;
import com.model.database.queries.EventManager;
import com.model.database.queries.PgisEventManager;
import com.model.database.queries.UpdateEventManager;
import com.model.database.queries.objects.DatabaseStatus;
import com.model.database.queries.util.Hutil;
import com.model.database.queries.util.StateInits;
import com.webapp.api.Queries;

@Path("/dbupdate")
@XmlRootElement
public class DbUpdate {
  final static Logger logger = Logger.getLogger(DbUpdate.class);
  private final static int USER_COUNT = 10;
  private final static int QUOTA = 10000000;
  private final static DatabaseConfig dbConfig = DatabaseConfig.getLastConfig();
  public final static String VERSION = "V4.16.07";
  public static boolean gtfsUpload = false;
  public static String gtfsMessage = "";

  // ian: unused?
  @POST
  @Path("/correctAjax")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object tests(@QueryParam("x") String x) {
    PDBerror b = new PDBerror();
    b.DBError = x;
    logger.debug(x);
    return b;
  }

  @GET
  @Path("/readDBinfo")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object readDBinfo() throws IOException {
    int dbsize = DatabaseConfig.getConfigSize();
    String[][] dbInfo = new String[dbsize+1][];
    dbInfo[0] = DatabaseConfig.getFields();
    DatabaseConfig[] ds = DatabaseConfig.getConfigs();
    for (int i=0; i < ds.length; i++) {
      dbInfo[i+1] = ds[i].toArray();
    }
    return dbInfo;
  }

  @GET
  @Path("/getIndex")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object getIndex() throws IOException {
    return DatabaseConfig.getLastConfig().getDatabaseIndex() + 1;
  }

  @GET
  @Path("/activateDBs")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object activateDBs(@QueryParam("db") String db) throws IOException {
    updateDatabaseStaticInfo(true);

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      statement.executeUpdate("UPDATE database_status SET activated = true;");
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    return "done";
  }

  public void updateDatabaseStaticInfo(boolean b) {
    Queries.updateDefaultDBindex();
    GtfsHibernateReaderExampleMain.updateSessions();
    Hutil.updateSessions();
    EventManager.updateSessions();
  }

  @GET
  @Path("/changeDBStatus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object changeDBStatus(@QueryParam("db") String db, @QueryParam("field") String fieldName,
      @QueryParam("b") boolean b) throws IOException {

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      statement.executeUpdate("UPDATE database_status SET " + fieldName + " = " + b + ";");
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return "done";
  }

  @GET
  @Path("/deactivateDBs")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deactivateDBs(@QueryParam("db") String db, @QueryParam("index") int index) throws IOException {
    DatabaseConfig.deactivateDb(index);
    updateDatabaseStaticInfo(false);

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      statement.executeUpdate("UPDATE database_status SET activated = false;");
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return "done";
  }

  // ian: unused?
  @GET
  @Path("/userCount")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object userCount() {
    Connection c = null;
    Statement statement = null;
    PDBerror error = new PDBerror();
    int count = 0;
    error.DBError = "true";
    ResultSet rs = null;
    try {
      c = dbConfig.getConnection();
      statement = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      rs = statement.executeQuery("select * from gtfs_pg_users;");
      rs.last();
      count = rs.getRow();
      if (count >= USER_COUNT) {
        error.DBError = "false";
      }

    } catch (SQLException e) {
      logger.error(e);
      error.DBError = "error";
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return error;
  }

  @GET
  @Path("/activateUser")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object activateUser(@QueryParam("key") String key, @QueryParam("user") String username)
      throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException {
    /*String root = new File(".").getAbsolutePath();
        root = removeLastChar(root);*/
    /*File passFile = new File(basePath + "TNAtoolAPI-Webapp/WebContent/playground/pass.txt");
    BufferedReader bf; */
    String passkey = "";
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    try {
      c = dbConfig.getConnection();
      statement = c.createStatement();
      rs = statement.executeQuery("SELECT key FROM gtfs_pg_users WHERE username='" + username + "';");
      if (rs.next()) {
        passkey = rs.getString("key");
      }

    } catch (SQLException e) {
      logger.error(e);
    }

    String email = "";
    String lastname = "";
    String firstname = "";
    if (passkey.equals(key)) {
      try {
        statement = c.createStatement();
        statement.executeUpdate("UPDATE gtfs_pg_users SET active=true WHERE username='" + username + "';");
        rs = statement
            .executeQuery("select email,lastname,firstname from gtfs_pg_users where username='" + username + "';");
        if (rs.next()) {
          email = rs.getString("email");
          lastname = rs.getString("lastname");
          firstname = rs.getString("firstname");
        }

      } catch (SQLException e) {
        logger.error(e);
      } finally {
        if (rs != null)
          try {
            rs.close();
          } catch (SQLException e) {
          }
        if (statement != null)
          try {
            statement.close();
          } catch (SQLException e) {
          }
        if (c != null)
          try {
            c.close();
          } catch (SQLException e) {
          }
      }
    } else {
      return "exit";
    }

    String to = email;
    final String emailUser = "tnatooltech";
    final String emailPass = "OSUteam007@gmail";
    String host = "smtp.gmail.com";

    Properties properties = System.getProperties();
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.user", emailUser);
    properties.put("mail.smtp.password", emailPass);
    properties.put("mail.smtp.port", "587");
    properties.put("mail.smtp.auth", "true");
    //properties.put("mail.debug", "true");              
    properties.put("mail.smtp.starttls.enable", "true");
    //properties.put("mail.smtp.EnableSSL.enable", "true");

    Session session = Session.getInstance(properties, null);
    logger.info("Port: " + session.getProperty("mail.smtp.port"));

    Transport trans = null;

    try {
      MimeMessage message = new MimeMessage(session);
      InternetAddress addressFrom = new InternetAddress(emailUser + "@gmail.com");
      message.setFrom(addressFrom);

      InternetAddress[] addressesTo = { new InternetAddress(to) };
      message.setRecipients(Message.RecipientType.TO, addressesTo);

      Multipart multipart = new MimeMultipart("alternative");
      BodyPart messageBodyPart = new MimeBodyPart();
      String htmlMessage = firstname + " " + lastname + ",<br><br>"
          + "Your GTFS Playground account was successfully activated!<br>"
          + "You can now log into the website using your credentials.";
      messageBodyPart.setContent(htmlMessage, "text/html");
      multipart.addBodyPart(messageBodyPart);
      message.setContent(multipart);

      message.setSubject("GTFS Playground Account Activated");
      trans = session.getTransport("smtp");
      trans.connect(host, emailUser, emailPass);
      //message.saveChanges();
      trans.sendMessage(message, message.getAllRecipients());
    } catch (MessagingException mex) {
      mex.printStackTrace();
    }
    PDBerror er = new PDBerror();
    er.DBError = username + "'s account was successfully activated.";
    return er;
  }

  /*@GET
    @Path("/validatePass")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public Object validatePass(@QueryParam("pass") String pass) throws IOException, NoSuchAlgorithmException, UnsupportedEncodingException{
    String tmpPath = basePath+"TNAtoolAPI-Webapp/WebContent/playground/";
    File inputFile = new File(tmpPath + "pass.txt");
  
    BufferedReader reader = new BufferedReader(new FileReader(inputFile));
    PDBerror b = new PDBerror();
    String passkey = reader.readLine();
    if(passkey.equals(pass)){
      b.DBError = "true";
    }else{
      b.DBError = "false";
    }
    reader.close();
    
    return b;
  }*/

  // ian: unused?
  @GET
  @Path("/getUserInfo")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object getUserInfo(@QueryParam("user") String user) {
    Connection c = null;
    Statement statement = null;
    UserInfo userInfo = new UserInfo();
    ResultSet rs = null;
    try {
      c = dbConfig.getConnection();
      statement = c.createStatement();
      rs = statement.executeQuery("select * from gtfs_pg_users where username='" + user + "' or email='" + user + "';");
      if (rs.next()) {
        userInfo.Firstname = rs.getString("firstname");
        userInfo.Lastname = rs.getString("lastname");
        userInfo.Username = rs.getString("username");
        userInfo.Quota = rs.getString("quota");
        userInfo.Usedspace = rs.getString("usedspace");
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return userInfo;
  }

  // ian: unused?
  @GET
  @Path("/checkUser")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkUser(@QueryParam("user") String user) {
    Connection c = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    PDBerror error = new PDBerror();
    error.DBError = "false";
    try {
      c = dbConfig.getConnection();
      statement = c.prepareStatement("select * from gtfs_pg_users where username=? or email=?;");
      statement.setString(1, user);
      statement.setString(2, user);
      rs = statement.executeQuery();
      if (rs.next()) {
        error.DBError = "true";
      } else {
        error.DBError = "false";
      }
    } catch (SQLException e) {
      logger.error(e);
      error.DBError = "error";
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return error;
  }

  // ian: unused?
  @GET
  @Path("/changePublic")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object changePublic(@QueryParam("isPublic") String p, @QueryParam("feedname") String feedname) {
    Connection c = null;
    Statement statement = null;
    PDBerror error = new PDBerror();
    error.DBError = "";
    try {
      c = dbConfig.getConnection();
      statement = c.createStatement();
      statement
          .executeUpdate("UPDATE gtfs_uploaded_feeds SET ispublic = '" + p + "' WHERE feedname = '" + feedname + "';");

    } catch (SQLException e) {
      logger.error(e);
      error.DBError = "error";
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return error;
  }

  // ian: unused?
  @GET
  @Path("/isActive")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object isActive(@QueryParam("user") String username) {
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    PDBerror error = new PDBerror();
    error.DBError = "false";
    try {
      c = dbConfig.getConnection();
      statement = c.createStatement();
      rs = statement.executeQuery("SELECT * FROM gtfs_pg_users WHERE username = '" + username + "';");
      if (rs.next()) {
        error.DBError = rs.getString("active");
      }
    } catch (SQLException e) {
      logger.error(e);
      error.DBError = "error";
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return error;
  }


  /**
   * Changes the playground passkey. Delete this method from the server!!
   * @param password
   * @return
   * @throws UnsupportedEncodingException
   * @throws NoSuchAlgorithmException
   */
  // ian: unused?
   /*
  @GET
  @Path("/makePassKey")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object makePassKey(@QueryParam("pass") String password) 
        throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException{
  
  byte[] passByte = password.getBytes("UTF-8");
  MessageDigest md = MessageDigest.getInstance("MD5");
  passByte = md.digest(passByte);
  String pass = new String(passByte, "UTF-8");
  
  String root = new File(".").getAbsolutePath();
    root = removeLastChar(root);
    File passFile = new File(basePath + "TNAtoolAPI-Webapp/WebContent/playground/pass.txt");
  BufferedWriter writer = new BufferedWriter(new FileWriter(passFile));
  
  writer.write(pass);
  
  writer.close();
  return "";
  }
  */

  // ian: unused?
  @GET
  @Path("/validateUser")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object validateUser(@QueryParam("user") String user, @QueryParam("pass") String password)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {

    byte[] passByte = password.getBytes("UTF-8");
    MessageDigest md = MessageDigest.getInstance("MD5");
    passByte = md.digest(passByte);
    String pass = new String(passByte, "UTF-8");

    Connection c = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    PDBerror error = new PDBerror();
    error.DBError = "false";
    try {
      c = dbConfig.getConnection();
      statement = c.prepareStatement("SELECT * FROM gtfs_pg_users WHERE (username = ? or email = ?) and password = ?;");
      statement.setString(1, user);
      statement.setString(2, user);
      statement.setString(3, pass);
      rs = statement.executeQuery();

      if (rs.next()) {
        error.DBError = rs.getString("username");
      }

    } catch (SQLException e) {
      logger.error(e);
      error.DBError = e.getMessage();
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return error;
  }

  // ian: unused?
  @GET
  @Path("/addUser")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addUser(@QueryParam("user") String user, @QueryParam("pass") String password,
      @QueryParam("email") String email, @QueryParam("firstname") String firstname,
      @QueryParam("lastname") String lastname) throws UnsupportedEncodingException, NoSuchAlgorithmException {

    byte[] passByte = password.getBytes("UTF-8");
    MessageDigest md = MessageDigest.getInstance("MD5");
    passByte = md.digest(passByte);
    String pass = new String(passByte, "UTF-8");

    long millis = System.currentTimeMillis() % 1000000;

    Connection c = null;
    PreparedStatement statement = null;
    PDBerror error = new PDBerror();
    error.DBError = "";
    try {
      c = dbConfig.getConnection();
      statement = c.prepareStatement(
          "INSERT INTO gtfs_pg_users (username,password,email,firstname,lastname,quota,usedspace,active,key) "
              + "VALUES (?,?,?,?,?,?,?,?,?);");
      statement.setString(1, user);
      statement.setString(2, pass);
      statement.setString(3, email);
      statement.setString(4, firstname);
      statement.setString(5, lastname);
      statement.setInt(6, QUOTA);
      statement.setInt(7, 0);
      statement.setBoolean(8, false);
      statement.setFloat(9, millis);
      statement.executeUpdate();
      error.DBError = "true";
    } catch (SQLException e) {
      logger.error(e);
      error.DBError = e.getMessage();
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return error;
  }

  @GET
  @Path("/checkInput")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkInput(@QueryParam("dbname") String dbname, @QueryParam("cURL") String cURL,
      @QueryParam("db") String db, @QueryParam("user") String user, @QueryParam("pass") String pass,
      @QueryParam("oldURL") String oldURL, @QueryParam("olddbname") String olddbname) throws IOException {
    PDBerror b = new PDBerror();
    b.DBError = "true";
    List<String> dbnames = new ArrayList<String>(); 
    List<String> urls = new ArrayList<String>();
    for (DatabaseConfig dbc : DatabaseConfig.getConfigs()) {
      dbnames.add(dbc.getDbName());
      urls.add(dbc.getConnectionUrl());
    }
    if (!olddbname.equals(dbname) && dbnames.contains(dbname)) {
      b.DBError = "Database display name \"" + dbname + "\" already exists.";
    } else if (!oldURL.equals(cURL + db) && urls.contains(cURL + db)) {
      b.DBError = "The connection \"" + cURL.split("//")[1] + db + "\" already exists";
    }

    Connection c = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(cURL, user, pass);
    } catch (SQLException e) {
      logger.error(e);
      b.DBError = e.getMessage();
    } finally {
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return b;
  }

  @GET
  @Path("/deleteDB")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteDB(@QueryParam("index") String i) throws IOException {
    DatabaseConfig dbConfig = DatabaseConfig.getConfig(i);
    PDBerror b = new PDBerror();
    b.DBError = "";
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;

    String name = dbConfig.getDatabase();
    String dropurl = "jdbc:postgresql://" + dbConfig.getHost() + ":" + dbConfig.getPort() + "/";
    try {
      c = PgisEventManager.makeConnectionByUrl(dropurl, dbConfig.getUsername(), dbConfig.getPassword());
      statement = c.createStatement();
      rs = statement.executeQuery("select pg_terminate_backend(pid) from pg_stat_activity where datname='" + name + "'");
      statement.executeUpdate("DROP DATABASE " + name);
      b.DBError = "Database was successfully deleted";
      // remove config
      DatabaseConfig.removeConfig(i);
      DatabaseConfig.saveDbInfo();  
    } catch (SQLException e) {
      logger.error(e);
      b.DBError = e.getMessage();
    } catch (IOException e) {
      // ian todo: fatal error
      e.printStackTrace();
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    logger.debug(b.DBError);
    updateDatabaseStaticInfo(true);
    return b;
  }

  @GET
  @Path("/checkForDeactivated")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkForDeactivated(@QueryParam("db") String db) {
    String b = "false";
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    String[] dbInfo = db.split(",");
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement.executeQuery("SELECT activated FROM database_status");
      rs.next();
      boolean bb = rs.getBoolean("activated");
      if (!bb) {
        b = "true";
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return b;
  }

  @GET
  @Path("/checkDBStatus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkDBStatus(@QueryParam("db") String db) {
    DatabaseStatus dbstat = new DatabaseStatus();
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    String[] dbInfo = db.split(",");
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement.executeQuery("SELECT * FROM database_status");
      rs.next();
      dbstat.Activated = rs.getBoolean("activated");
      dbstat.Census = rs.getBoolean("census");
      dbstat.Employment = rs.getBoolean("employment");
      dbstat.FutureEmp = rs.getBoolean("future_emp");
      dbstat.FuturePop = rs.getBoolean("future_pop");
      dbstat.GtfsFeeds = rs.getBoolean("gtfs_feeds");
      dbstat.Parknride = rs.getBoolean("parknride");
      dbstat.Title6 = rs.getBoolean("title6");
      dbstat.Updated = rs.getBoolean("update_process");
      dbstat.Region = rs.getBoolean("region");
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return dbstat;
  }

  @GET
  @Path("/updateDB")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object updateDB(@QueryParam("db") String db, @QueryParam("oldName") String oldName,
      @QueryParam("oldcfgSpatial") String oldcfgSpatial, @QueryParam("oldcfgTransit") String oldcfgTransit)
      throws IOException {
    String[] dbInfo = db.split(",");

    String[] p = dbInfo[4].split("/");
    String name = p[p.length - 1];
    String url = "";
    for (int k = 0; k < p.length - 1; k++) {
      url += p[k] + "/";
    }
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    PDBerror error = new PDBerror();
    error.DBError = "";
    try {
      c = DriverManager.getConnection(url, dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement
          .executeQuery("select pg_terminate_backend(pid) from pg_stat_activity where datname='" + oldName + "'");
      statement.executeUpdate("ALTER DATABASE " + oldName + " RENAME TO " + name);
      error.DBError = "Database was successfully updated";
      // ian: todo: update config
    } catch (SQLException e) {
      logger.error(e);
      error.DBError = e.getMessage();
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return error;
  }

  @GET
  @Path("/addDB")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addDB(@QueryParam("db") String db) {
    String[] dbInfo = db.split(",");
    PDBerror error = new PDBerror();
    error.DBError = "";
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setDatabaseIndex(dbInfo[0]);
    dbConfig.setDbName(dbInfo[1]);
    dbConfig.setConnectionUrl(dbInfo[4]);
    dbConfig.setUsername(dbInfo[5]);
    dbConfig.setPassword(dbInfo[6]);
    String name = dbConfig.getDatabase();
    String createurl = "jdbc:postgresql://" + dbConfig.getHost() + ":" + dbConfig.getPort() + "/";

    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(createurl, dbConfig.getUsername(), dbConfig.getPassword());
      statement = c.createStatement();
      statement.executeUpdate("CREATE DATABASE " + name);
      statement.close();
      c.close();

      c = dbConfig.getConnection();
      statement = c.createStatement();
      statement.executeUpdate("CREATE EXTENSION IF NOT EXISTS postgis;");
      statement.executeUpdate("DROP TABLE IF EXISTS database_status;");
      statement.executeUpdate("CREATE TABLE database_status (" + "name character varying(255) NOT NULL,"
          + "activated boolean," + "gtfs_feeds boolean," + "census boolean," + "employment boolean,"
          + "parknride boolean," + "title6 boolean," + "future_emp boolean," + "future_pop boolean," + "region boolean,"
          + "update_process boolean," + "CONSTRAINT database_status_pkey PRIMARY KEY (name));");
      statement.executeUpdate("INSERT INTO database_status " + "VALUES ('" + name
          + "', false, false, false, false, false, false, false, false, false, false);");
      statement.executeUpdate("CREATE TABLE database_metadata (" + "stateid character varying(2)," + "census text,"
          + "employment text," + "parknride text," + "title6 text," + "future_emp text," + "future_pop text,"
          + "region text," + "CONSTRAINT database_metadata_pkey PRIMARY KEY (stateid));");
      UpdateEventManager.createTables(c, dbInfo);

      statement.executeUpdate(
          "insert into gtfs_pg_users (username,email,firstname,lastname,quota,usedspace,password,active,key) "
              + "VALUES ('admin','admin','','',1,0,'1234',false,1234);");

      error.DBError = "Database was successfully added";

      DatabaseConfig.addConfig(dbConfig);
      DatabaseConfig.saveDbInfo();      
    } catch (SQLException e) {
      logger.error(e);
      error.DBError = e.getMessage();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    logger.debug(error.DBError);
    return error;
  }

  @GET
  @Path("/addExistingDB")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addExistingDB(@QueryParam("db") String db) {
    String[] dbInfo = db.split(",");
    String[] p;

    //		PDBerror error = new PDBerror();
    String DBError = "";

    boolean b = false;
    p = dbInfo[4].split("/");
    String name = p[p.length - 1];
    String url = "";
    for (int k = 0; k < p.length - 1; k++) {
      url += p[k] + "/";
    }
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    try {
      c = DriverManager.getConnection(url, dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement.executeQuery("SELECT 1 AS result FROM pg_database WHERE datname='" + name + "';");

      if (rs.next()) {
        if (rs.getInt("result") == 1) {
          DBError = "Database was successfully added";
        } else {
          DBError = "Database " + name + " could not be found.";
        }
      }
      // ian: todo: update config
    } catch (SQLException e) {
      logger.error(e);
      // } catch (IOException e) {
    //   // TODO Auto-generated catch block
    //   e.printStackTrace();
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    logger.debug(DBError);
    return DBError;
  }

  @GET
  @Path("/checkGTFSstatus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkGTFSstatus(@QueryParam("db") String db) {
    String response = "false";

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement.executeQuery("SELECT * FROM gtfs_feed_info limit 1;");

      if (rs.next()) {
        response = "true";
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return response;
  }

  @GET
  @Path("/checkUpdatestatus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkUpdatestatus(@QueryParam("db") String db) {
    String response = "false";

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement.executeQuery("SELECT * FROM gtfs_uploaded_feeds where updated=FALSE limit 1;");

      if (!rs.next()) {
        response = "true";
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return response;
  }

  @GET
  @Path("/checkT6status")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkT6status(@QueryParam("db") String db) {
    //		String response = "false";

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    PDBerror response = new PDBerror();
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement.executeQuery(
          "SELECT distinct(Left(blockid,2)) stateid from title_vi_blocks_float WHERE with_disability IS NOT NULL order by stateid;");

      while (rs.next()) {
        response.stateids.add(rs.getString("stateid"));
      }
      for (String id : response.stateids) {
        rs = statement.executeQuery("SELECT sname FROM census_states WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.states.add(rs.getString("sname"));
        }
        rs = statement.executeQuery("SELECT title6 FROM database_metadata WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.metadata.add(rs.getString("title6"));
        }
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return response;
  }

  @GET
  @Path("/checkPNRstatus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkPNRstatus(@QueryParam("db") String db) {
    //		String response = "false";

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    PDBerror response = new PDBerror();
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement.executeQuery("SELECT distinct(Left(countyid,2)) stateid FROM parknride order by stateid;");

      while (rs.next()) {
        response.stateids.add(rs.getString("stateid"));
      }
      for (String id : response.stateids) {
        rs = statement.executeQuery("SELECT sname FROM census_states WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.states.add(rs.getString("sname"));
        }
        rs = statement.executeQuery("SELECT parknride FROM database_metadata WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.metadata.add(rs.getString("parknride"));
        }
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return response;
  }

  @GET
  @Path("/deletePNR")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deletePNR(@QueryParam("db") String db, @QueryParam("stateid") String stateid) {
    String message = "done";

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      statement.executeUpdate("DELETE FROM parknride WHERE left(countyid,2)='" + stateid + "';");
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  @GET
  @Path("/deleteT6")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteT6(@QueryParam("db") String db, @QueryParam("stateid") String stateid) {
    String message = "done";

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      statement.executeUpdate("DELETE FROM title_vi_blocks_float WHERE left(blockid,2)='" + stateid + "';");
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  @GET
  @Path("/deleteEmpWac")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteEmpWac(@QueryParam("db") String db, @QueryParam("stateid") String stateid) {
    String message = "done";

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      //			statement.executeUpdate("DROP TABLE IF EXISTS lodes_blocks_rac;");
      statement.executeUpdate("DELETE FROM lodes_blocks_wac WHERE left(blockid,2)='" + stateid + "';");
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  @GET
  @Path("/deleteEmpRac")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteEmpRac(@QueryParam("db") String db, @QueryParam("stateid") String stateid) {
    String message = "done";

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      //			statement.executeUpdate("DROP TABLE IF EXISTS lodes_blocks_rac;");
      statement.executeUpdate("DELETE FROM lodes_blocks_rac WHERE left(blockid,2)='" + stateid + "';");
      statement.executeUpdate("VACUUM");

    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  @GET
  @Path("/deletefEmp")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deletefEmp(@QueryParam("db") String db, @QueryParam("stateid") String stateid) {
    String message = "done";

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      statement.executeUpdate("DELETE FROM lodes_rac_projection_block WHERE left(blockid,2)='" + stateid + "';");
      statement.executeUpdate("DELETE FROM lodes_rac_projection_county WHERE left(countyid,2)='" + stateid + "';");
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  @GET
  @Path("/deletefPop")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deletefPop(@QueryParam("db") String db) {
    String message = "done";

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      statement.executeUpdate("UPDATE census_blocks SET "
          + "population2015 = NULL, population2020 = NULL, population2025 = NULL, population2030 = NULL, "
          + "population2035 = NULL, population2040 = NULL, population2045 = NULL, population2050 = NULL;");
      statement.executeUpdate("UPDATE census_tracts SET "
          + "population2015 = NULL, population2020 = NULL, population2025 = NULL, population2030 = NULL, "
          + "population2035 = NULL, population2040 = NULL, population2045 = NULL, population2050 = NULL;");
      statement.executeUpdate("UPDATE census_counties SET "
          + "population2015 = NULL, population2020 = NULL, population2025 = NULL, population2030 = NULL, "
          + "population2035 = NULL, population2040 = NULL, population2045 = NULL, population2050 = NULL;");
      statement.executeUpdate("UPDATE census_congdists SET "
          + "population2015 = NULL, population2020 = NULL, population2025 = NULL, population2030 = NULL, "
          + "population2035 = NULL, population2040 = NULL, population2045 = NULL, population2050 = NULL;");
      statement.executeUpdate("UPDATE census_places SET "
          + "population2015 = NULL, population2020 = NULL, population2025 = NULL, population2030 = NULL, "
          + "population2035 = NULL, population2040 = NULL, population2045 = NULL, population2050 = NULL;");
      statement.executeUpdate("UPDATE census_urbans SET "
          + "population2015 = NULL, population2020 = NULL, population2025 = NULL, population2030 = NULL, "
          + "population2035 = NULL, population2040 = NULL, population2045 = NULL, population2050 = NULL;");

    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
      //			e.printStackTrace();
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  @GET
  @Path("/checkEmpstatus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkEmpstatus(@QueryParam("db") String db) {
    //		String response = "false";
    boolean rac = false;
    boolean wac = false;
    ResultSet rs = null;
    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    PDBerror response = new PDBerror();
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement.executeQuery("SELECT distinct(Left(blockid,2)) stateid FROM lodes_blocks_wac order by stateid;");
      while (rs.next()) {
        response.stateids.add(rs.getString("stateid"));
      }
      for (String id : response.stateids) {
        rs = statement.executeQuery("SELECT sname FROM census_states WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.states.add(rs.getString("sname"));
        }
        rs = statement.executeQuery("SELECT employment FROM database_metadata WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.metadata.add(rs.getString("employment"));
        }
      }
      rs = statement.executeQuery("SELECT distinct(Left(blockid,2)) stateid FROM lodes_blocks_rac order by stateid;");
      while (rs.next()) {
        response.agencies.add(rs.getString("stateid"));
      }
      for (String id : response.agencies) {
        rs = statement.executeQuery("SELECT sname FROM census_states WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.feeds.add(rs.getString("sname"));
        }
        rs = statement.executeQuery("SELECT employment FROM database_metadata WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.sizes.add(rs.getString("employment"));
        }
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    //		if(wac && rac){
    //			response = "true";
    //		}

    return response;
  }

  @GET
  @Path("/checkfEmpstatus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkfEmpstatus(@QueryParam("db") String db) {
    //		String response = "false";
    boolean rac = false;
    boolean wac = false;
    ResultSet rs = null;
    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    PDBerror response = new PDBerror();
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement
          .executeQuery("SELECT distinct(Left(blockid,2)) stateid FROM lodes_rac_projection_block order by stateid;");
      while (rs.next()) {
        response.stateids.add(rs.getString("stateid"));
      }
      for (String id : response.stateids) {
        rs = statement.executeQuery("SELECT sname FROM census_states WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.states.add(rs.getString("sname"));
        }
        rs = statement.executeQuery("SELECT future_emp FROM database_metadata WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.metadata.add(rs.getString("future_emp"));
        }
      }
      //			rs = statement.executeQuery("SELECT * FROM lodes_rac_projection_county limit 1;");
      //			if(rs.next()){
      //				rac = true;
      //			}
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    //		if(wac && rac){
    //			response = "true";
    //		}

    return response;
  }

  @GET
  @Path("/checkFpopstatus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkFpopstatus(@QueryParam("db") String db) {
    //		String response = "false";
    boolean census_blocks = false;
    boolean census_congdists = false;
    boolean census_counties = false;
    boolean census_places = false;
    boolean census_tracts = false;
    boolean census_urbans = false;
    boolean census_states = false;
    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    Integer value;
    PDBerror response = new PDBerror();
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();

      //			rs = statement.executeQuery("SELECT population2040 FROM census_blocks where population2040 is null limit 1;");
      //			if(!rs.next()){
      //				census_blocks = true;
      //			}
      //			
      //			rs = statement.executeQuery("SELECT population2040 FROM census_states where population2040 is null limit 1;");
      //			if(!rs.next()){
      //				census_states = true;
      //			}
      //			
      //			rs = statement.executeQuery("SELECT population2040 FROM census_congdists where population2040 is null limit 1;");
      //			if(!rs.next()){
      //				census_congdists = true;
      //			}
      //			
      //			rs = statement.executeQuery("SELECT population2040 FROM census_counties where population2040 is null limit 1;");
      //			if(!rs.next()){
      //				census_counties = true;
      //			}
      //			
      //			rs = statement.executeQuery("SELECT population2040 FROM census_places where population2040 is null limit 1;");
      //			if(!rs.next()){
      //				census_places = true;
      //			}
      //			
      //			rs = statement.executeQuery("SELECT population2040 FROM census_tracts where population2040 is null limit 1;");
      //			if(!rs.next()){
      //				census_tracts = true;
      //			}

      rs = statement.executeQuery(
          "SELECT distinct(stateid) FROM census_tracts where population2040 is not null order by stateid;");
      while (rs.next()) {
        response.stateids.add(rs.getString("stateid"));
      }
      for (String id : response.stateids) {
        rs = statement.executeQuery("SELECT sname FROM census_states WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.states.add(rs.getString("sname"));
        }
        rs = statement.executeQuery("SELECT future_pop FROM database_metadata WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.metadata.add(rs.getString("future_pop"));
        }
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    //		if(census_states && census_blocks && census_congdists && census_counties && census_places && census_tracts && census_urbans){
    //			response = "true";
    //		}

    return response;
  }

  @GET
  @Path("/checkRegionstatus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkRegionstatus(@QueryParam("db") String db) {
    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    PDBerror response = new PDBerror();
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();

      rs = statement
          .executeQuery("SELECT distinct(stateid) FROM census_blocks WHERE regionid is not null order by stateid;");
      while (rs.next()) {
        response.stateids.add(rs.getString("stateid"));
      }
      for (String id : response.stateids) {
        rs = statement.executeQuery("SELECT sname FROM census_states WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.states.add(rs.getString("sname"));
        }
        rs = statement.executeQuery("SELECT region FROM database_metadata WHERE stateid='" + id + "';");
        if (rs.next()) {
          response.metadata.add(rs.getString("region"));
        }
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    //		if(census_states && census_blocks && census_congdists && census_counties && census_places && census_tracts && census_urbans){
    //			response = "true";
    //		}

    return response;
  }

  @GET
  @Path("/checkCensusstatus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkCensusstatus(@QueryParam("db") String db) {
    String response = "false";
    boolean census_blocks = false;
    boolean census_congdists = false;
    boolean census_counties = false;
    boolean census_places = false;
    boolean census_tracts = false;
    boolean census_urbans = false;
    boolean census_states = false;
    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();

      rs = statement.executeQuery("SELECT * FROM census_blocks limit 1;");
      if (rs.next()) {
        census_blocks = true;
      }

      rs = statement.executeQuery("SELECT * FROM census_states limit 1;");
      if (rs.next()) {
        census_states = true;
      }

      rs = statement.executeQuery("SELECT * FROM census_congdists limit 1;");
      if (rs.next()) {
        census_congdists = true;
      }

      rs = statement.executeQuery("SELECT * FROM census_counties limit 1;");
      if (rs.next()) {
        census_counties = true;
      }

      rs = statement.executeQuery("SELECT * FROM census_places limit 1;");
      if (rs.next()) {
        census_places = true;
      }

      rs = statement.executeQuery("SELECT * FROM census_tracts limit 1;");
      if (rs.next()) {
        census_tracts = true;
      }

      rs = statement.executeQuery("SELECT * FROM census_urbans limit 1;");
      if (rs.next()) {
        census_urbans = true;
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    if (census_states && census_blocks && census_congdists && census_counties && census_places && census_tracts
        && census_urbans) {
      response = "true";
    }

    return response;
  }

  @GET
  @Path("/copyCensus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object copyCensus(@QueryParam("dbFrom") String dbFrom, @QueryParam("dbTo") String dbTo,
      @QueryParam("section") String section) {
    String tables;
    String[] dbInfoFrom = dbFrom.split(",");
    String[] p;
    p = dbInfoFrom[4].split("/");
    String nameFrom = p[p.length - 1];

    String[] dbInfoTo = dbTo.split(",");
    p = dbInfoTo[4].split("/");
    String nameTo = p[p.length - 1];

    String[] dbInfo = dbFrom.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    ArrayList<String> stateids = new ArrayList<String>();
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement.executeQuery("SELECT DISTINCT(stateid) states FROM census_states;");
      while (rs.next()) {
        stateids.add(rs.getString("states"));
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    dbInfo = dbTo.split(",");
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      switch (section) {
      case "census":
        statement.executeUpdate("DROP TABLE IF EXISTS census_blocks;");
        statement.executeUpdate("DROP TABLE IF EXISTS census_states;");
        statement.executeUpdate("DROP TABLE IF EXISTS census_congdists;");
        statement.executeUpdate("DROP TABLE IF EXISTS census_counties;");
        statement.executeUpdate("DROP TABLE IF EXISTS census_places;");
        statement.executeUpdate("DROP TABLE IF EXISTS census_tracts;");
        statement.executeUpdate("DROP TABLE IF EXISTS census_urbans;");
        for (String state : stateids) {
          addMetadata(state, "Copied from " + nameFrom, c, "census");
          addMetadata(state, "Copied from " + nameFrom, c, "future_pop");
          addMetadata(state, "Copied from " + nameFrom, c, "region");
        }

        break;
      case "employment":
        statement.executeUpdate("DROP TABLE IF EXISTS lodes_blocks_rac;");
        statement.executeUpdate("DROP TABLE IF EXISTS lodes_blocks_wac;");
        for (String state : stateids) {
          addMetadata(state, "Copied from " + nameFrom, c, "employment");
        }
        break;
      case "parknride":
        statement.executeUpdate("DROP TABLE IF EXISTS parknride;");
        for (String state : stateids) {
          addMetadata(state, "Copied from " + nameFrom, c, "parknride");
        }
        break;
      case "title6":
        statement.executeUpdate("DROP TABLE IF EXISTS title_vi_blocks_float;");
        for (String state : stateids) {
          addMetadata(state, "Copied from " + nameFrom, c, "title6");
        }
        break;
      case "femployment":
        statement.executeUpdate("DROP TABLE IF EXISTS lodes_rac_projection_block;");
        statement.executeUpdate("DROP TABLE IF EXISTS lodes_rac_projection_county;");
        for (String state : stateids) {
          addMetadata(state, "Copied from " + nameFrom, c, "future_emp");
        }
        break;
      default:
        break;
      }

    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    switch (section) {
    case "census":
      tables = "census_blocks census_states census_congdists census_counties census_places census_tracts census_urbans";
      break;
    case "employment":
      tables = "lodes_blocks_rac lodes_blocks_wac";
      break;
    case "parknride":
      tables = "parknride";
      break;
    case "title6":
      tables = "title_vi_blocks_float";
      break;
    case "femployment":
      tables = "lodes_rac_projection_block lodes_rac_projection_county";
      break;
    default:
      tables = "";
      break;
    }

    String[] tableArray = tables.trim().split("\\s+");
    for (String tableName: tableArray) {
      copyTable(tableName, dbInfoFrom[4], dbInfoTo[4], dbInfo[5], dbInfo[6]);
    }

    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return "done";
  }

  @GET
  @Path("/removeCensus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object removeCensus(@QueryParam("stateid") String stateid, @QueryParam("states") String states,
      @QueryParam("db") String db) throws IOException {
    String message = "done";
    StateInits st = new StateInits();

    String[] stateids = states.split(",");
    String sql = "";
    for (String str : stateids) {
      if (!str.equals(stateid)) {
        sql += " AND uname NOT LIKE '%" + st.stateInitials.get(str) + "%'";
      }
    }

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();
      statement.executeUpdate("DELETE FROM census_states WHERE stateid = '" + stateid + "';");
      statement.executeUpdate("DELETE FROM census_congdists WHERE stateid = '" + stateid + "';");
      statement.executeUpdate("DELETE FROM census_counties WHERE stateid = '" + stateid + "';");
      statement.executeUpdate("DELETE FROM census_tracts WHERE stateid = '" + stateid + "';");
      statement.executeUpdate("DELETE FROM census_places WHERE stateid = '" + stateid + "';");
      statement.executeUpdate("DELETE FROM census_blocks WHERE stateid = '" + stateid + "';");
      statement.executeUpdate(
          "DELETE FROM census_urbans WHERE uname LIKE '%" + st.stateInitials.get(stateid) + "%'" + sql + ";");
      statement.executeUpdate("VACUUM;");

    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  public void addMetadata(String stateid, String metadata, Connection c, String field) throws SQLException {
    Statement statement = c.createStatement();
    ResultSet rs = statement.executeQuery("SELECT * FROM database_metadata WHERE stateid='" + stateid + "';");
    if (rs.next()) {
      statement.executeUpdate(
          "UPDATE database_metadata SET " + field + "='" + metadata + "' WHERE stateid='" + stateid + "';");
    } else {
      statement.executeUpdate(
          "INSERT INTO database_metadata (stateid," + field + ") VALUES('" + stateid + "','" + metadata + "');");
    }
  }

  @GET
  @Path("/importCensus")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object importCensus(@QueryParam("stateid") String stateid, @QueryParam("db") String db,
      @QueryParam("metadata") String metadata) throws IOException {
    String[] dbInfo = db.split(",");
    String[] states = stateid.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    String message = "";
    String[] dbURL = dbInfo[4].split("/");
    dbURL[dbURL.length - 1] = "census_reference";

    dbInfo[4] = "";
    for (int i = 0; i < dbURL.length - 1; i++) {
      dbInfo[4] += dbURL[i] + "/";
    }
    dbInfo[4] += dbURL[dbURL.length - 1];
    StateInits st = new StateInits();
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();
      statement.executeUpdate("TRUNCATE TABLE census_states;");
      statement.executeUpdate("TRUNCATE TABLE census_counties;");
      statement.executeUpdate("TRUNCATE TABLE census_congdists;");
      statement.executeUpdate("TRUNCATE TABLE census_tracts;");
      statement.executeUpdate("TRUNCATE TABLE census_places;");
      statement.executeUpdate("TRUNCATE TABLE census_urbans;");
      statement.executeUpdate("TRUNCATE TABLE census_blocks;");
      statement.executeUpdate("VACUUM;");
      for (String state : states) {
        statement.executeUpdate(
            "INSERT INTO census_states select * FROM census_states_ref WHERE stateid = '" + state + "';");
        statement.executeUpdate(
            "INSERT INTO census_congdists select * FROM census_congdists_ref WHERE stateid = '" + state + "';");
        statement.executeUpdate(
            "INSERT INTO census_counties select * FROM census_counties_ref WHERE stateid = '" + state + "';");
        statement.executeUpdate(
            "INSERT INTO census_tracts select * FROM census_tracts_ref WHERE stateid = '" + state + "';");
        statement.executeUpdate(
            "INSERT INTO census_places select * FROM census_places_ref WHERE stateid = '" + state + "';");
        statement.executeUpdate(
            "INSERT INTO census_blocks select * FROM census_blocks_ref WHERE stateid = '" + state + "';");
        statement.executeUpdate("INSERT INTO census_urbans select * FROM census_urbans_ref WHERE uname LIKE '%"
            + st.stateInitials.get(state) + "%' ON CONFLICT DO NOTHING;");
      }

    } catch (SQLException e) {
      logger.error(e);
      message += e.getMessage();
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    dbInfo = db.split(",");
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();
      statement.executeUpdate("DROP TABLE IF EXISTS census_states;");
      statement.executeUpdate("DROP TABLE IF EXISTS census_congdists;");
      statement.executeUpdate("DROP TABLE IF EXISTS census_counties;");
      statement.executeUpdate("DROP TABLE IF EXISTS census_tracts;");
      statement.executeUpdate("DROP TABLE IF EXISTS census_places;");
      statement.executeUpdate("DROP TABLE IF EXISTS census_urbans;");
      statement.executeUpdate("DROP TABLE IF EXISTS census_blocks;");
      statement.executeUpdate("UPDATE gtfs_uploaded_feeds SET updated = FALSE;");
      for (String state : states) {
        addMetadata(state, metadata, c, "census");
      }

    } catch (SQLException e) {
      logger.error(e);
      message += e.getMessage();
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    if (message.equals("")) {
      message = "done";
    }

    String[] p;
    p = dbInfo[4].split("/");
    String nameFrom = "census_reference";
    String nameTo = p[p.length - 1];

    Process pr;

    String fromHost = dbInfo[4].split(":")[2];
    fromHost = fromHost.substring(2);
    String fromUser = dbInfo[5];
    String fromPass = dbInfo[6];

    String toHost = fromHost;
    String toUser = fromUser;
    String toPass = fromPass;
    fromHost = "localhost"; //to be deleted
    toHost = "localhost"; //to be deleted

    String tables = "census_blocks census_states census_congdists census_counties census_places census_tracts census_urbans";

    String[] tableArray = tables.trim().split("\\s+");
    for (String tableName: tableArray) {
      copyTable(tableName, dbInfo[4], dbInfo[4], dbInfo[5], dbInfo[6]);
    }

    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();
      statement.executeUpdate("VACUUM;");

    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  private void parseXmlFile(File xmlFile, File dstFile, String[] dbInfo, boolean b) {

    //		File xmlFile = new File(srcFile);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;
    try {
      dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(xmlFile);
      doc.getDocumentElement().normalize();

      NodeList props = doc.getElementsByTagName("property");
      Element prop = null;

      for (int i = 0; i < 3/*props.getLength()*/; i++) {
        prop = (Element) props.item(i + 1);
        prop.appendChild(doc.createTextNode(dbInfo[i + 4]));
      }

      NodeList mappings = doc.getElementsByTagName("mapping");
      Element map = null;

      if (b) {
        map = (Element) mappings.item(0);
        map.setAttribute("resource", dbInfo[7]);
      } else {
        for (int i = 0; i < 2; i++) {
          map = (Element) mappings.item(i);
          map.setAttribute("resource", dbInfo[i + 8]);
        }
      }

      XMLSerializer serializer = new XMLSerializer();
      serializer.setOutputCharStream(new java.io.FileWriter(dstFile));
      OutputFormat format = new OutputFormat();
      format.setStandalone(true);
      serializer.setOutputFormat(format);
      serializer.serialize(doc);

    } catch (ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch (SAXException se) {
      se.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  @GET
  @Path("/deletefeed")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deletefeed(@QueryParam("feedname") String feedname, @QueryParam("db") String db) throws IOException {

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    String agencyId = "";
    String agencyIds = "";
    String[] agencyIdList;
    String message = "done";

    String[][] defAgencyIds = { { "census_congdists_trip_map", "agencyid_def" },
        { "census_states_trip_map", "agencyid_def" }, { "census_places_trip_map", "agencyid_def" },
        { "census_urbans_trip_map", "agencyid_def" }, { "census_counties_trip_map", "agencyid_def" },
        { "census_tracts_trip_map", "agencyid_def" }, { "gtfs_fare_rules", "fare_agencyid" },
        { "gtfs_fare_attributes", "agencyid" }, { "gtfs_trip_stops", "stop_agencyid_origin" },
        { "gtfs_stop_service_map", "agencyid_def" }, { "gtfs_route_serviceid_map", "agencyid_def" },
        { "gtfs_stop_route_map", "agencyid_def" }, { "gtfs_frequencies", "defaultid" }, { "gtfs_pathways", "agencyid" },
        { "gtfs_shape_points", "shapeid_agencyid" }, { "gtfs_stop_times", "stop_agencyid" },
        { "gtfs_transfers", "defaultid" }, { "tempstopcodes", "agencyid" }, { "tempetriptimes", "agencyid" },
        { "tempestshapes", "agencyid" }, { "tempshapes", "agencyid" }, { "gtfs_trips", "serviceid_agencyid" },
        { "gtfs_calendar_dates", "serviceid_agencyid" }, { "gtfs_calendars", "serviceid_agencyid" },
        { "gtfs_stops", "agencyid" }, { "gtfs_routes", "defaultid" }, { "gtfs_agencies", "defaultid" },
        { "gtfs_feed_info", "defaultid" } };

    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();

      statement.executeUpdate("DELETE FROM gtfs_uploaded_feeds WHERE feedname = '" + feedname + "';");
      statement.executeUpdate("DELETE FROM gtfs_uploaded_feeds WHERE feedname IS NULL;");

      rs = statement.executeQuery("SELECT defaultid FROM gtfs_feed_info where feedname = '" + feedname + "';");
      if (rs.next()) {
        agencyId = rs.getString("defaultid");
      }

      rs = statement.executeQuery("SELECT agencyids FROM gtfs_feed_info where feedname = '" + feedname + "';");
      if (rs.next()) {
        agencyIds = rs.getString("agencyids");
      }
      agencyIdList = agencyIds.split(",");
      //			
      for (int i = 0; i < defAgencyIds.length; i++) {
        logger.debug(defAgencyIds[i][0]);
        try {
          if (defAgencyIds[i][0].startsWith("temp")) {
            statement.executeUpdate(
                "DELETE FROM " + defAgencyIds[i][0] + " WHERE " + sqlString(agencyIdList, defAgencyIds[i][1]) + "';");

          } else {
            statement.executeUpdate("ALTER TABLE " + defAgencyIds[i][0] + " DISABLE TRIGGER ALL;");
            statement.executeUpdate(
                "DELETE FROM " + defAgencyIds[i][0] + " WHERE " + defAgencyIds[i][1] + "='" + agencyId + "';");
            statement.executeUpdate("ALTER TABLE " + defAgencyIds[i][0] + " ENABLE TRIGGER ALL;");
          }

        } catch (SQLException e) {
          logger.error(e);
        }
      }

      agencyId = "";
      agencyIds = "";
      rs = statement.executeQuery("SELECT defaultid FROM gtfs_feed_info where feedname IS NULL;");
      if (rs.next()) {
        agencyId = rs.getString("defaultid");
      }

      rs = statement.executeQuery("SELECT agencyids FROM gtfs_feed_info where feedname IS NULL;");
      if (rs.next()) {
        agencyIds = rs.getString("agencyids");
      }

      if (!agencyIds.equals("")) {
        agencyIdList = agencyIds.split(",");
        //				
        for (int i = 0; i < defAgencyIds.length; i++) {
          logger.debug(defAgencyIds[i][0]);
          try {
            if (defAgencyIds[i][0].startsWith("temp")) {
              statement.executeUpdate(
                  "DELETE FROM " + defAgencyIds[i][0] + " WHERE " + sqlString(agencyIdList, defAgencyIds[i][1]) + "';");

            } else {
              statement.executeUpdate("ALTER TABLE " + defAgencyIds[i][0] + " DISABLE TRIGGER ALL;");
              statement.executeUpdate(
                  "DELETE FROM " + defAgencyIds[i][0] + " WHERE " + defAgencyIds[i][1] + "='" + agencyId + "';");
              statement.executeUpdate("ALTER TABLE " + defAgencyIds[i][0] + " ENABLE TRIGGER ALL;");
            }

          } catch (SQLException e) {
            logger.error(e);
          }
        }
      }

      logger.debug("vacuum start");
      statement.executeUpdate("VACUUM");
      logger.debug("vacuum finish");
    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    message = feedname + "%%" + message;
    return message;
  }

  @GET
  @Path("/deleteUploadedGTFS")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteUploadedGTFS() throws IOException {
    //String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    //File gtfsFolder = new File(path+"../../src/main/webapp/resources/admin/uploads/gtfs");
    File gtfsFolder = new File(DatabaseConfig.getPath("admin", "uploads", "gtfs"));
    File[] files = gtfsFolder.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
    return "done";
  }

  @GET
  @Path("/deleteUploadedPNR")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteUploadedPNR() throws IOException {
    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    File gtfsFolder = new File(path + "../../src/main/webapp/resources/admin/uploads/pnr");
    File[] files = gtfsFolder.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
    return "done";
  }

  @GET
  @Path("/deleteUploadedT6")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteUploadedT6() throws IOException {
    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    File gtfsFolder = new File(path + "../../src/main/webapp/resources/admin/uploads/t6");
    File[] files = gtfsFolder.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
    return "done";
  }

  @GET
  @Path("/deleteUploadedEmp")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteUploadedEmp() throws IOException {
    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    File gtfsFolder = new File(path + "../../src/main/webapp/resources/admin/uploads/emp");
    File[] files = gtfsFolder.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
    return "done";
  }

  @GET
  @Path("/deleteUploadedfEmp")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteUploadedfEmp() throws IOException {
    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    File gtfsFolder = new File(path + "../../src/main/webapp/resources/admin/uploads/femp");
    File[] files = gtfsFolder.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
    return "done";
  }

  @GET
  @Path("/deleteUploadedfPop")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteUploadedfPop() throws IOException {
    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    File gtfsFolder = new File(path + "../../src/main/webapp/resources/admin/uploads/fpop");
    File[] files = gtfsFolder.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
    return "done";
  }

  @GET
  @Path("/deleteUploadedRegion")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteUploadedRegion() throws IOException {
    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    File gtfsFolder = new File(path + "../../src/main/webapp/resources/admin/uploads/region");
    File[] files = gtfsFolder.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
    return "done";
  }

  @GET
  @Path("/deleteProcessGTFS")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object deleteProcessGTFS() throws IOException {
    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    File gtfsFolder = new File(path + "../../src/main/webapp/resources/admin/processFiles/gtfs");
    File[] files = gtfsFolder.listFiles();
    if (files != null) {
      for (File f : files) {
        f.delete();
      }
    }
    return "done";
  }

  @GET
  @Path("/checkDuplicateFeeds")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object checkDuplicateFeeds(@QueryParam("feed") String feed, @QueryParam("db") String db) throws IOException {
    for (int i = 0; i < 4; i++) {
      feed = removeLastChar(feed);
    }
    String b = "false";
    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();

      rs = statement.executeQuery("SELECT * FROM gtfs_feed_info where feedname = '" + feed + "';");
      if (rs.next()) {
        b = "true";
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    return b;
  }

  public String sqlString(String[] ids, String column) {
    String sql = "";
    for (int i = 0; i < ids.length - 1; i++) {
      sql += column + " = '" + ids[i] + "' OR ";
    }
    sql += column + " = '" + ids[ids.length - 1];
    return sql;
  }

  @GET
  @Path("/addfeed")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addfeed(@QueryParam("feedname") String feedname, @QueryParam("feedsize") String feedsize,
      @QueryParam("db") String db) throws IOException {
    String[] args = new String[5];
    String[] dbInfo = db.split(",");
    args[0] = "--driverClass=\"org.postgresql.Driver\"";
    args[1] = "--url=\"" + dbInfo[4] + "\"";
    args[2] = "--username=\"" + dbInfo[5] + "\"";
    args[3] = "--password=\"" + dbInfo[6] + "\"";

    //String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    //File source = new File(path+"../../src/main/webapp/resources/admin/uploads/gtfs/"+feedname);
    File source = new File(DatabaseConfig.getPath("admin", "uploads", "gtfs", feedname));
    //String feed = path+"../../src/main/webapp/resources/admin/processFiles/gtfs/"+feedname;
    // Include dbInfo[0] which is the index number -- this way we save an archive of each file as it's used.
    String feed = DatabaseConfig.getPath("admin", "processFiles", "gtfs", dbInfo[0], feedname);

    File target = new File(feed);
    target.mkdirs(); // FIXME, catch any error here and log. Ed 2017-09-18
    Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    String message = "done";
    args[4] = feed;
    for (int i = 0; i < 4; i++) {
      feedname = removeLastChar(feedname);
    }
    GtfsDatabaseLoaderMain.main(args);
    if (gtfsUpload) {
      gtfsUpload = false;
      message = gtfsMessage;
      gtfsMessage = "";
      target.delete();
      return feedname + "%%" + message;
    }
    gtfsMessage = "";
    gtfsUpload = false;
    /*try{
      GtfsDatabaseLoaderMain.main(args);	
    }catch(Exception e){
      message = e.getMessage();
      logger.debug(target.delete());
      
    //			try{
    //				FileDeleteStrategy.FORCE.delete(target);
    //			}catch(IOException ioe){
    //				ioe.printStackTrace();
    //			}
      
      return feedname+"%%"+message;
    }*/

    //		String[] feedName = feedname.split("/");
    //		String fName = feedName[feedName.length-1];
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    String defaultId = "";
    String agencyNames = "";
    String agencyIds = "";

    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();
      rs = statement.executeQuery("SELECT * FROM gtfs_agencies Where defaultid IS NULL;");
      if (rs.next()) {
        String tmpAgencyId = rs.getString("id");
        rs = statement.executeQuery("SELECT * FROM gtfs_routes where agencyid = '" + tmpAgencyId + "' limit 1;");
        if (rs.next()) {
          defaultId = rs.getString("defaultid");
        }
        statement.executeUpdate("UPDATE gtfs_agencies SET defaultid = '" + defaultId + "' WHERE defaultid IS NULL;");
      }

      rs = statement.executeQuery("SELECT * FROM gtfs_agencies Where added IS NULL;");

      while (rs.next()) {
        defaultId = rs.getString("defaultid");
        agencyNames += rs.getString("name") + ",";
        agencyIds += rs.getString("id") + ",";
      }
      agencyNames = removeLastChar(agencyNames);
      agencyIds = removeLastChar(agencyIds);
      statement.executeUpdate("UPDATE gtfs_agencies SET added='added' WHERE added IS NULL;");

      rs = statement.executeQuery("SELECT * FROM gtfs_feed_info Where defaultid = '" + defaultId + "';");
      if (!rs.next()) {
        rs = statement.executeQuery("SELECT gid FROM gtfs_feed_info;");
        List<String> ids = new ArrayList<String>();
        while (rs.next()) {
          ids.add(rs.getString("gid"));
        }
        int gid;
        int Low = 10000;
        int High = 99999;
        do {
          Random r = new Random();
          gid = r.nextInt(High - Low) + Low;
        } while (ids.contains(Integer.toString(gid)));
        String sql = "INSERT INTO gtfs_feed_info "
            + "(gid,publishername,publisherurl,lang,startdate,enddate,version,defaultid,agencyids,agencynames,feedname) "
            + "VALUES (" + Integer.toString(gid) + ",'N/A','N/A','N/A','N/A','N/A','N/A','" + defaultId + "','"
            + agencyIds + "','" + agencyNames + "','" + feedname + "')";
        statement.executeUpdate(sql);
      } else {
        statement.executeUpdate(
            "UPDATE gtfs_feed_info SET feedname = '" + feedname + "' WHERE defaultid = '" + defaultId + "';");
      }
      statement.executeUpdate(
          "with calendars as (select serviceid_agencyid as agencyid, min(startdate::int) as calstart, max(enddate::int) as calend from gtfs_calendars where serviceid_agencyid='"
              + defaultId + "' group by serviceid_agencyid),"
              + "calendardates as (select serviceid_agencyid as agencyid, min(date::int) as calstart, max(date::int) as calend from gtfs_calendar_dates where serviceid_agencyid='"
              + defaultId + "' group by serviceid_agencyid),"
              + "calendar as (select cals.agencyid, least(cals.calstart, calds.calstart) as calstart, greatest(cals.calend, calds.calend) as calend from calendars cals full join calendardates calds using(agencyid)) "
              + "update gtfs_feed_info set startdate= calendar.calstart::varchar , enddate=calendar.calend::varchar from calendar where defaultid = agencyid;");

      statement.executeUpdate("INSERT INTO gtfs_uploaded_feeds (feedname,username,ispublic,feedsize,updated) "
          + "VALUES ('" + feedname + "','admin',False,'" + feedsize + "', False);");

      //UpdateEventManager.updateTables(To BE DELETED, defaultId);
    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }

      // For Issue #6, do not delete the file, so we can keep it for reference!
      // https://github.com/pouyalireza/TNExT/issues/6/
      // target.delete();
    }

    //		logger.debug("done");
    //		return new TransitError(feedname +"Has been added to the database");
    return feedname + "%%" + message;
  }

  @GET
  @Path("/addPnr")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addPnr(@QueryParam("fileName") String fileName, @QueryParam("db") String db,
      @QueryParam("metadata") String metadata, @QueryParam("stateid") String stateid) throws IOException, SQLException {
    String[] dbInfo = db.split(",");

    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    path = path + "../../src/main/webapp/resources/admin/uploads/pnr/" + fileName;
    path = path.substring(1, path.length());
    File source = new File(path);
    String message = "done";
    Connection c = null;
    Statement statement = null;
    //		ResultSet rs = null;
    c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
    try {
      statement = c.createStatement();
      statement.executeUpdate("CREATE TABLE IF NOT EXISTS parknride(" + "pnrid integer NOT NULL, "
          + "lat double precision NOT NULL," + "lon double precision NOT NULL," + "lotName text," + "location text,"
          + "city character varying (30)," + "zipcode integer," + "countyID character varying(5) NOT NULL,"
          + "county character varying(50) NOT NULL," + "spaces integer," + "accessibleSpaces integer,"
          + "bikeRackSpaces integer," + "bikeLockerSpaces integer," + "electricVehicleSpaces integer,"
          + "carSharing text," + "transitService text, " + "availability text," + "timeLimit text," + "restroom text,"
          + "benches text," + "shelter text," + "indoorWaitingArea text," + "trashCan text," + "lighting text,"
          + "securityCameras text," + "sidewalks text," + "pnrSignage text," + "lotSurface text,"
          + "propertyOwner text," + "localExpert text," + "PRIMARY KEY (pnrid,countyID)" + ")WITH (" + "  OIDS=FALSE"
          + ");");
      statement.executeUpdate("ALTER TABLE parknride" + "  OWNER TO postgres;");
    } catch (SQLException e) {
      logger.error(e);
    }

    try {
      statement = c.createStatement();
      statement.executeUpdate("ALTER TABLE parknride DROP COLUMN IF EXISTS geom;");
      statement.executeUpdate("DROP TABLE IF EXISTS temp_01;");
      statement.executeUpdate("CREATE TABLE temp_01 as (SELECT * FROM parknride LIMIT 1);");
      statement.executeUpdate("TRUNCATE TABLE temp_01;");
    } catch (SQLException e) {
      logger.error(e);
    }

    String[] p;
    p = dbInfo[4].split("/");
    String name = p[p.length - 1];
    String host = dbInfo[4].split(":")[2];
    host = host.substring(2);
    host = "localhost"; //to be deleted
    copySqlCommand("temp_01 (pnrid,lat,lon,lotName,location,city,zipcode,countyID,county,spaces,accessibleSpaces,"
        + "bikeRackSpaces,bikeLockerSpaces,electricVehicleSpaces,carSharing,transitService,availability,timeLimit,"
        + "restroom,benches,shelter,indoorWaitingArea,trashCan,lighting,securityCameras,sidewalks,pnrSignage,lotSurface,propertyOwner,localExpert) ",
        path, dbInfo[4], dbInfo[5], dbInfo[6]);
    try {
      statement = c.createStatement();
      statement.executeUpdate("INSERT INTO parknride SELECT * FROM temp_01 ON CONFLICT DO NOTHING;");
      statement.executeUpdate("DROP TABLE IF EXISTS temp_01;");
    } catch (SQLException e) {
      logger.error(e);
    }
    try {
      statement = c.createStatement();
      statement.executeUpdate("ALTER TABLE parknride " + "ADD COLUMN IF NOT EXISTS geom geometry(Point, 2993);");
    } catch (SQLException e) {
      logger.error(e);
    }
    try {
      statement = c.createStatement();
      statement.executeUpdate(
          "UPDATE parknride " + "SET geom = ST_transform(ST_setsrid(ST_MakePoint(lon, lat),4326), 2993);");
    } catch (SQLException e) {
      logger.error(e);
    }
    try {
      statement = c.createStatement();
      statement.executeUpdate("UPDATE parknride SET lotName='N/A' WHERE lotName IS NULL;");
      statement.executeUpdate("UPDATE parknride SET location='N/A' WHERE location IS NULL;");
      statement.executeUpdate("UPDATE parknride SET city='N/A' WHERE city IS NULL;");
      statement.executeUpdate("UPDATE parknride SET county='N/A' WHERE county IS NULL;");
      statement.executeUpdate("UPDATE parknride SET spaces=0 WHERE spaces IS NULL;");
      statement.executeUpdate("UPDATE parknride SET accessiblespaces=0 WHERE accessiblespaces IS NULL;");
      statement.executeUpdate("UPDATE parknride SET bikerackspaces=0 WHERE bikerackspaces IS NULL;");
      statement.executeUpdate("UPDATE parknride SET bikelockerspaces=0 WHERE bikelockerspaces IS NULL;");
      statement.executeUpdate("UPDATE parknride SET electricvehiclespaces=0 WHERE electricvehiclespaces IS NULL;");
      statement.executeUpdate("UPDATE parknride SET carsharing='N/A' WHERE carsharing IS NULL;");
      statement.executeUpdate("UPDATE parknride SET transitservice='N/A' WHERE transitservice IS NULL;");
      statement.executeUpdate("UPDATE parknride SET availability='N/A' WHERE availability IS NULL;");
      statement.executeUpdate("UPDATE parknride SET timelimit='N/A' WHERE timelimit IS NULL;");
      statement.executeUpdate("UPDATE parknride SET restroom='N/A' WHERE restroom IS NULL;");
      statement.executeUpdate("UPDATE parknride SET shelter='N/A' WHERE shelter IS NULL;");
      statement.executeUpdate("UPDATE parknride SET trashcan='N/A' WHERE trashcan IS NULL;");
      statement.executeUpdate("UPDATE parknride SET lighting='N/A' WHERE lighting IS NULL;");
      statement.executeUpdate("UPDATE parknride SET securitycameras='N/A' WHERE securitycameras IS NULL;");
      statement.executeUpdate("UPDATE parknride SET sidewalks='N/A' WHERE sidewalks IS NULL;");
      statement.executeUpdate("UPDATE parknride SET pnrsignage='N/A' WHERE pnrsignage IS NULL;");
      statement.executeUpdate("UPDATE parknride SET lotsurface='N/A' WHERE lotsurface IS NULL;");
      statement.executeUpdate("UPDATE parknride SET propertyowner='N/A' WHERE propertyowner IS NULL;");
      statement.executeUpdate("UPDATE parknride SET localexpert='N/A' WHERE localexpert IS NULL;");
    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
    } finally {
      source.delete();
    }

    try {
      statement = c.createStatement();
      addMetadata(stateid, metadata, c, "parknride");
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  @GET
  @Path("/addT6")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addT6(@QueryParam("db") String db, @QueryParam("metadata") String metadata,
      @QueryParam("stateid") String stateid) throws SQLException {
    String[] dbInfo = db.split(",");

    String message = "";
    logger.debug(message);

    String host = dbInfo[4].split(":")[2];
    host = host.substring(2);
    host = "localhost"; //to be deleted
    String[] p;
    p = dbInfo[4].split("/");
    String name = p[p.length - 1];
    Process pr;
    String sqlPath;
    String s_path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/bg_b_dist.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/t_b_dist.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    String[] fileNames = { "b03002", "b16004", "b17021", "b18101", "b19037" };
    String[] copyColumn = new String[5];
    copyColumn[0] = "blkGrp_b03002(GISJOIN,STATEA,COUNTYA, TRACTA, BLKGRPA,"
        + "not_hispanic_or_latino_white_alone,not_hispanic_or_latino_black_or_african_american_alone,"
        + "not_hispanic_or_latino_american_indian_and_alaska_native_alone,not_hispanic_or_latino_asian_alone,"
        + "not_hispanic_or_latino_native_hawaiian_and_other_pacific,not_hispanic_or_latino_some_other_race_alone, "
        + "not_hispanic_or_latino_two_or_more_races,hispanic__or__latino)";
    copyColumn[1] = "blkGrp_b16004(GISJOIN,STATEA,COUNTYA, TRACTA, BLKGRPA," + "from_5_to_17_years, "
        + "from_5_to_17_years_speak_only_english, "
        + "from_5_to_17_years_speak_spanish, from_5_to_17_years_speak_spanish_very_well, "
        + "from_5_to_17_years_speak_spanish_well, from_5_to_17_years_speak_spanish_not_well, from_5_to_17_years_speak_spanish_not_at_all,"
        + "from_5_to_17_years_speak_indo_european, from_5_to_17_years_speak_indo_european_very_well, "
        + "from_5_to_17_years_speak_indo_european_well, from_5_to_17_years_speak_indo_european_not_well, from_5_to_17_years_speak_indo_european_not_at_all, "
        + "from_5_to_17_years_speak_asian_and_pacific_island, from_5_to_17_years_speak_asian_and_pacific_island_very_well, "
        + "from_5_to_17_years_speak_asian_and_pacific_island_well, from_5_to_17_years_speak_asian_and_pacific_island_not_well, from_5_to_17_years_speak_asian_and_pacific_island_not_at_all,"
        + "from_5_to_17_years_speak_other , from_5_to_17_years_speak_other_very_well , "
        + "from_5_to_17_years_speak_other_well, from_5_to_17_years_speak_other_not_well, from_5_to_17_years_speak_other_not_at_all,"
        + "" + "from_18_to_64_years, " + "from_18_to_64_years_speak_only_english, "
        + "from_18_to_64_years_speak_spanish, from_18_to_64_years_speak_spanish_very_well, "
        + "from_18_to_64_years_speak_spanish_well, from_18_to_64_years_speak_spanish_not_well, from_18_to_64_years_speak_spanish_not_at_all,"
        + "from_18_to_64_years_speak_indo_european, from_18_to_64_years_speak_indo_european_very_well, "
        + "from_18_to_64_years_speak_indo_european_well, from_18_to_64_years_speak_indo_european_not_well, from_18_to_64_years_speak_indo_european_not_at_all, "
        + "from_18_to_64_years_speak_asian_and_pacific_island, from_18_to_64_years_speak_asian_and_pacific_island_very_well, "
        + "from_18_to_64_years_speak_asian_and_pacific_island_well, from_18_to_64_years_speak_asian_and_pacific_island_not_well, from_18_to_64_years_speak_asian_and_pacific_island_not_at_all,"
        + "from_18_to_64_years_speak_other , from_18_to_64_years_speak_other_very_well , "
        + "from_18_to_64_years_speak_other_well, from_18_to_64_years_speak_other_not_well, from_18_to_64_years_speak_other_not_at_all,"
        + "" + "from_64_to_over, " + "from_64_to_over_speak_only_english, "
        + "from_64_to_over_speak_spanish, from_64_to_over_speak_spanish_very_well, "
        + "from_64_to_over_speak_spanish_well, from_64_to_over_speak_spanish_not_well, from_64_to_over_speak_spanish_not_at_all,"
        + "from_64_to_over_speak_indo_european, from_64_to_over_speak_indo_european_very_well, "
        + "from_64_to_over_speak_indo_european_well, from_64_to_over_speak_indo_european_not_well, from_64_to_over_speak_indo_european_not_at_all, "
        + "from_64_to_over_speak_asian_and_pacific_island, from_64_to_over_speak_asian_and_pacific_island_very_well, "
        + "from_64_to_over_speak_asian_and_pacific_island_well, from_64_to_over_speak_asian_and_pacific_island_not_well, from_64_to_over_speak_asian_and_pacific_island_not_at_all, "
        + "from_64_to_over_speak_other , from_64_to_over_speak_other_very_well , "
        + "from_64_to_over_speak_other_well, from_64_to_over_speak_other_not_well, from_64_to_over_speak_other_not_at_all)";
    copyColumn[2] = "blkGrp_b17021(GISJOIN,STATEA,COUNTYA, TRACTA, BLKGRPA,below_poverty_total,above_poverty_total)";
    copyColumn[3] = "tract_b18101(GISJOIN,STATEA,COUNTYA, TRACTA,"
        + "male_under_5_with_disability,male_under_5_no_disability,male_5_to_17_with_disability,male_5_to_17_no_disability,male_18_to_34_with_disability,male_18_to_34_no_disability,"
        + "male_35_to_64_with_disability,male_35_to_64_no_disability,male_65_to_74_with_disability,male_65_to_74_no_disability,male_over_75_with_disability,male_over_75_no_disability,"
        + "female_under_5_with_disability,female_under_5_no_disability,female_5_to_17_with_disability,female_5_to_17_no_disability,female_18_to_34_with_disability,female_18_to_34_no_disability,"
        + "female_35_to_64_with_disability,female_35_to_64_no_disability,female_65_to_74_with_disability,female_65_to_74_no_disability,female_over_75_with_disability,female_over_75_no_disability)";
    copyColumn[4] = "blkGrp_b19037(GISJOIN,STATEA,COUNTYA, TRACTA, BLKGRPA,under_25,from_25_to_44,from_45_to_64,above_65)";

    for (int i = 0; i < fileNames.length; i++) {
      String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
      path = path + "../../src/main/webapp/resources/admin/uploads/t6/" + fileNames[i] + ".csv";
      path = path.substring(1, path.length());
      File source = new File(path);

      sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/" + fileNames[i] + "_1.sql";
      sqlPath = sqlPath.substring(1, sqlPath.length());
      runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

      copySqlCommand(copyColumn[i], path, dbInfo[4], dbInfo[5], dbInfo[6]);

      sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/" + fileNames[i] + "_2.sql";
      sqlPath = sqlPath.substring(1, sqlPath.length());
      runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);
    }

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/title_vi_blocks_float1.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/title_vi_blocks_float2.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/title_vi_blocks_float31.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/title_vi_blocks_float32.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/title_vi_blocks_float33.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/title_vi_blocks_float32.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/title_vi_blocks_float33.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/title_vi_blocks_float4.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/t6_Queries/title_vi_blocks_float5.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    if (message.equals("")) {
      message = "done";
    }

    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      addMetadata(stateid, metadata, c, "title6");
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  @GET
  @Path("/addEmp")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addEmp(@QueryParam("db") String db, @QueryParam("metadata") String metadata,
      @QueryParam("stateid") String stateid) throws SQLException {
    String[] dbInfo = db.split(",");

    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    path = path + "../../src/main/webapp/resources/admin/uploads/emp/rac.csv";
    path = path.substring(1, path.length());
    File source = new File(path);
    String message = "";
    //		logger.debug(message);

    String host = dbInfo[4].split(":")[2];
    host = host.substring(2);
    host = "localhost"; //to be deleted
    String[] p;
    p = dbInfo[4].split("/");
    String name = p[p.length - 1];
    Process pr;
    String sqlPath;
    String s_path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    sqlPath = s_path + "../../src/main/resources/admin/resources/emp_Queries/lodes_rac1.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    copySqlCommand("temp_01", path, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/emp_Queries/lodes_rac2.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    path = path + "../../src/main/webapp/resources/admin/uploads/emp/wac.csv";
    path = path.substring(1, path.length());
    source = new File(path);
    sqlPath = s_path + "../../src/main/resources/admin/resources/emp_Queries/lodes_wac1.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    copySqlCommand("temp_01", path, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/emp_Queries/lodes_wac2.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      addMetadata(stateid, metadata, c, "employment");
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    if (message.equals("")) {
      message = "done";
    }

    return message;
  }

  @GET
  @Path("/addfEmp")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addfEmp(@QueryParam("db") String db, @QueryParam("metadata") String metadata,
      @QueryParam("stateid") String stateid) throws SQLException {
    String[] dbInfo = db.split(",");

    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    path = path + "../../src/main/webapp/resources/admin/uploads/femp/future_employment.csv";
    path = path.substring(1, path.length());
    File source = new File(path);
    String message = "";
    //		logger.debug(message);

    String host = dbInfo[4].split(":")[2];
    host = host.substring(2);
    host = "localhost"; //to be deleted
    String[] p;
    p = dbInfo[4].split("/");
    String name = p[p.length - 1];
    Process pr;
    String sqlPath;
    String s_path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    sqlPath = s_path
        + "../../src/main/resources/admin/resources/femp_Queries/Create_Table_lodes_rac_projection_county1.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    copySqlCommand("temp_01 (countyid, ecurrent,e2010,e2015,e2020,e2025,e2030,e2035,e2040,e2045,e2050)", path,
        dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path
        + "../../src/main/resources/admin/resources/femp_Queries/Create_Table_lodes_rac_projection_county2.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path
        + "../../src/main/resources/admin/resources/femp_Queries/CREATE_TABLE_lodes_rac_projection_block.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      addMetadata(stateid, metadata, c, "future_emp");
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    if (message.equals("")) {
      message = "done";
    }

    return message;
  }

  @GET
  @Path("/addfPop")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addfPop(@QueryParam("db") String db, @QueryParam("metadata") String metadata,
      @QueryParam("stateid") String stateid) throws SQLException {
    String[] dbInfo = db.split(",");

    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    path = path + "../../src/main/webapp/resources/admin/uploads/fpop/future_population.csv";
    path = path.substring(1, path.length());
    File source = new File(path);
    String message = "done";
    //		logger.debug(message);

    String host = dbInfo[4].split(":")[2];
    host = host.substring(2);
    host = "localhost"; //to be deleted
    String[] p;
    p = dbInfo[4].split("/");
    String name = p[p.length - 1];
    Process pr;
    String sqlPath;
    String s_path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    sqlPath = s_path + "../../src/main/resources/admin/resources/fpop_Queries/futurePopBlocks1.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    copySqlCommand(
        "counties_future_pop(countyid,population2015,population2020,population2025,population2030,population2035,population2040,population2045,population2050)",
        path, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/fpop_Queries/futurePopBlocks2.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/fpop_Queries/futurePopBlocks3.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/fpop_Queries/futurePopBlocks3_1.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/fpop_Queries/futurePopBlocks3_2.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/fpop_Queries/futurePopBlocks4.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/fpop_Queries/futurePopBlocks4_1.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/fpop_Queries/futurePopBlocks4_2.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      addMetadata(stateid, metadata, c, "future_pop");
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  @GET
  @Path("/addRegion")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addRegion(@QueryParam("db") String db, @QueryParam("metadata") String metadata,
      @QueryParam("stateid") String stateid) throws SQLException {
    String[] dbInfo = db.split(",");

    String path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    path = path + "../../src/main/webapp/resources/admin/uploads/region/regions.csv";
    path = path.substring(1, path.length());
    File source = new File(path);
    String message = "done";
    //		logger.debug(message);

    String host = dbInfo[4].split(":")[2];
    host = host.substring(2);
    host = "localhost"; //to be deleted
    String[] p;
    p = dbInfo[4].split("/");
    String name = p[p.length - 1];
    Process pr;
    String sqlPath;
    String s_path = DbUpdate.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    sqlPath = s_path + "../../src/main/resources/admin/resources/region_Queries/region1.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    copySqlCommand("counties_regions(countyid,regionid,regionname)", path, dbInfo[4], dbInfo[5], dbInfo[6]);

    sqlPath = s_path + "../../src/main/resources/admin/resources/region_Queries/region2.sql";
    sqlPath = sqlPath.substring(1, sqlPath.length());
    runSqlFromFile(sqlPath, dbInfo[4], dbInfo[5], dbInfo[6]);

    Connection c = null;
    Statement statement = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      addMetadata(stateid, metadata, c, "region");
      statement.executeUpdate("VACUUM");
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return message;
  }

  public String removeLastChar(String str) {
    if (str.length() > 0) {
      str = str.substring(0, str.length() - 1);
    }
    return str;
  }

  /*@GET
    @Path("/updateSingleFeed")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object updateSingleFeed(@QueryParam("dbindex") int dbindex, @QueryParam("feedname") String feedname){
    String defaultId="";
    
    Connection c = null;
    Statement statement = null;
    try {
      c = DriverManager.getConnection(Databases.connectionURLs[dbindex], Databases.usernames[dbindex], Databases.passwords[dbindex]);
      statement = c.createStatement();
      ResultSet rs = statement.executeQuery("SELECT * FROM gtfs_feed_info WHERE feedname = '"+feedname+"';");
      if(rs.next()){
        defaultId = rs.getString("defaultId");
      }
    } catch (SQLException e) {
      logger.error(e);
      //e.printStackTrace();
    } finally {
      if (statement != null) try { statement.close(); } catch (SQLException e) {}
      if (c != null) try { c.close(); } catch (SQLException e) {}
    }
    logger.debug(defaultId);
    UpdateEventManager.updateTables(dbindex, defaultId);
    return "done";
  }*/

  @GET
  @Path("/updateNext")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object updateNext(@QueryParam("db") String db, @QueryParam("agency") String agency,
      @QueryParam("feed") String feed, @QueryParam("username") String username) {
    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    String message = "done";
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();

      logger.debug(agency);
      UpdateEventManager.updateTables(c, agency);
      statement.executeUpdate("UPDATE gtfs_uploaded_feeds set updated=True WHERE feedname='" + feed
          + "' AND username = '" + username + "';");
      logger.debug("vacuum start");
      statement.executeUpdate("VACUUM");
      logger.debug("vacuum finish");

      statement.close();
    } catch (SQLException e) {
      logger.error(e);
      message = e.getMessage();
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    return agency + "%%" + message;
  }

  @GET
  @Path("/updateFeeds")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object updateFeeds(@QueryParam("db") String db, @QueryParam("username") String username) {

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    //		List<String> agencies = new ArrayList<String>();
    //		List<String> feeds = new ArrayList<String>();
    PDBerror lists = new PDBerror();

    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();
      rs = statement.executeQuery(
          "SELECT gfi.defaultid agency, guf.feedname feed, guf.feedsize size FROM gtfs_feed_info gfi join gtfs_uploaded_feeds guf "
              + "ON gfi.feedname=guf.feedname WHERE guf.username = '" + username + "' AND guf.updated=False;");
      while (rs.next()) {
        lists.agencies.add(rs.getString("agency"));
        lists.feeds.add(rs.getString("feed"));
        lists.sizes.add("size");
      }
      /*for(int i=0; i<feeds.size();i++){
        logger.debug(agencies.get(i));
        UpdateEventManager.updateTables(c, agencies.get(i));
        statement.executeUpdate("UPDATE gtfs_uploaded_feeds set updated=True WHERE feedname='"+feeds.get(i)+"' AND username = '"+username+"';");
        logger.debug("vacuum start");
        statement.executeUpdate("VACUUM");
        logger.debug("vacuum finish");
      }*/

      statement.close();
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return lists;
  }

  @GET
  @Path("/addIndex")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object addIndex(@QueryParam("db") String db) {

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;

    String[][] defAgencyIds = { { "census_congdists_trip_map", "agencyid_def" },
        { "census_places_trip_map", "agencyid_def" }, { "census_urbans_trip_map", "agencyid_def" },
        { "census_counties_trip_map", "agencyid_def" }, { "census_tracts_trip_map", "agencyid_def" },
        { "gtfs_fare_rules", "fare_agencyid" }, { "gtfs_fare_attributes", "agencyid" },
        { "gtfs_trip_stops", "stop_agencyid_origin" }, { "gtfs_stop_service_map", "agencyid_def" },
        { "gtfs_route_serviceid_map", "agencyid_def" }, { "gtfs_stop_route_map", "agencyid_def" },
        { "gtfs_frequencies", "defaultid" }, { "gtfs_pathways", "agencyid" },
        { "gtfs_shape_points", "shapeid_agencyid" }, { "gtfs_stop_times", "stop_agencyid" },
        { "gtfs_transfers", "defaultid" }, { "tempstopcodes", "agencyid" }, { "tempetriptimes", "agencyid" },
        { "tempestshapes", "agencyid" }, { "tempshapes", "agencyid" }, { "gtfs_trips", "serviceid_agencyid" },
        { "gtfs_calendar_dates", "serviceid_agencyid" }, { "gtfs_calendars", "serviceid_agencyid" },
        { "gtfs_stops", "agencyid" }, { "gtfs_routes", "defaultid" }, { "gtfs_agencies", "defaultid" },
        { "gtfs_feed_info", "defaultid" } };
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);
      statement = c.createStatement();

      for (int i = 0; i < defAgencyIds.length; i++) {
        logger.debug("creating index for table: " + defAgencyIds[i][0]);
        try {
          statement.executeUpdate(
              "CREATE INDEX defaid" + i + " ON " + defAgencyIds[i][0] + " (" + defAgencyIds[i][1] + ");");
        } catch (SQLException e) {
          logger.error(e);
        }
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return "done";
  }

  @GET
  @Path("/agencyList")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object agencyList(@QueryParam("db") String db) {
    String[] dbInfo = db.split(",");
    FeedNames fn = new FeedNames();
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    Boolean b = true;
    PDBerror error = new PDBerror();
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();
      rs = statement.executeQuery("SELECT * FROM gtfs_feed_info;");

      while (rs.next()) {
        fn.feeds.add(rs.getString("feedname"));
        fn.names.add(rs.getString("agencynames"));
        fn.startdates.add(rs.getString("startdate"));
        fn.enddates.add(rs.getString("enddate"));
      }

    } catch (SQLException e) {
      logger.error(e);
      error.DBError = e.getMessage();
      b = false;

    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    if (b) {
      return fn;
    } else {
      return error;
    }

  }

  @GET
  @Path("/getImportedStates")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object getImportedStates(@QueryParam("db") String db) throws IOException {
    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    PDBerror results = new PDBerror();
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();
      rs = statement.executeQuery("SELECT distinct(stateid) stateids FROM census_blocks order by stateid;");
      while (rs.next()) {
        results.stateids.add(rs.getString("stateids"));
      }
      for (String id : results.stateids) {
        rs = statement.executeQuery("SELECT sname FROM census_states WHERE stateid='" + id + "';");
        if (rs.next()) {
          results.states.add(rs.getString("sname"));
        }
        rs = statement.executeQuery("SELECT census FROM database_metadata WHERE stateid='" + id + "';");
        if (rs.next()) {
          results.metadata.add(rs.getString("census"));
        }
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    return results;
  }

  @GET
  @Path("/getAvailableStates")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object getAvailableStates(@QueryParam("db") String db) throws IOException {
    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    PDBerror results = new PDBerror();
    String[] dbURL = dbInfo[4].split("/");
    dbURL[dbURL.length - 1] = "census_reference";

    dbInfo[4] = "";
    for (int i = 0; i < dbURL.length - 1; i++) {
      dbInfo[4] += dbURL[i] + "/";
    }
    dbInfo[4] += dbURL[dbURL.length - 1];
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();
      rs = statement.executeQuery("SELECT distinct(stateid) stateids FROM census_blocks_ref order by stateid;");
      while (rs.next()) {
        results.stateids.add(rs.getString("stateids"));
      }
      for (String id : results.stateids) {
        rs = statement.executeQuery("SELECT sname FROM census_states_ref WHERE stateid='" + id + "';");
        if (rs.next()) {
          results.states.add(rs.getString("sname"));
        }
      }
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }
    return results;
  }

  @GET
  @Path("/feedlist")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object listf(@QueryParam("foldername") String directoryName, @QueryParam("db") String db) throws IOException {

    File directory = new File(directoryName);
    File[] fList = directory.listFiles();
    FeedNames fn = new FeedNames();
    //ArrayList<String> fNames = new ArrayList<String>(); 
    try {
      for (File file : fList) {
        if (file.isDirectory()) {
          fn.feeds.add(file.getName());
        }
      }
    } catch (NullPointerException e) {
      logger.error("IndexOutOfBoundsException: ", e);
    }

    String[] dbInfo = db.split(",");
    Connection c = null;
    Statement statement = null;
    ResultSet rs = null;
    try {
      c = PgisEventManager.makeConnectionByUrl(dbInfo[4], dbInfo[5], dbInfo[6]);

      statement = c.createStatement();
      rs = statement.executeQuery("SELECT feedname FROM gtfs_feed_info;");
      while (rs.next()) {
        fn.feeds.remove(rs.getString("feedname"));
      }

    } catch (SQLException e) {
      logger.error(e);
    } finally {
      if (rs != null)
        try {
          rs.close();
        } catch (SQLException e) {
        }
      if (statement != null)
        try {
          statement.close();
        } catch (SQLException e) {
        }
      if (c != null)
        try {
          c.close();
        } catch (SQLException e) {
        }
    }

    return fn;
  }

  // ian: unused?
  @GET
  @Path("/simplify")
  @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
  public Object simplify() throws ZipException, FileNotFoundException {
    ZipFile zFile = new ZipFile("D:/ziptest/trimet.zip");
    zFile.extractAll("D:/ziptest/trimet");
    File OriginalFile = new File("D:/ziptest/trimet/shapes.txt");
    String line = "";
    String cvsSplitBy = ",";
    String header = "";
    List<ShapeRecord> records = new ArrayList<ShapeRecord>();
    try (BufferedReader br = new BufferedReader(new FileReader(OriginalFile))) {
      boolean skipChar = true;
      while ((line = br.readLine()) != null) {
        // use comma as separator
        if (skipChar) {
          header = line;
          skipChar = false;
          continue;
        }
        String[] values = line.split(cvsSplitBy);
        records.add(new ShapeRecord(values[0], Double.parseDouble(values[1]), Double.parseDouble(values[2]),
            Integer.parseInt(values[3]), Double.parseDouble(values[4])));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    OriginalFile.delete();

    ShapeRecord[] recordsArray = new ShapeRecord[records.size()];
    recordsArray = records.toArray(recordsArray);
    Simplify<ShapeRecord> simplify = new Simplify<ShapeRecord>(recordsArray);
    ShapeRecord[] simplifiedRecords = simplify.simplify(recordsArray, 0.00005, true);

    PrintWriter writer = new PrintWriter("D:/ziptest/trimet/shapes.txt");
    writer.print(header + "\n");
    int seqCounter = 0;
    try {
      for (ShapeRecord s : simplifiedRecords) {
        if (seqCounter > s.getSeq())
          seqCounter = 0;
        writer.print(s.getid() + "," + s.getX() + "," + s.getY() + "," + seqCounter++ + "," + s.getDisTrav() + "\n");
      }
    } catch (NullPointerException e) {
    }
    writer.close();
    File folder = new File("D:/ziptest/trimet");
    File[] files = folder.listFiles();
    ZipFile zipFile = new ZipFile("D:/ziptest/trimet_simplified.zip");
    ZipParameters parameters = new ZipParameters();
    parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
    parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
    zipFile.addFiles(new ArrayList<File>(Arrays.asList(files)), parameters);
    folder.delete();
    logger.debug("Done modifying feed shapes");
    return null;
  }

  public static void shapeSimplifier(File feed, String path) throws ZipException, FileNotFoundException {
    ZipFile zFile = new ZipFile(feed);
    zFile.extractAll(path + "/");
    File OriginalFile = new File(path + "/shapes.txt");
    String line = "";
    String cvsSplitBy = ",";
    String header = "";
    List<ShapeRecord> records = new ArrayList<ShapeRecord>();
    try (BufferedReader br = new BufferedReader(new FileReader(OriginalFile))) {
      boolean skipChar = true;
      while ((line = br.readLine()) != null) {
        // use comma as separator
        if (skipChar) {
          header = line;
          skipChar = false;
          continue;
        }
        String[] values = line.split(cvsSplitBy);
        records.add(new ShapeRecord(values[0], Double.parseDouble(values[1]), Double.parseDouble(values[2]),
            Integer.parseInt(values[3]), Double.parseDouble(values[4])));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    OriginalFile.delete();

    ShapeRecord[] recordsArray = new ShapeRecord[records.size()];
    recordsArray = records.toArray(recordsArray);
    Simplify<ShapeRecord> simplify = new Simplify<ShapeRecord>(recordsArray);
    ShapeRecord[] simplifiedRecords = simplify.simplify(recordsArray, 0.00005, true);

    PrintWriter writer = new PrintWriter(path + "/shapes.txt");
    writer.print(header + "\n");
    int seqCounter = 0;
    try {
      for (ShapeRecord s : simplifiedRecords) {
        if (seqCounter > s.getSeq())
          seqCounter = 0;
        writer.print(s.getid() + "," + s.getX() + "," + s.getY() + "," + seqCounter++ + "," + s.getDisTrav() + "\n");
      }
    } catch (NullPointerException e) {
    }
    writer.close();
    File folder = new File(path);
    File[] files = folder.listFiles();
    ZipFile zipFile = new ZipFile(path + ".zip");
    ZipParameters parameters = new ZipParameters();
    parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
    parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
    zipFile.addFiles(new ArrayList<File>(Arrays.asList(files)), parameters);
    folder.delete();
    logger.debug("Done modifying feed shapes");
  }

  private static class ShapeRecord implements Point {
    private final String id;
    private final double lat;
    private final double lng;
    private final int sequence;
    private final double distTraveled;

    private ShapeRecord(String id, double lat, double lng, int sequence, double distTraveled) {
      this.lat = lat;
      this.lng = lng;
      this.id = id;
      this.sequence = sequence;
      this.distTraveled = distTraveled;
    }

    public String getid() {
      return this.id;
    }

    public double getX() {
      return this.lat;
    }

    public double getY() {
      return this.lng;
    }

    public int getSeq() {
      return this.sequence;
    }

    public double getDisTrav() {
      return this.distTraveled;
    }
  }

  public static boolean runSqlFromFile(String sqlFilePath, String dbConnectionUrl, String dbUser, String dbPassword) {
    logger.info("runSqlFromFile: " + sqlFilePath);
    // 1. read file
    String sql = "";
    try {
      Scanner sqlScanner = new Scanner(new File(sqlFilePath));
      sql = sqlScanner.useDelimiter("\\Z").next();
      sqlScanner.close();
    } catch (FileNotFoundException e) {
      logger.error(e);
      return false;
    }

    try {
      // 2. open DB connection
      Connection c = PgisEventManager.makeConnectionByUrl(dbConnectionUrl, dbUser, dbPassword);
      Statement statement = c.createStatement();
      // 3. run SQL statement
      ResultSet rs = statement.executeQuery(sql);
      statement.close();
      c.close();
    } catch (SQLException e) {
      logger.error(e);
      return false;
    }
    return true;
  }

  public static boolean runSqlFromResource(String resourcePath, String dbConnectionUrl, String dbUser, String dbPassword) {
    logger.info("runSqlFromResource: "+resourcePath);
    // 1. read file
    File f = DatabaseConfig.getResource(resourcePath);
    String sql = "";
    try {
      Scanner sqlScanner = new Scanner(f);
      sql = sqlScanner.useDelimiter("\\Z").next();
      sqlScanner.close();
    } catch (FileNotFoundException e) {
      logger.error(e);
      return false;
    }

    try {
      // 2. open DB connection
      Connection c = PgisEventManager.makeConnectionByUrl(dbConnectionUrl, dbUser, dbPassword);
      Statement statement = c.createStatement();
      // 3. run SQL statement
      ResultSet rs = statement.executeQuery(sql);
      statement.close();
      c.close();
    } catch (SQLException e) {
      logger.error(e);
      return false;
    }
    return true;    
  }

  private static boolean copyTable(String tableName, String fromDbConnectionUrl, String toDbConnectionUrl, String dbUser, String dbPassword) {
    logger.info("copyTable: " + tableName + " from: " + fromDbConnectionUrl + " to: " + toDbConnectionUrl);
    // Parse connection urls...
    String fromHost = fromDbConnectionUrl.split(":")[2];
    fromHost = fromHost.substring(2);
    String[] p;
		p = fromDbConnectionUrl.split("/");
		String fromName = p[p.length-1];

    String toHost = toDbConnectionUrl.split(":")[2];
    toHost = toHost.substring(2);
		p = toDbConnectionUrl.split("/");
		String toName = p[p.length-1];

    // Dump
    // Don't use pipes because it cannot be done in a portable way.
    try{
    	String[] cmd = {
        "pg_dump",
        "-U", dbUser,
        "-h", fromHost,
        "-f", "census.dump",
        "-Fc",
        "-t", tableName,
        fromName
      };
      String[] envp = { "PGPASSWORD="+dbPassword };
      Process pr = Runtime.getRuntime().exec(cmd,envp);
      BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
      String str;while ((str=reader.readLine()) != null) {}
      pr.waitFor();
    }catch(Exception e) {
    	e.printStackTrace();
      return false;
    }
    // Load
    try{
    	String[] cmd = {
        "pg_restore",
        "-U", dbUser,
        "-h", toHost,
        "-d", toName,
        "census.dump"
      };
      String[] envp = { "PGPASSWORD="+dbPassword };
      Process pr = Runtime.getRuntime().exec(cmd,envp);
      BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
      String str;while ((str=reader.readLine()) != null) {}
      pr.waitFor();
    }catch(Exception e) {
    	e.printStackTrace();
      return false;
    }
    return true;
  }

  private static boolean copySqlCommand(String copyCommand, String fromFile, String dbConnectionUrl, String dbUser,
      String dbPassword) {
    logger.info("copySqlCommand: " + copyCommand + " (from: " + fromFile + ")");
    try {
      Connection c = PgisEventManager.makeConnectionByUrl(dbConnectionUrl, dbUser, dbPassword);
      Statement statement = c.createStatement();
      statement.executeUpdate("copy " + copyCommand + " FROM '" + fromFile + "' DELIMITER ',' CSV HEADER");
      return true;
    } catch (SQLException e) {
      logger.error(e);
      return false;
    }
  }
}
