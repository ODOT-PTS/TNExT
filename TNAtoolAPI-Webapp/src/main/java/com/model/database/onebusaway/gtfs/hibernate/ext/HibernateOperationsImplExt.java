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


package com.model.database.onebusaway.gtfs.hibernate.ext;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.onebusaway.gtfs.services.HibernateOperation;
import org.onebusaway.gtfs.services.HibernateOperations;
import org.onebusaway.gtfs.impl.HibernateOperationsImpl;

public class HibernateOperationsImplExt extends HibernateOperationsImpl{
	/*private static final int BUFFER_SIZE = 1000;

	  private SessionFactory _sessionFactory;

	  private Session _session;

	  private int _count = 0;*/

	  public HibernateOperationsImplExt() {

	  }

	  /*public HibernateOperationsImplExt(SessionFactory sessionFactory) {
	    setSessionFactory(sessionFactory);
	  }
	  
	  @Override
	  public SessionFactory getSessionFactory() {
	    return _sessionFactory;
	  }

	  public void setSessionFactory(SessionFactory sessionFactory) {
	    _sessionFactory = sessionFactory;
	  }*/

	  /*@Override
	  public void open() {
	    _session = _sessionFactory.openSession();
	    _session.beginTransaction();
	  }

	  @Override
	  public void close() {
	    Transaction tx = _session.getTransaction();
	    tx.commit();

	    _session.close();
	    _session = null;
	  }

	  @Override
	  public Object execute(HibernateOperation callback) {
	    if (_session == null) {
	      Session session = _sessionFactory.openSession();
	      Transaction tx = session.beginTransaction();
	      tx.begin();
	      try {
	        Object result = callback.doInHibernate(session);
	        tx.commit();
	        return result;
	      } catch (Exception ex) {
	        tx.rollback();
	        throw new IllegalStateException(ex);
	      } finally {
	        session.close();
	      }
	    } else {
	      try {
	        return callback.doInHibernate(_session);
	      } catch (Exception ex) {
	        throw new IllegalStateException(ex);
	      }
	    }
	  }*/
	  
	  /*@Override
	  public <T> List<T> find(String queryString) {
	    return findWithNamedParams(queryString, null, null);
	  }

	  @Override
	  public <T> List<T> findWithNamedParam(String queryString, String paramName,
	      Object value) {
	    return findWithNamedParams(queryString, new String[] {paramName},
	        new Object[] {value});
	  }*/

	  /*@SuppressWarnings("unchecked")
	  public <T> List<T> findWithNamedParams(final String queryString,
	      final String[] paramNames, final Object[] values) {
	    return (List<T>) execute(new HibernateOperation() {
	      public Object doInHibernate(Session session) throws HibernateException {
	        Query queryObject = session.createQuery(queryString);
	        if (values != null) {
	          for (int i = 0; i < values.length; i++)
	            applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
	        }
	        return queryObject.list();
	      }
	    });
	  }*/

	  /*@Override
	  public <T> List<T> findByNamedQuery(String namedQuery) {
	    return findByNamedQueryAndNamedParams(namedQuery, null, null);
	  }*/
	  
	  public <T> List<T> findByNamedQueryLimited(String namedQuery, final int limit, final int offset) {
	    return findByNamedQueryAndNamedParamsLimited(namedQuery, null, null, limit, offset);
	  }

	  /*@Override
	  public <T> List<T> findByNamedQueryAndNamedParam(final String namedQuery,
	      String paramName, Object paramValue) {
	    return findByNamedQueryAndNamedParams(namedQuery, new String[] {paramName},
	        new Object[] {paramValue});
	  }*/

	  public <T> List<T> findByNamedQueryAndNamedParamLimited(final String namedQuery,
	      String paramName, Object paramValue, final int limit, final int offset) {
	    return findByNamedQueryAndNamedParamsLimited(namedQuery, new String[] {paramName},
	        new Object[] {paramValue}, limit, offset);
	  }
	  
	  /*@SuppressWarnings("unchecked")
	  public <T> List<T> findByNamedQueryAndNamedParams(final String namedQuery,
	      final String[] paramNames, final Object[] values) {
	    return (List<T>) execute(new HibernateOperation() {
	      public Object doInHibernate(Session session) throws HibernateException {
	        Query queryObject = session.getNamedQuery(namedQuery);
	        if (values != null) {
	          for (int i = 0; i < values.length; i++)
	            applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
	        }
	        return queryObject.list();
	      }
	    });
	  }*/
	  
