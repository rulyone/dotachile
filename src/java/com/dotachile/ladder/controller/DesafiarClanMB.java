/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.ladder.controller;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import com.dotachile.clanes.entity.Clan;
import com.dotachile.clanes.facade.ClanFacade;
import com.dotachile.ladder.service.LadderService;

/**
 *
 * @author Pablo
 */
@ManagedBean(name="desafiarClanMB")
@ViewScoped
public class DesafiarClanMB implements Serializable {

    @EJB private ClanFacade clanFac;
    @EJB private LadderService ladderService;

    private String tag;
    private Clan clan;

    /** Creates a new instance of DesafiarClanMB */
    public DesafiarClanMB() {
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void loadClan(ComponentSystemEvent e) {
        if(this.tag == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        Clan c = clanFac.findByTag(tag);
        if(c == null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "/index.xhtml?faces-redirect=true");
            return ;
        }
        this.clan = c;
    }
    
    

}
