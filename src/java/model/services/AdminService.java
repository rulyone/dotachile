/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.services;

import java.security.Principal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.servlet.http.HttpSession;
import model.entities.base.Ban;
import model.entities.base.Clan;
import model.entities.base.ClanBan;
import model.entities.base.Grupo;
import model.entities.base.Usuario;
import model.entities.base.facades.BanFacade;
import model.entities.base.facades.BanHistoryFacade;
import model.entities.base.facades.ClanBanFacade;
import model.entities.base.facades.ClanFacade;
import model.entities.base.facades.GrupoFacade;
import model.entities.base.facades.UsuarioFacade;
import model.exceptions.BusinessLogicException;

/**
 *
 * @author Pablo
 */
@Stateless
@DeclareRoles({"ADMIN_ROOT", "ADMIN_DOTA", "MODERADOR"})
@RolesAllowed({"ADMIN_ROOT"})
public class AdminService {
    
    @Resource SessionContext ctx;

    @EJB private UsuarioFacade userFac;
    @EJB private GrupoFacade grupoFac;
    @EJB private BanFacade banFac;
    @EJB private BanHistoryFacade banHistoryFac;
    @EJB private ClanBanFacade clanBanFac;
    @EJB private ClanFacade clanFac;

    /**
     * EJB PARA DESIGNAR GRUPOS A USUARIOS. . .
     * UTILIZAR EL SESSIONCOLLECTORLISTENER PARA INVALIDAR SESSIONES CUANDO SE MODIFICA
     * EL GRUPO DE CUALQUIER USER (PARA EVITAR PROBLEMAS DE SEGURIDAD).
     *
     * TAMBIEN CREAR UN METODO DE SEGURIDAD, DONDE SE "DESLOGEA" O INVALIDAN
     * TODAS LAS SESSIONES DE LOS USUARIOS QUE ESTAN VISITANDO LA PAGINA.
     **/
    
    public synchronized void logoutAllUsers() {
        Map<Usuario,HttpSession> logeados = Usuario.logins;
        Set<Usuario> keys = logeados.keySet();
        Iterator<Usuario> it = keys.iterator(); 
        while(it.hasNext()) {
            logeados.get(it.next()).invalidate();
        }
    }

    public synchronized void forzarLogout(Usuario usuario) {
        Map<Usuario,HttpSession> logeados = Usuario.logins;
        HttpSession session = logeados.get(usuario);
        session.invalidate();
    }

    public void setearGrupos(String username, List<String> grupos) throws BusinessLogicException {
        if(username == null)
            throw new BusinessLogicException("Valor requerido: username");
        Usuario user = userFac.findByUsername(username);
        if(user == null)
            throw new BusinessLogicException("Usuario no encontrado.");
        user.getGrupos().clear();
        if(grupos != null && grupos.size() > 0) {
            for(int i = 0; i < grupos.size(); i++) {
                String groupname = grupos.get(i);
                if(groupname == null)
                    throw new BusinessLogicException("Grupo no puede ser null.");
                Grupo grupo = grupoFac.find(groupname);
                if(grupo == null)
                    throw new BusinessLogicException("Grupo no existe.");
                user.getGrupos().add(grupo);
                userFac.edit(user);
                grupo.getUsuarios().add(user);
                grupoFac.edit(grupo);
            }
        }
        this.forzarLogout(user);
    }

    public void agregarGrupo(String username, String groupname) throws BusinessLogicException {
        if(username == null || groupname == null)
            throw new BusinessLogicException("Valor requerido.");
        Usuario user = userFac.findByUsername(username);
        if(user == null)
            throw new BusinessLogicException("Usuario no existe.");
        Grupo grupo = grupoFac.find(groupname);
        if(grupo == null)
            throw new BusinessLogicException("Grupo no existe.");
        if(user.getGrupos().contains(grupo))
            throw new BusinessLogicException("Usuario ya pertenece al grupo " + groupname);

        user.getGrupos().add(grupo);
        userFac.edit(user);
        grupo.getUsuarios().add(user);
        grupoFac.edit(grupo);
        this.forzarLogout(user);
    }

