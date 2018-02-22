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

package com.model.database.queries.util;

import java.io.File;

import org.hibernate.*;
import org.hibernate.cfg.*;

import com.model.database.Databases;

public class Hutil {
	private static SessionFactory[] sessionFactory = new SessionFactory[Databases.dbsize];	

    static {
    	for (int k=0; k<Databases.dbsize; k++)
    	{
				System.err.format("Hutil::static{} k: %s\n", k);
    		try {
                // Create the SessionFactory from hibernate.cfg.xml
                // Ed 2017-09-12 log so we can see who is using xml config paths.
								File config = new File(Databases.ConfigurationDirectory() + Databases.spatialConfigPaths[k]);
                System.err.format("Hutil::static{}, creating session factory from spatialConfigPath: %s\n", config.getPath());
                sessionFactory[k] = new Configuration().configure(config).buildSessionFactory();
            } catch (Throwable ex) {
                // Make sure you log the exception, as it might be swallowed
                System.err.println("Initial SessionFactory creation failed." + ex);
                throw new ExceptionInInitializerError(ex);
            }
    	}
        
    }
    
    public static void updateSessions(){
    	for (SessionFactory s: sessionFactory){
  		  s.close();
  	    }
    	sessionFactory = new SessionFactory[Databases.dbsize];	
    	for (int k=0; k<Databases.dbsize; k++)
    	{
    		try {
                // Create the SessionFactory from hibernate.cfg.xml
                // Ed 2017-09-12 log so we can see who is using xml config paths.
								File config = new File(Databases.ConfigurationDirectory() + Databases.spatialConfigPaths[k]);
								System.err.format("Hutil::updateSessions(), creating session factory from spatialConfigPath: %s\n", config.getPath());
                sessionFactory[k] = new Configuration().configure(config).buildSessionFactory();
                
            } catch (Throwable ex) {
                // Make sure you log the exception, as it might be swallowed
                System.err.println("Initial SessionFactory creation failed." + ex);
                throw new ExceptionInInitializerError(ex);
            }
    	}
    }

    public static SessionFactory[] getSessionFactory() {
        return sessionFactory;
    }
    
}
