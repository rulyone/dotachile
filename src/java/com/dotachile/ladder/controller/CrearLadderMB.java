/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.ladder.controller;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import com.dotachile.torneos.entity.FactorK;
import com.dotachile.shared.BusinessLogicException;
import com.dotachile.ladder.service.LadderService;
import com.dotachile.shared.Util;

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
