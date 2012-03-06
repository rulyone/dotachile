/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller.clanes;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import model.exceptions.BusinessLogicException;
import model.services.ClanService;
import utils.Util;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="revivirClanMB")
@RequestScoped
public class RevivirClanMB implements Serializable {
    
    @EJB
    private ClanService clanService;

    private String tag;

    /** Creates a new instance of RevivirClanMB */
    public RevivirClanMB() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String revivirClan() {
        String ret = null;
        try {
            clanService.revivirClan(tag);
            Util.addInfoMessage("Clan revivido satisfactoriamente", null);
            ret = "/web/clanes/VerClan.xhtml?faces-redirect=true&amp;tag=" + tag;
        } catch (BusinessLogicException ex) {
            Util.addErrorMessage("Error al revivir el clan.", ex.getMessage());
        }
        return ret;
    }

}
