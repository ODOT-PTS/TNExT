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