    public void removerGrupo(String username, String groupname) throws BusinessLogicException {
        if(username == null || groupname == null)
            throw new BusinessLogicException("Valor requerido.");
        Usuario user = userFac.findByUsername(username);
        if(user == null)
            throw new BusinessLogicException("Usuario no existe.");
        Grupo grupo = grupoFac.find(groupname);
        if(grupo == null)
            throw new BusinessLogicException("Grupo no existe.");
        if(!user.getGrupos().contains(grupo))
            throw new BusinessLogicException("Usuario no pertenece al grupo " + groupname);

        user.getGrupos().remove(grupo);
        userFac.edit(user);
        grupo.getUsuarios().remove(user);
        grupoFac.edit(grupo);
        this.forzarLogout(user);
    }

    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA", "MODERADOR"})
    public void banUser(String username, String razonBan, Integer diasBan) throws BusinessLogicException {
        if(username == null || razonBan == null || diasBan == null)
            throw new BusinessLogicException("Valor requerido.");
        Usuario user = userFac.findByUsername(username);
        if(user == null)
            throw new BusinessLogicException("Usuario no encontrado.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario baneador = userFac.findByUsername(principal.getName());
        if(baneador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        Ban prev = banFac.findByBaneadoUsername(user.getUsername());
        if(prev != null) {
            throw new BusinessLogicException("El usuario ya esta baneado, idban: " + prev.getId());
        }

        Ban ban = new Ban();
        ban.setBaneado(user);
        ban.setBaneador(baneador);
        ban.setDiasBan(diasBan);
        ban.setFechaBan(new Date());
        ban.setRazonBan(razonBan);

        banFac.create(ban);

        Grupo baneados = grupoFac.find("BANEADO");
        if(baneados == null)
            throw new BusinessLogicException("ERROR DE APLICACION");

        if(!user.getGrupos().contains(baneados))
            user.getGrupos().add(baneados);
        userFac.edit(user);
        if(!baneados.getUsuarios().contains(user))
            baneados.getUsuarios().add(user);
        grupoFac.edit(baneados);

        this.forzarLogout(user);
    }


    public void removerFromBaneados(String username) throws BusinessLogicException {
        if(username == null)
            throw new BusinessLogicException("Valor requerido.");
        Usuario baneado = userFac.findByUsername(username);
        if(baneado == null)
            throw new BusinessLogicException("Usuario no encontrado.");

        Grupo baneados = grupoFac.find("BANEADO");
        if(baneados == null)
            throw new BusinessLogicException("ERROR DE APLICACION");
        baneado.getGrupos().remove(baneados);

        userFac.edit(baneado);
        baneados.getUsuarios().remove(baneado);
        grupoFac.edit(baneados);
        this.forzarLogout(baneado);
    }

    public boolean isBanned(String username) throws BusinessLogicException {
        if(username == null)
            throw new BusinessLogicException("Valor requerido.");
        Usuario usuario = userFac.findByUsername(username);
        if(usuario == null)
            throw new BusinessLogicException("Usuario no encontrado.");

        Ban ban = banFac.findByBaneadoUsername(usuario.getUsername());
        if(ban == null)
            return false;

        return true;
    }

    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
    public void banearClan(String tag, String razon) throws BusinessLogicException {
        if (tag == null || razon == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        
        Clan clan = clanFac.findByTag(tag);
        if (clan == null) {
            throw new BusinessLogicException("Clan no encontrado.");
        }
        
        ClanBan clanBan = clanBanFac.findByTag(tag);
        if (clanBan != null) {
            throw new BusinessLogicException("Clan ya está baneado, razon: " + clanBan.getRazon());
        }
        
        clanBan = new ClanBan();
        clanBan.setClanBaneado(clan);
        clanBan.setRazon(razon);
        clanBanFac.create(clanBan);
        
    }
    
    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
    public void desbanearClan(String tag) throws BusinessLogicException {
        if (tag == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        ClanBan clanBan = clanBanFac.findByTag(tag);
        if (clanBan == null) {
            throw new BusinessLogicException("Clan no esta baneado o no existe.");
        }
        clanBanFac.remove(clanBan);
    }
    
}
