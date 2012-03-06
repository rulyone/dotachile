/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.torneos.ladder;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import model.entities.torneos.FactorK;
import model.exceptions.BusinessLogicException;
import model.services.LadderService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="crearLadderMB")
@RequestScoped
public class CrearLadderMB implements Serializable {

    @EJB private LadderService ladderService;

    private String nombre;
    private String informacion;
    private FactorK factorK;

    /** Creates a new instance of CrearLadderMB */
    public CrearLadderMB() {
    }

    public FactorK getFactorK() {
        return factorK;
    }

    public void setFactorK(FactorK factorK) {
        this.factorK = factorK;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public FactorK[] getFactorKValues() {
        return FactorK.values();
    }

    public String crearLadderYComenzarlo() {
        try {
            ladderService.crearLadderYComenzarlo(nombre, informacion, factorK);
            Util.addInfoMessage("Ladder creado y comenzado.", null);
            Util.keepMessages();
            return "/web/ladder/VerLadder.xhtml";
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al crear/comenzar ladder", ex.getMessage());
        }
        return null;
    }
}
