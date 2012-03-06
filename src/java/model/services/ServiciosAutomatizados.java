/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timer;
import model.entities.base.*;
import model.entities.base.facades.*;
import model.entities.torneos.Desafio;
import model.entities.torneos.facades.DesafioFacade;
import utils.Util;

/**
 *
 * @author rulyone
 */
@Singleton
public class ServiciosAutomatizados {

    @EJB private UsuarioFacade userFac;
    @EJB private PreRegistroFacade prFac;
    @EJB private GrupoFacade grupoFac;
    @EJB private BanFacade banFac;
    @EJB private BanHistoryFacade banHistoryFac;
    @EJB private ClanFacade clanFac;
    @EJB private DesafioFacade desafioFac;

    @EJB private AdminService adminService;

    @Schedule(hour="5", minute="0")
    private void removerCuentasNoActivadas(Timer timer) {
        List<PreRegistro> preRegistros = prFac.findAll();
        for(int i = 0; i < preRegistros.size(); i++) {
            PreRegistro pre = preRegistros.get(i);
            prFac.remove(pre);
        }
    }


    @Schedule(hour="6", minute="0")
    private void checkearBansYRemoverDeGrupoBaneados(Timer timer) {
        List<Ban> bans = banFac.findAll();
        for(int i = 0; i < bans.size(); i++) {
            Ban ban = bans.get(i);
            Calendar actual = Calendar.getInstance();
            actual.setTime(Util.dateSinTime(new Date()));
            Calendar terminoBan = Calendar.getInstance();
            terminoBan.setTime(ban.getFechaBan());
            if(ban.getDiasBan() > 0) {
                terminoBan.add(Calendar.DAY_OF_MONTH, ban.getDiasBan());
                if(actual.after(terminoBan)) {
                    //removemos el ban...
                    Usuario baneado = ban.getBaneado();
                    Grupo grupoBaneados = grupoFac.find("BANEADO");
                    if(grupoBaneados == null)
                        Logger.getLogger(AdminService.class.getName()).log(Level.SEVERE, "GRUPO 'BANEADO' NO EXISTE...");

                    baneado.getGrupos().remove(grupoBaneados);
                    grupoBaneados.getUsuarios().remove(baneado);
                    userFac.edit(baneado);
                    grupoFac.edit(grupoBaneados);
                    BanHistory banHistory = new BanHistory();
                    banHistory.setBaneado(ban.getBaneado());
                    banHistory.setBaneador(ban.getBaneador());
                    banHistory.setDiasBan(ban.getDiasBan());
                    banHistory.setFechaBan(ban.getFechaBan());
                    banHistory.setRazonBan(ban.getRazonBan());
                    banHistoryFac.create(banHistory);
                    banFac.remove(ban);
                    adminService.forzarLogout(ban.getBaneado());
                }
            }else{
                //ban infinito.
            }
        }
    }


    @Schedule(hour="6", minute="0")
    private void eliminarInvitacionesDeClan(Timer timer) {

        List<Clan> clanes = clanFac.findAll();
        for(int i = 0; i < clanes.size(); i++) {
            Clan clan = clanes.get(i);
            List<Usuario> invitados = clan.getInvitaciones();
            for(int j = 0; j < invitados.size(); j++) {
                Usuario invitado = invitados.get(j);
                invitado.getInvitacionesDeClan().clear();
                userFac.edit(invitado);
            }
            clan.getInvitaciones().clear();
            clanFac.edit(clan);
        }
        
    }
    
    @Schedule(hour="7", minute="0")
    private void eliminarDesafiosNoAceptados(Timer timer) {
        
        List<Desafio> desafios = desafioFac.findDesafiosNoAceptados();
        for (int i = 0; i < desafios.size(); i++) {
            Desafio desafio = desafios.get(i);
            desafioFac.remove(desafio);
        }
        
    }
 
}
