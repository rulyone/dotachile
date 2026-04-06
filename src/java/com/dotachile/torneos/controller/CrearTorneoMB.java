/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.torneos.controller;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import com.dotachile.torneos.entity.FactorK;
import com.dotachile.torneos.entity.TipoTorneo;
import com.dotachile.shared.BusinessLogicException;
import com.dotachile.torneos.service.TorneoService;
import com.dotachile.shared.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="crearTorneoMB")
@RequestScoped
public class CrearTorneoMB implements Serializable {

    @EJB private TorneoService torneoService;

    private String nombreTorneo;
    private String infoTorneo;
    private FactorK factorK;
    private Integer maxCantidadClanes;
    private Integer minCantidadClanes;

    /** Creates a new instance of CrearTorneoMB */
    public CrearTorneoMB() {
    }

    public FactorK getFactorK() {
        return factorK;
    }

    public void setFactorK(FactorK factorK) {
        this.factorK = factorK;
    }

    public String getInfoTorneo() {
        return infoTorneo;
    }

    public void setInfoTorneo(String infoTorneo) {
        this.infoTorneo = infoTorneo;
    }

    public Integer getMaxCantidadClanes() {
        return maxCantidadClanes;
    }

    public void setMaxCantidadClanes(Integer maxCantidadClanes) {
        this.maxCantidadClanes = maxCantidadClanes;
    }

    public Integer getMinCantidadClanes() {
        return minCantidadClanes;
    }

    public void setMinCantidadClanes(Integer minCantidadClanes) {
        this.minCantidadClanes = minCantidadClanes;
    }

    public String getNombreTorneo() {
        return nombreTorneo;
    }

    public void setNombreTorneo(String nombreTorneo) {
        this.nombreTorneo = nombreTorneo;
    }

    public SelectItem[] getFactorKValues() {
        SelectItem[] items = new SelectItem[FactorK.values().length];
        int i = 0;
        for(FactorK f: FactorK.values()) {
          items[i++] = new SelectItem(f, f.toString());
        }
        return items;
  }

    
    public String crearTorneo() {
        String ret = "/index.xhtml";
        try {
            torneoService.crearTorneo(nombreTorneo, infoTorneo, TipoTorneo.CUSTOM, factorK, maxCantidadClanes, minCantidadClanes);
            Util.addInfoMessage("Torneo creado satisfactoriamente.", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al crear torneo.", ex.getMessage());
        }
        return ret;
    }
    
}
