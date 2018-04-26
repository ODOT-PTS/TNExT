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

package com.model.database.queries.objects;

import java.io.InputStream;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
 
@JsonIgnoreProperties({"content"})
public class FileMeta {
 
    private String fileName;
    private String fileSize;
    private String fileType;
 
    private InputStream content;
 
    public void setContent(InputStream content){
    	this.content = content;
    }
    
    public InputStream getContent(){
    	return this.content;
    }
    
    public void setFileType(String fileType){
    	this.fileType = fileType;
    }
    
    public String getFileType(){
    	return this.fileType;
    }
    
    public void setFileName(String fileName){
    	this.fileName = fileName;
    }
    
    public String getFileName(){
    	return this.fileName;
    }
    
    public void setFileSize(String fileSize){
    	this.fileSize = fileSize;
    }
    
    public String getFileSize(){
    	return this.fileSize;
    }
}
