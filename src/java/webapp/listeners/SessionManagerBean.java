/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package webapp.listeners;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author rulyone
 */
@Singleton
@LocalBean
@WebListener
public class SessionManagerBean implements HttpSessionListener{

    private static int counter = 0;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        counter++;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        counter--;
    }

    public int getActiveSessionsCount() {
        return counter;
    }

}