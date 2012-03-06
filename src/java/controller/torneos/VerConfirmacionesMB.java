/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.torneos;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.entities.base.Confirmacion;
import model.entities.base.facades.ConfirmacionFacade;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class VerConfirmacionesMB implements Serializable {

    @EJB private ConfirmacionFacade confirmacionFac;
    
    private List<Confirmacion> confirmaciones;
    
    /** Creates a new instance of VerConfirmaciones */
    public VerConfirmacionesMB() {
    }
    
    @PostConstruct
    public void init() {
        this.confirmaciones = confirmacionFac.findAll();
    }

    public List<Confirmacion> getConfirmaciones() {
        return confirmaciones;
    }

    public void setConfirmaciones(List<Confirmacion> confirmaciones) {
        this.confirmaciones = confirmaciones;
    }
    
}
