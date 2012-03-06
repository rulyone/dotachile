/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.clanes;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import model.exceptions.BusinessLogicException;
import model.services.ClanService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="crearClanMB")
@ViewScoped
public class CrearClanMB implements Serializable {

    @EJB
    private ClanService clanService;

    private String nombre;
    private String tag;

    /** Creates a new instance of CrearClanMB */
    public CrearClanMB() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String crearClan() {
        String ret = null;
        try {
            clanService.crearClan(nombre, tag);
            Util.addInfoMessage("Clan creado satisfactoriamente", null);
            ret = "/web/clanes/VerClan.xhtml?faces-redirect=true&amp;tag=" + tag;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al crear clan", ex.getMessage());
        }
        return ret;        
    }
}
