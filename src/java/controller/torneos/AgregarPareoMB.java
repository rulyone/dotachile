/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.bean.ManagedBean;
import model.entities.torneos.Ronda;
import model.entities.torneos.facades.RondaFacade;
import model.exceptions.BusinessLogicException;
import model.services.TorneoService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="agregarPareoMB")
@ViewScoped
public class AgregarPareoMB implements Serializable {

   
    @EJB private TorneoService torneoService;
    @EJB private RondaFacade rondaFac;

    private String tag1;
    private String tag2;
    private Long idRonda;
    private int bestOf = 1;
    private String arbitroUsername;
    private Date fechaMatch;
    private Integer horaFechaMatch = 16;
    private Integer minutoFechaMatch = 0;

    /** Creates a new instance of AgregarPareoMB */
    public AgregarPareoMB() {
    }

    public Integer getHoraFechaMatch() {
        return horaFechaMatch;
    }

    public void setHoraFechaMatch(Integer horaFechaMatch) {
        this.horaFechaMatch = horaFechaMatch;
    }

    public Integer getMinutoFechaMatch() {
        return minutoFechaMatch;
    }

    public void setMinutoFechaMatch(Integer minutoFechaMatch) {
        this.minutoFechaMatch = minutoFechaMatch;
    }
    
    public String getArbitroUsername() {
        return arbitroUsername;
    }

    public void setArbitroUsername(String arbitroUsername) {
        this.arbitroUsername = arbitroUsername;
    }

    public int getBestOf() {
        return bestOf;
    }

    public void setBestOf(int bestOf) {
        this.bestOf = bestOf;
    }

    public Long getIdRonda() {
        return idRonda;
    }

    public void setIdRonda(Long idRonda) {
        this.idRonda = idRonda;
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public Date getFechaMatch() {
        return fechaMatch;
    }

    public void setFechaMatch(Date fechaMatch) {
        this.fechaMatch = fechaMatch;
    }

    public void verificarRonda(ComponentSystemEvent e) {
        if(this.idRonda == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        Ronda r = rondaFac.find(idRonda);
        if(r == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
    }

    public String agregarPareo() {

        
        if(horaFechaMatch < 1 || horaFechaMatch > 24) {
            Util.addErrorMessage("La hora debe ser entre 1 y 12", null);
            return null;
        }
        if(minutoFechaMatch < 0 || minutoFechaMatch > 59) {
            Util.addErrorMessage("El minuto debe ser entre 0 y 59", null);
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaMatch);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, horaFechaMatch);
        cal.set(Calendar.MINUTE, minutoFechaMatch);
        cal.setTimeZone(TimeZone.getTimeZone("America/Santiago"));
        fechaMatch = cal.getTime();
        try {
            torneoService.agregarPareo(tag1, tag2, idRonda, bestOf, arbitroUsername, fechaMatch);
            Util.addInfoMessage("Pareo agregado satisfactoriamente.", null);
            this.tag1 = null;
            this.tag2 = null;
            this.bestOf = 1;
            this.arbitroUsername = null;
            this.fechaMatch = null;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al agregar pareo.", ex.getMessage());
        }
        return null;
    }

}
