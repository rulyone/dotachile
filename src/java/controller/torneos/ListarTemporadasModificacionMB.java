/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import model.entities.torneos.TemporadaModificacion;
import model.entities.torneos.facades.TemporadaModificacionFacade;
import model.exceptions.BusinessLogicException;
import model.services.TorneoService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="listarTemporadasModificacionMB")
@ViewScoped
public class ListarTemporadasModificacionMB implements Serializable {

    @EJB private TorneoService torneoService;
    @EJB private TemporadaModificacionFacade tempFac;

    private List<TemporadaModificacion> temporadas = new ArrayList<TemporadaModificacion>();

    /** Creates a new instance of ListarTemporadasModificacionMB */
    public ListarTemporadasModificacionMB() {
    }

    @PostConstruct
    private void loadTemporadas() {
        this.temporadas.clear();
        this.temporadas = tempFac.findAllReverse();
    }

    public List<TemporadaModificacion> getTemporadas() {
        return temporadas;
    }

    public void setTemporadas(List<TemporadaModificacion> temporadas) {
        this.temporadas = temporadas;
    }

    public String eliminarTemporadaModificacion(Long idTemporada) {
        try {
            torneoService.eliminarTemporadaModificaciones(idTemporada);
            for(TemporadaModificacion temp : this.temporadas) {
                if(temp.getId().equals(idTemporada)) {
                    this.temporadas.remove(temp);
                    break;
                }
            }
            Util.addInfoMessage("Temporada de modificacion eliminada satisfactoriamente", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al eliminar temporada de modificacion.", ex.getMessage());
        }
        return null;
    }

}
