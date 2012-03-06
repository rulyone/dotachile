/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.torneos.ladder;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.entities.torneos.Desafio;
import model.entities.torneos.facades.DesafioFacade;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class VerDesafiosPendientesMB implements Serializable {
    
    @EJB private DesafioFacade desafioFac;
    private List<Desafio> desafiosPendientes;

    /** Creates a new instance of VerDesafiosPendientesMB */
    public VerDesafiosPendientesMB() {
    }
    
    @PostConstruct
    private void loadAll() {
        this.desafiosPendientes = desafioFac.findDesafiosPendientes();
    }

    public List<Desafio> getDesafiosPendientes() {
        return desafiosPendientes;
    }

    public void setDesafiosPendientes(List<Desafio> desafiosPendientes) {
        this.desafiosPendientes = desafiosPendientes;
    }
    
}
