/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.encuestas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import model.exceptions.BusinessLogicException;
import model.services.EncuestaService;
import utils.Util;

/**
 *
 * @author rulyone
 */
@ManagedBean
@ViewScoped
public class CrearEncuesta implements Serializable {

    @EJB private EncuestaService encuestaService;

    private String pregunta;
    private List<String> opciones;
    private boolean multiple = false;

    private String opcionNueva = null;

    /** Creates a new instance of CrearEncuesta */
    public CrearEncuesta() {
    }

    public String getOpcionNueva() {
        return opcionNueva;
    }

    public void setOpcionNueva(String opcionNueva) {
        this.opcionNueva = opcionNueva;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public List<String> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<String> opciones) {
        this.opciones = opciones;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public void agregarOpcion(ActionEvent e) {
        if(this.opciones == null) {
            this.opciones = new ArrayList<String>();
        }
        if(this.opcionNueva != null) {
            this.opciones.add(this.opcionNueva);
            this.opcionNueva = null;
        }
    }

    public String crearEncuesta() {
        try {
            encuestaService.crearEncuesta(pregunta, opciones, multiple);
            Util.addInfoMessage("Encuesta creada satisfactoriamente!", null);
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al crear encuesta.", ex.getMessage());
            return null;
        }
        return "/index.xhtml?faces-redirect=true";
    }

}
