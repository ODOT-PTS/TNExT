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

package com.webapp.modifiers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.webapp.api.MainMap;

@WebServlet(urlPatterns = "/TNAtoolAPI-Webapp/admin")
public class Admin extends HttpServlet {
	// GET
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/resources/admin/adminTool.html").forward(
				request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
	    PrintWriter out = response.getWriter();
	    JSONArray json = new JSONArray();
	    JSONObject rs = new JSONObject();
//		String feed = "";
//		String error = "";
		String data = "";
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		//System.out.println(System.currentTimeMillis());
        if (isMultipart) {
	        FileItemFactory factory = new DiskFileItemFactory();
	
	        ServletFileUpload upload = new ServletFileUpload(factory);
	 
	        try {
		        List<FileItem> items = upload.parseRequest(new ServletRequestContext(request));
		        
		        for(FileItem item: items){
		        	if (item.isFormField()) {
		        		data = item.getString();
		        	}
		        }
		        if(data.equals("gtfs")){
		        	for(FileItem item: items){
			            if (!item.isFormField()) {
			                String fileName = item.getName();
			                String path = Admin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			                File loc = new File(path
			        				+ "../../src/main/webapp/resources/admin/uploads/gtfs");
			                if (!loc.exists()) {
			                	boolean status = loc.mkdirs();
			                }
			                File uploadedFile = new File(loc + "/" + fileName);
			                item.write(uploadedFile);
			                String str = fileName.split("\\.")[0];
			                if(item.getSize()>5000000){
			                	DbUpdate.shapeSimplifier(uploadedFile, loc+ "/" + fileName.split("\\.")[0]);
			                }
			                
			                JSONObject jsono = new JSONObject();
	                        jsono.put("name", fileName);
	                        jsono.put("size", item.getSize());
	                        jsono.put("url", "upload?getfile=" + fileName);
	                        jsono.put("thumbnail_url", "upload?getthumb=" + fileName);
	                        jsono.put("delete_url", "upload?delfile=" + fileName);
	                        jsono.put("delete_type", "GET");
	                        json.put(jsono);
	                        
			            }
			        }
		        }else if(data.equals("pnr")){
		        	for(FileItem item: items){
			            if (!item.isFormField()) {
			                String fileName = item.getName();
			                String path = Admin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			                File loc = new File(path
			        				+ "../../src/main/webapp/resources/admin/uploads/pnr");
			                if (!loc.exists()) {
			                	boolean status = loc.mkdirs();
			                }
			                File uploadedFile = new File(loc + "/" + fileName);
			                item.write(uploadedFile);
			                
			                JSONObject jsono = new JSONObject();
	                        jsono.put("name", fileName);
	                        jsono.put("size", item.getSize());
	                        jsono.put("url", "upload?getfile=" + fileName);
	                        jsono.put("thumbnail_url", "upload?getthumb=" + fileName);
	                        jsono.put("delete_url", "upload?delfile=" + fileName);
	                        jsono.put("delete_type", "GET");
	                        json.put(jsono);
	                        
			            }
			        }
		        }else if(data.equals("t6")){
		        	for(FileItem item: items){
			            if (!item.isFormField()) {
			                String fileName = item.getName();
			                String path = Admin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			                File loc = new File(path
			        				+ "../../src/main/webapp/resources/admin/uploads/t6");
			                if (!loc.exists()) {
			                	boolean status = loc.mkdirs();
			                }
			                File uploadedFile = new File(loc + "/" + fileName);
			                item.write(uploadedFile);
			                
			                JSONObject jsono = new JSONObject();
	                        jsono.put("name", fileName);
	                        jsono.put("size", item.getSize());
	                        jsono.put("url", "upload?getfile=" + fileName);
	                        jsono.put("thumbnail_url", "upload?getthumb=" + fileName);
	                        jsono.put("delete_url", "upload?delfile=" + fileName);
	                        jsono.put("delete_type", "GET");
	                        json.put(jsono);
	                        
			            }
			        }
		        }else if(data.equals("emp")){
		        	for(FileItem item: items){
			            if (!item.isFormField()) {
			                String fileName = item.getName();
			                String path = Admin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			                File loc = new File(path
			        				+ "../../src/main/webapp/resources/admin/uploads/emp");
			                if (!loc.exists()) {
			                	boolean status = loc.mkdirs();
			                }
			                File uploadedFile = new File(loc + "/" + fileName);
			                item.write(uploadedFile);
			                
			                JSONObject jsono = new JSONObject();
	                        jsono.put("name", fileName);
	                        jsono.put("size", item.getSize());
	                        jsono.put("url", "upload?getfile=" + fileName);
	                        jsono.put("thumbnail_url", "upload?getthumb=" + fileName);
	                        jsono.put("delete_url", "upload?delfile=" + fileName);
	                        jsono.put("delete_type", "GET");
	                        json.put(jsono);
	                        
			            }
			        }
		        }else if(data.equals("femp")){
		        	for(FileItem item: items){
			            if (!item.isFormField()) {
			                String fileName = item.getName();
			                String path = Admin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			                File loc = new File(path
			        				+ "../../src/main/webapp/resources/admin/uploads/femp");
			                if (!loc.exists()) {
			                	boolean status = loc.mkdirs();
			                }
			                File uploadedFile = new File(loc + "/" + fileName);
			                item.write(uploadedFile);
			                
			                JSONObject jsono = new JSONObject();
	                        jsono.put("name", fileName);
	                        jsono.put("size", item.getSize());
	                        jsono.put("url", "upload?getfile=" + fileName);
	                        jsono.put("thumbnail_url", "upload?getthumb=" + fileName);
	                        jsono.put("delete_url", "upload?delfile=" + fileName);
	                        jsono.put("delete_type", "GET");
	                        json.put(jsono);
	                        
			            }
			        }
		        }else if(data.equals("fpop")){
		        	for(FileItem item: items){
			            if (!item.isFormField()) {
			                String fileName = item.getName();
			                String path = Admin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			                File loc = new File(path
			        				+ "../../src/main/webapp/resources/admin/uploads/fpop");
			                if (!loc.exists()) {
			                	boolean status = loc.mkdirs();
			                }
			                File uploadedFile = new File(loc + "/" + fileName);
			                item.write(uploadedFile);
			                
			                JSONObject jsono = new JSONObject();
	                        jsono.put("name", fileName);
	                        jsono.put("size", item.getSize());
	                        jsono.put("url", "upload?getfile=" + fileName);
	                        jsono.put("thumbnail_url", "upload?getthumb=" + fileName);
	                        jsono.put("delete_url", "upload?delfile=" + fileName);
	                        jsono.put("delete_type", "GET");
	                        json.put(jsono);
	                        
			            }
			        }
		        }
		        
	        } catch (FileUploadException e) {
	        	e.printStackTrace();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
	        
       
	        try {
				rs.put("files", json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			out.print(rs);
			out.flush();
			out.close();
			
        }
	}

}