package com.model.database.queries.util;

import org.hibernate.*;
import org.hibernate.cfg.*;

import com.model.database.Databases;

public class Hutil {
	private static final SessionFactory[] sessionFactory = new SessionFactory[Databases.dbsize];	

    static {
    	for (int k=0; k<Databases.dbsize; k++)
    	{
    		try {
                // Create the SessionFactory from hibernate.cfg.xml
                sessionFactory[k] = new Configuration().configure(Databases.spatialConfigPaths[k]).buildSessionFactory();
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
