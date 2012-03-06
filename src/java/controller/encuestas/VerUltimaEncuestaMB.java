/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.encuestas;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import model.entities.base.Encuesta;
import model.entities.base.OpcionEncuesta;
import model.entities.base.Usuario;
import model.entities.base.facades.EncuestaFacade;
import model.entities.base.facades.OpcionEncuestaFacade;
import model.entities.base.facades.UsuarioFacade;
import model.exceptions.BusinessLogicException;
import model.services.EncuestaService;
import utils.Util;

/**
 *
 * @author rulyone
 */
@ManagedBean
@RequestScoped
public class VerUltimaEncuestaMB {

    @EJB private EncuestaFacade encFac;
    @EJB private OpcionEncuestaFacade opcionFac;
    @EJB private UsuarioFacade userFac;

    @EJB private EncuestaService encuestaService;

    private Encuesta encuesta;
    private Map<OpcionEncuesta, Integer> votosPorOpcionMap;
    private Integer votosTotales = 0;

    
    private OpcionEncuesta votoUsuarioSingle;
    private List<OpcionEncuesta> votosUsuarioMultiple;

    private boolean usuarioYaVoto = false;

    /** Creates a new instance of VerUltimaEncuestaMB */
    public VerUltimaEncuestaMB() {
    }

    @PostConstruct
    public void postConstruct() {
        this.encuesta = encFac.getUltimaEncuesta();
        this.votosPorOpcionMap = new HashMap<OpcionEncuesta, Integer>();
        if (encuesta != null) {
            for (OpcionEncuesta oe : encuesta.getOpciones()) {
                int count = opcionFac.countVotosByIdOpcion(oe.getId());
                this.votosPorOpcionMap.put(oe, count);
            }
            this.votosTotales = opcionFac.countVotosUnicosByIdEncuesta(encuesta.getId());
        }

        Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        if(principal != null) {
            Usuario user = userFac.findByUsername(principal.getName());
            if(user != null) {
                List<OpcionEncuesta> opciones = opcionFac.findVotosUsuarioPorEncuesta(user, encuesta);
                if(opciones != null && !opciones.isEmpty()) {
                    this.usuarioYaVoto = true;
                }
            }
        }
    }

    public boolean isUsuarioYaVoto() {
        return usuarioYaVoto;
    }

    public void setUsuarioYaVoto(boolean usuarioYaVoto) {
        this.usuarioYaVoto = usuarioYaVoto;
    }

    public OpcionEncuesta getVotoUsuarioSingle() {
        return votoUsuarioSingle;
    }

    public void setVotoUsuarioSingle(OpcionEncuesta votoUsuarioSingle) {
        this.votoUsuarioSingle = votoUsuarioSingle;
    }

    public List<OpcionEncuesta> getVotosUsuarioMultiple() {
        return votosUsuarioMultiple;
    }

    public void setVotosUsuarioMultiple(List<OpcionEncuesta> votosUsuarioMultiple) {
        this.votosUsuarioMultiple = votosUsuarioMultiple;
    }

    public Encuesta getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(Encuesta encuesta) {
        this.encuesta = encuesta;
    }

    public Map<OpcionEncuesta, Integer> getVotosPorOpcionMap() {
        return votosPorOpcionMap;
    }

    public void setVotosPorOpcionMap(Map<OpcionEncuesta, Integer> votosPorOpcionMap) {
        this.votosPorOpcionMap = votosPorOpcionMap;
    }

    public Integer getVotosTotales() {
        return votosTotales;
    }

    public void setVotosTotales(Integer votosTotales) {
        this.votosTotales = votosTotales;
    }

    public void votar(ActionEvent e) {
        List<Long> idsOpciones = new ArrayList<Long>();
        if(this.encuesta.isMultiple()) {
            if(this.votosUsuarioMultiple == null || this.votosUsuarioMultiple.isEmpty()) {
                Util.addWarnMessage("Debes elegir al menos 1 opción.", null);
                return ;
            }
            for(int i = 0; i < this.votosUsuarioMultiple.size(); i++) {
                idsOpciones.add(this.votosUsuarioMultiple.get(i).getId());
            }
        }else{
            if(this.votoUsuarioSingle == null) {
                Util.addWarnMessage("Debes elegir al menos 1 opción.", null);
                return ;
            }
            idsOpciones.add(this.votoUsuarioSingle.getId());
        }

        try {
            encuestaService.votar(encuesta.getId(), idsOpciones);
            Util.addInfoMessage("Voto agregado satisfactoriamente.", null);

        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al votar.", ex.getMessage());
        }
    }

}
