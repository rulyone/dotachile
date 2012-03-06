/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import model.entities.base.Usuario;
import model.entities.base.facades.GrupoFacade;
import model.entities.base.facades.UsuarioFacade;
//import model.entities.inhouse.PlayerIH;

/**
 *
 * @author Pablo
 */
public class Util implements Serializable {


    public static String cambiarSlashes(String str) {
        return str.replaceAll("\\\\", "/");
    }

    private static String hashPassword(String rawpassword) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] data = rawpassword.getBytes();
            m.update(data, 0, data.length);
            BigInteger i = new BigInteger(1,m.digest());
            return String.format("%1$032X", i).toLowerCase();
        }catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    
    public static String hashPassword(String rawpassword, String username) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            String str = rawpassword.concat(username);
            byte[] data = str.getBytes();
            m.update(data,0,data.length);
            BigInteger i = new BigInteger(1,m.digest());
            return String.format("%1$032X", i).toLowerCase();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static void addErrorMessage(String summary, String detail) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        msg.setSummary(summary);
        msg.setDetail(detail);
        ctx.addMessage(null, msg);
    }

    public static void addInfoMessage(String summary, String detail) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        msg.setSummary(summary);
        msg.setDetail(detail);
        ctx.addMessage(null, msg);
    }

    public static void addFatalMessage(String summary, String detail) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_FATAL);
        msg.setSummary(summary);
        msg.setDetail(detail);
        ctx.addMessage(null, msg);
    }

    public static void addWarnMessage(String summary, String detail) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_WARN);
        msg.setSummary(summary);
        msg.setDetail(detail);
        ctx.addMessage(null, msg);
    }

    public static Date dateSinMillis(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date dateSinTime(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static void keepMessages() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getExternalContext().getFlash().setKeepMessages(true);
    }

    public static void navigate(String where) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, where);
        ctx.renderResponse();
    }

    public static Usuario getUsuarioLogeado() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        Principal principal = fctx.getExternalContext().getUserPrincipal();
        if(principal != null && principal.getName() != null)  {
            try {
                Context ctx = (Context) new InitialContext();
                UsuarioFacade userFac = (UsuarioFacade) ctx.lookup("java:global/DotaCL/UsuarioFacade");
                return userFac.findByUsername(principal.getName());
            } catch (NamingException ex) {
                (Logger.getLogger(Util.class.getName())).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    public static boolean caracteresValidosPvpgn(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!(c == '-' || c == '_' || c == '^' || c == '[' || c == ']' || c == '.' || Character.isLetter(c) || Character.isDigit(c))) {
                return false;
            }
        }
        return true;
    }

}
