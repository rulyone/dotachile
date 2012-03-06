/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import model.exceptions.BusinessLogicException;
import model.services.TorneoService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="crearTemporadaModificacionesMB")
@RequestScoped
public class CrearTemporadaModificacionesMB implements Serializable {

    @EJB private TorneoService torneoService;

    private Date startDate;
    private Date endDate;
    private int maxAgregaciones = 1;

    /** Creates a new instance of CrearTemporadaModificacionesMB */
    public CrearTemporadaModificacionesMB() {
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getMaxAgregaciones() {
        return maxAgregaciones;
    }

    public void setMaxAgregaciones(int maxAgregaciones) {
        this.maxAgregaciones = maxAgregaciones;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String crearTemporadaModificaciones() {
        try {
            torneoService.crearTemporadaModificaciones(startDate, endDate, maxAgregaciones);
            Util.addInfoMessage("Temporada de modificaciones creada satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al crear temporada de modificaciones.", ex.getMessage());
        }
        return null;
    }

}
