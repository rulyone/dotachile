/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.clanes;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.entities.base.Clan;
import model.entities.base.facades.ClanFacade;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class VerClanesPorFechaCreacion {
    
    @EJB private ClanFacade clanFac;
    
    private List<Clan> clanes;

    /** Creates a new instance of VerClanesPorFechaCreacion */
    public VerClanesPorFechaCreacion() {
    }
    
    @PostConstruct
    public void init() {
        this.clanes = clanFac.listClanesOrderByFechaCreacion();
    }

    public List<Clan> getClanes() {
        return clanes;
    }

    public void setClanes(List<Clan> clanes) {
        this.clanes = clanes;
    }
    
}