	  @SuppressWarnings("unchecked")
	  public <T> List<T> findByNamedQueryAndNamedParamsLimited(final String namedQuery,
	      final String[] paramNames, final Object[] values, final int limit, final int offset) {
	    return (List<T>) execute(new HibernateOperation() {
	      public Object doInHibernate(Session session) throws HibernateException {
	        Query queryObject = session.getNamedQuery(namedQuery);
	        if (values != null) {
	          for (int i = 0; i < values.length; i++)
	            applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
	        }
	        queryObject.setFirstResult(offset);
	        queryObject.setMaxResults(limit);
	        return queryObject.list();
	      }
	    });
	  }
	  
	  /*@SuppressWarnings("unchecked")
	  @Override
	  public <T> T get(final Class<T> entityType, final Serializable id) {
	    return (T) execute(new HibernateOperation() {
	      public Object doInHibernate(Session session) throws HibernateException {
	        return session.get(entityType, id);
	      }
	    });
	  }*/

	 /* @Override
	  public void update(final Object entity) {
	    execute(new HibernateOperation() {
	      @Override
	      public Object doInHibernate(Session session) throws HibernateException,
	          SQLException {
	        session.update(entity);
	        return null;
	      }
	    });
	  }*/

	  /*@Override
	  public void save(final Object entity) {
	    execute(new HibernateOperation() {
	      @Override
	      public Object doInHibernate(Session session) throws HibernateException,
	          SQLException {

	        Object obj = session.save(entity);

	        _count++;

	        if (_count >= BUFFER_SIZE) {
	          session.flush();
	          session.clear();
	          _count = 0;
	        }

	        return obj;
	      }
	    });
	  }*/

	  /*@Override
	  public void saveOrUpdate(final Object entity) {
	    execute(new HibernateOperation() {
	      @Override
	      public Object doInHibernate(Session session) throws HibernateException,
	          SQLException {

	        session.saveOrUpdate(entity);

	        _count++;

	        if (_count >= BUFFER_SIZE) {
	          session.flush();
	          session.clear();
	          _count = 0;
	        }

	        return null;
	      }
	    });
	  }*/

	  /*@Override
	  public <T> void clearAllEntitiesForType(final Class<T> type) {
	    execute(new HibernateOperation() {
	      @Override
	      public Object doInHibernate(Session session) throws HibernateException,
	          SQLException {
	        Query query = session.createQuery("delete from " + type.getName());
	        return query.executeUpdate();
	      }
	    });
	  }*/

	  public <T> void updateByNamedQueryAndNamedParams(final String namedQuery, final String[] paramNames, final Object[] values) {
	    execute(new HibernateOperation() {
	    	
	      //Session session = _sessionFactory.openSession();
	      //Transaction tx = session.beginTransaction();
	      @Override
	      public Object doInHibernate(Session session) throws HibernateException,
	          SQLException {
	    	  Query queryObject = session.getNamedQuery(namedQuery);
	    	  if (values != null) {
	              for (int i = 0; i < values.length; i++)
	                applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
	            }        
	        //Transaction tx = session.beginTransaction();
	    	Object result = queryObject.executeUpdate();
	        //tx.commit();
	        //session.close();
	        return result;
	      }
	    });
	  }
	  
	  /*@Override
	  public <T> void removeEntity(final T entity) {
	    execute(new HibernateOperation() {
	      @Override
	      public Object doInHibernate(Session session) throws HibernateException,
	          SQLException {
	        session.delete(entity);
	        return null;
	      }
	    });
	  }

	  @Override
	  public void flush() {
	    execute(new HibernateOperation() {
	      public Object doInHibernate(Session session) throws HibernateException {
	        session.flush();
	        _count = 0;
	        return null;
	      }
	    });
	  }*/

	  /*protected void applyNamedParameterToQuery(Query queryObject,
	      String paramName, Object value) throws HibernateException {

	    if (value instanceof Collection<?>) {
	      queryObject.setParameterList(paramName, (Collection<?>) value);
	    } else if (value instanceof Object[]) {
	      queryObject.setParameterList(paramName, (Object[]) value);
	    } else {
	      queryObject.setParameter(paramName, value);
	    }
	  }*/
}
