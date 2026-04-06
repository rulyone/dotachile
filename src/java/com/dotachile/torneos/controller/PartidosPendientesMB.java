/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dotachile.torneos.controller;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.dotachile.torneos.entity.GameMatch;
import com.dotachile.torneos.facade.GameMatchFacade;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class PartidosPendientesMB implements Serializable {

    @EJB private GameMatchFacade matchFac;
    
    private List<GameMatch> matchesPendientes;
    
    /** Creates a new instance of PartidosPendientesMB */
    public PartidosPendientesMB() {
    
    }
    
    @PostConstruct
    public void init() {
        matchesPendientes = matchFac.listMatchesPendientes();
    }

    public List<GameMatch> getMatchesPendientes() {
        return matchesPendientes;
    }

    public void setMatchesPendientes(List<GameMatch> matchesPendientes) {
        this.matchesPendientes = matchesPendientes;
    }
    
}
