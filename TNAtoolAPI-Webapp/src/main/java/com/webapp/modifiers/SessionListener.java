package com.webapp.modifiers;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import java.sql.SQLException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.model.database.queries.PgisEventManager;
import com.model.database.DatabaseConfig;

@WebListener
public class SessionListener implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        session.setMaxInactiveInterval(10 * 60); //in seconds
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        try {
            for (int index = 0; index < DatabaseConfig.getConfigSize(); index ++) {
                PgisEventManager.removeHiddenAgencies(index, session.getId());
            }
        } catch (SQLException sqle) {
        } catch (FactoryException fe) {
        } catch (TransformException te) {
        }
    }
}