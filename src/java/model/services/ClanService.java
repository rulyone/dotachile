/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.services;

import model.entities.base.Confirmacion;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import model.entities.base.*;
import model.entities.base.facades.*;
import model.entities.torneos.FaseTorneo;
import model.entities.torneos.Modificacion;
import model.entities.torneos.TemporadaModificacion;
import model.entities.torneos.TipoModificacion;
import model.entities.torneos.Torneo;
import model.entities.torneos.facades.ModificacionFacade;
import model.entities.torneos.facades.TemporadaModificacionFacade;
import model.exceptions.BusinessLogicException;

/**
 *
 * @author Pablo
 */
@Stateless
@DeclareRoles({"ADMIN_ROOT", "ADMIN_DOTA"})
public class ClanService {
    
    @Resource
    SessionContext ctx;

    @EJB private ClanFacade clanFac;
    @EJB private UsuarioFacade userFac;
    @EJB private TemporadaModificacionFacade tempFac;
    @EJB private ModificacionFacade modFac;
    @EJB private ConfirmacionFacade confirmacionFac;
    @EJB private ClanBanFacade clanBanFac;
    @EJB private MovimientoFacade movFac;

    
    /**
     * Existen 3 actores o factores que influyen como los clanes se organizan.
     * Cuando un clan es recien creado o no esta en torneos activos,
     * este es libre de hacer las modificaciones que quiera.
     *
     * Un clan se le dice recien creado cuando este NO esta inscrito en niun torneo (excepcion LADDER...)
     * Un clan esta en un(os) torneo(s) activo(s) cuando algun(o) (de los) torneo(s) esta en la fase
     * REGISTRATION o STARTED.
     *
     * Cuando un clan esta en un torneo activo, solo los chieftain o shamanes
     * pueden invitar jugadores nuevos al clan, respetando las
     * reglas de la TEMPORADA DE MODIFICACIONES.
     *
     * Con esto en cuenta, un clan sólo puede desarmarse cuando NO tiene torneos activos.
     *
     * Agregaciones se hacen al aceptarInvitacion
     *
     * Con esto en claro, se deben tener en cuenta las siguientes funcionalidades
     * -Crear clan
     * -Invitar player
     * -Aceptar invitacion *(acá se debe hacer la validaciòn respecto a la TEMPORADA DE MODIFICACION)
     * -Rechazar invitacion
     * -Kickear player
     * -Salirse
     * -Desarmar clan
     * -Revivir clan
     * -Promover player (de peon a grunt o a shaman)
     * -Demotear player (de shaman a grunt o a peon)
     * -Traspasar chieftain (debido a q solo 1 puede ser el chieftain de un clan).
     */

    /**
     * Crear clan nuevo.
     * Tomar en cuenta remover las invitaciones del player, si es q tiene alguna.
     *
     * @param nombre Nombre del clan
     * @param tag Tag del clan
     * @throws BusinessLogicException Si el nombre o tag ya existen, si el q lo intenta crear
     * ya es parte de un clan, si no esta logeado (duh)
     */
    public void crearClan(String nombre, String tag) throws BusinessLogicException {
        if(nombre == null || tag == null)
            throw new BusinessLogicException("Valor requerido.");
        if(clanFac.findByNombre(nombre) != null)
            throw new BusinessLogicException("Nombre ya existe.");
        if(clanFac.findByTag(tag) != null)
            throw new BusinessLogicException("Tag ya existe.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        if(user.getClan() != null)
            throw new BusinessLogicException("No debes ser parte de ningun clan.");

        //eliminamos las invitaciones del player...
        for(Clan c : user.getInvitacionesDeClan()) {
            c.getInvitaciones().remove(user);
            clanFac.edit(c);
        }
        user.getInvitacionesDeClan().clear();

        Clan clan = new Clan();
        clan.setChieftain(user);
        clan.setElo(1000);
        clan.setFechaCreacion(new Date());
        List<Usuario> integrantes = new ArrayList<Usuario>();
        integrantes.add(user);
        clan.setIntegrantes(integrantes);
        clan.setNombre(nombre);
        clan.setTag(tag);
        clan.setMovimientos(new ArrayList<Movimiento>());
        
        Movimiento movimiento = new Movimiento();
        movimiento.setClan(clan);
        movimiento.setFechaMovimiento(new Date());
        movimiento.setTipoMovimiento(TipoMovimiento.CREO_CLAN);
        movimiento.setUsuario(user);
        movFac.create(movimiento);

        clan.getMovimientos().add(movimiento);
        clanFac.create(clan);
        user.getMovimientos().add(movimiento);
        user.setClan(clan);
        userFac.edit(user);
    }

    /**
     * Revivir un clan.
     * 
     * @param tag Tag del clan a revivir.
     * @throws BusinessLogicException Si el usuario ya esta en un clan, si el clan no se encuentra,
     * si no fue el ultimo chieftain del clan a revivir, si el clan tiene integrantes (lo cual no debiese
     * ocurrir).
     */
    public void revivirClan(String tag) throws BusinessLogicException {
        if(tag == null)
            throw new BusinessLogicException("Valor requerido.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        if(user.getClan() != null)
            throw new BusinessLogicException("No debes estar en ningun clan para usar esta caracteristica.");

        Clan clan = clanFac.findByTag(tag);
        if(clan == null)
            throw new BusinessLogicException("Clan no encontrado.");

        if(!clan.getChieftain().equals(user))
            throw new BusinessLogicException("Solo el chieftain puede revivir el clan.");

        if(clan.getIntegrantes().size() > 0)
            throw new BusinessLogicException("El clan no debe tener ningun integrante para usar esta caracteristica.");

        for(Clan c : user.getInvitacionesDeClan()) {
            c.getInvitaciones().remove(user);
            clanFac.edit(c);
        }
        user.getInvitacionesDeClan().clear();

        //todo ok (supongo :x)
        clan.getIntegrantes().add(user);

        user.setClan(clan);
        
        Movimiento movimiento = new Movimiento();
        movimiento.setClan(clan);
        movimiento.setFechaMovimiento(new Date());
        movimiento.setTipoMovimiento(TipoMovimiento.REVIVIO_CLAN);
        movimiento.setUsuario(user);
        movFac.create(movimiento);

        user.getMovimientos().add(movimiento);
        userFac.edit(user);
        clan.getMovimientos().add(movimiento);
        clanFac.edit(clan);

    }

    /**
     * Invitar a un player al clan.
     *
     * @param username Username del player a invitar.
     * @throws BusinessLogicException Si el usuario no existe, si el usuario
     * ya tiene clan, si el caller no tiene clan, si no es chieftain o shaman,
     * si el usuario ya fue invitado anteriormente...
     * pd: no es necesario validar las temporada de modificacion, esta validacion se
     * realiza en la funcion aceptarInvitacion.
     */
    public void invitarPlayer(String username) throws BusinessLogicException {
        if(username == null)
            throw new BusinessLogicException("Valor requerido.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario invitador = userFac.findByUsername(principal.getName());
        if(invitador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        Clan clan = invitador.getClan();
        if(clan == null)
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");

        ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan está baneado, razón: " + clanBan.getRazon());
        }
        
        if(!clan.getChieftain().equals(invitador) && !clan.getShamanes().contains(invitador))
            throw new BusinessLogicException("Debes ser chieftain o shaman para invitar players.");

        Usuario invitado = userFac.findByUsername(username);
        if(invitado == null)
            throw new BusinessLogicException("Username incorrecto.");

        if(invitado.getClan() != null)
            throw new BusinessLogicException("Solo puedes invitar a usuarios sin clan.");

        if(invitado.getInvitacionesDeClan().contains(invitador.getClan())) {
            throw new BusinessLogicException("El usuario ya fue invitado, debes esperar que confirme o rechaze la invitacion.");
        }

        clan.getInvitaciones().add(invitado);
        invitado.getInvitacionesDeClan().add(clan);

        clanFac.edit(clan);
        userFac.edit(invitado);

    }

    /**
     * Aceptar la invitacion. IMPORTANTE, ACÁ SE VALIDA TOMANDO EN CUENTA TEMPORADA
     * DE MODIFICACION.
     *
     * @param clantag Tag del Clan que debiese haber invitado al user
     * @throws BusinessLogicException Si el tag es incorrecto o no existe, si no fue invitado por el clan
     * (o expiro el tiempo de invitacion),si el clan esta en un torneo activo
     * (registration o started) y NO estamos en una fecha de modificacion,
     * o si el clan ya utilizo el maximo de agregaciones de la temporada de modificaciones.
     */
    public void aceptarInvitacion(String clantag) throws BusinessLogicException {
        if(clantag == null)
            throw new BusinessLogicException("Valor requerido.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        Clan clan = clanFac.findByTag(clantag);
        if(clan == null)
            throw new BusinessLogicException("Clan tag incorrecto.");

        ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan está baneado, razón: " + clanBan.getRazon());
        }
        
        if(!clan.getInvitaciones().contains(user))
            throw new BusinessLogicException("Debes ser invitado por el clan para poder aceptar la invitacion (valga la redundancia).");

        FacesContext fctx = FacesContext.getCurrentInstance();
        String maxstr = fctx.getExternalContext().getInitParameter("com.tarreo.dota.torneo.maxPlayersPorClan");
        int maxPlayersPorClan = Integer.parseInt(maxstr);
        int numPlayers = clan.getIntegrantes().size();
        if(numPlayers >= maxPlayersPorClan) {
            throw new BusinessLogicException("Clan full (" + numPlayers + " integrantes).");
        }

        List<Torneo> torneosClan = clan.getTorneosActivos();
        if(!torneosClan.isEmpty()) {
            //el clan esta en un torneo activo, checkear si estamos en temporada de modificaciones.
            TemporadaModificacion temp = tempFac.findByDate(new Date());
            if(temp == null) {
                throw new BusinessLogicException("No estamos en temporada de modificaciones, imposible aceptar invitacion.");
            }
            //estamos en temporada de modificacion, veamos si el clan aun no sobre pasa el
            //limite de agregaciones.
            List<Modificacion> modificaciones = modFac.findByTemporadaAndClan(temp, clan);
            int adds = 0;
            for(int i = 0; i < modificaciones.size(); i++) {
                if(modificaciones.get(i).getTipoModificacion().equals(TipoModificacion.AGREGAR))
                    adds++;
            }
            if(temp.getMaxAgregaciones() <= adds)
                throw new BusinessLogicException("El clan ha alcanzado el maximo permitido de agregaciones al clan.");

            //all good, agregamos la modificacion.
            Modificacion mod = new Modificacion();
            mod.setClan(clan);
            mod.setFechaModificacion(new Date());
            mod.setTemporada(temp);
            mod.setTipoModificacion(TipoModificacion.AGREGAR);
            mod.setUsuario(user);
            modFac.create(mod);
            temp.getModificaciones().add(mod);
            tempFac.edit(temp);
        }

        for(Clan c : user.getInvitacionesDeClan()) {
            c.getInvitaciones().remove(user);
            clanFac.edit(c);
        }
        user.getInvitacionesDeClan().clear();
        clan.getIntegrantes().add(user);
        clan.getPeones().add(user);
        
        Movimiento movimiento = new Movimiento();
        movimiento.setClan(clan);
        movimiento.setFechaMovimiento(new Date());
        movimiento.setTipoMovimiento(TipoMovimiento.INGRESO_CLAN);
        movimiento.setUsuario(user);
        movFac.create(movimiento);
        
        clan.getMovimientos().add(movimiento);        
        clanFac.edit(clan);
        user.setClan(clan);
        user.getMovimientos().add(movimiento);
        userFac.edit(user);

    }

    /**
     * Rechazar una invitacion de clan.
     * 
     * @param clantag El tag del clan a rechazar.
     * @throws BusinessLogicException Si el tag no existe, si el clan no lo ha invitado (o se expiro
     * el tiempo de invitacion).
     */
    public void rechazarInvitacion(String clantag) throws BusinessLogicException {
        if(clantag == null)
            throw new BusinessLogicException("Valor requerido.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        Clan clan = clanFac.findByTag(clantag);
        if(clan == null)
            throw new BusinessLogicException("Clan tag incorrecto.");

        if(!user.getInvitacionesDeClan().contains(clan))
            throw new BusinessLogicException("El clan no te ha invitado, imposible rechazar invitacion.");

        clan.getInvitaciones().remove(user);
        user.getInvitacionesDeClan().remove(clan);

        clanFac.edit(clan);
        userFac.edit(user);
    }

    /**
     * Kickear player del clan.
     *
     * @param username El username del player a kickear.
     * @throws BusinessLogicException Si el username no existe, si no tiene clan o no pertenece
     * al mismo clan del removedor, si el removedor no es chieftain ni shaman, o si el removedor
     * es shaman pero el removido tmbn es shaman (solo se puede remover rangos inferiores), si se
     * intenta remover al chieftain (para esto se usa desarmarClan), si se intenta remover a
     * uno mismo (para esto se usa dejarClan).
     */
    public void kickearPlayer(String username) throws BusinessLogicException {
        if(username == null)
            throw new BusinessLogicException("Valor requerido.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        Usuario removido = userFac.findByUsername(username);
        if(removido == null)
            throw new BusinessLogicException("Username incorrecto.");

        Clan clan = user.getClan();
        if(clan == null)
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");

        ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan está baneado, razón: " + clanBan.getRazon());
        }
        
        if(!removido.getClan().equals(clan))
            throw new BusinessLogicException("El usuario a remover debe pertenecer a tu mismo clan.");

        if(removido.equals(user))
            throw new BusinessLogicException("No puedes removerte a ti mismo, para esto utiliza la funcion 'Dejar clan'.");

        if(clan.getChieftain().equals(user)) {
            clan.getIntegrantes().remove(removido);
            clan.getShamanes().remove(removido);
            clan.getGrunts().remove(removido);
            clan.getPeones().remove(removido);
        }else if(clan.getShamanes().contains(user)) {
            if(clan.getShamanes().contains(removido))
                throw new BusinessLogicException("No puedes remover a otro usuario con el mismo rango tuyo.");
            else if(clan.getChieftain().equals(removido))
                throw new BusinessLogicException("No puedes remover a otro usuario con mayor rango que tu.");
            clan.getIntegrantes().remove(removido);
            clan.getGrunts().remove(removido);
            clan.getPeones().remove(removido);
        }else{
            throw new BusinessLogicException("Solo el chieftain y shamanes pueden remover miembros del clan.");
        }

        Movimiento movimiento = new Movimiento();
        movimiento.setClan(clan);
        movimiento.setFechaMovimiento(new Date());
        movimiento.setTipoMovimiento(TipoMovimiento.FUE_KICKEADO_CLAN);
        movimiento.setUsuario(removido);
        movFac.create(movimiento);
        
        removido.getMovimientos().add(movimiento);
        removido.setClan(null);
        userFac.edit(removido);
        clan.getMovimientos().add(movimiento);
        clanFac.edit(clan);

    }

    /**
     * Dejar el clan
     * 
     * @throws BusinessLogicException si no tiene clan, si es chieftain (para esto usar Desarmar clan)
     */
    public void dejarClan() throws BusinessLogicException {
        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        Clan clan = user.getClan();
        if(clan == null)
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");

        if(clan.getChieftain().equals(user))
            throw new BusinessLogicException("Como chieftain debes usar la funcion 'Desarmar clan'.");

        ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan está baneado, razón: " + clanBan.getRazon());
        }
        
        clan.getIntegrantes().remove(user);
        clan.getShamanes().remove(user);
        clan.getGrunts().remove(user);
        clan.getPeones().remove(user);
        user.setClan(null);
        
        Movimiento movimiento = new Movimiento();
        movimiento.setClan(clan);
        movimiento.setFechaMovimiento(new Date());
        movimiento.setTipoMovimiento(TipoMovimiento.DEJO_CLAN);
        movimiento.setUsuario(user);
        movFac.create(movimiento);

        user.getMovimientos().add(movimiento);
        userFac.edit(user);
        clan.getMovimientos().add(movimiento);
        clanFac.edit(clan);
    }

    /**
     * Promover a un player
     *
     * @param username Username del player a promover.
     * @throws BusinessLogicException Si no es el chieftain quien promueve, si el user
     * no pertenece al mismo clan, si se intenta promover a un shaman (para esto se usa Traspasar Chieftain),
     * si el user NO tiene clan...
     */
    public void promover(String username) throws BusinessLogicException {
        if(username == null)
            throw new BusinessLogicException("Valor requerido.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Clan clan = user.getClan();
        if(clan == null)
            throw new BusinessLogicException("Debes tener clan y ser chieftain para usar esta caracteristica.");
        if(!clan.getChieftain().equals(user))
            throw new BusinessLogicException("Solo el chieftain puede modificar rangos de los integrantes.");

        Usuario promovido = userFac.findByUsername(username);
        if(promovido == null)
            throw new BusinessLogicException("Username incorrecto.");

        if(promovido.equals(user))
            throw new BusinessLogicException("No puedes promoverte a ti mismo.");
        if(!clan.equals(promovido.getClan()))
            throw new BusinessLogicException("El player debe pertenecer a tu mismo clan para usar esta caracteristica.");

        if(clan.getShamanes().contains(promovido))
            throw new BusinessLogicException("Solo puede haber un chieftain por clan, para hacer esto debes usar la funcion 'Traspasar Chieftain'.");

        ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan está baneado, razón: " + clanBan.getRazon());
        }
        
        if(clan.getGrunts().contains(promovido)) {
            clan.getGrunts().remove(promovido);
            clan.getShamanes().add(promovido);
        }else if(clan.getPeones().contains(promovido)) {
            clan.getPeones().remove(promovido);
            clan.getGrunts().add(promovido);
        }

        promovido.setClan(clan);
        userFac.edit(user);
        clanFac.edit(clan);

    }

    /**
     * Demotar a un player
     *
     * @param username Username del player a demotear
     * @throws BusinessLogicException Si no es el chieftain quien usa la funcion, si el username
     * no se encuentra, si el player no pertenece al mismo clan, si el usuario ya es peon.
     */
    public void demotear(String username) throws BusinessLogicException {
        if(username == null)
            throw new BusinessLogicException("Valor requerido.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Clan clan = user.getClan();
        if(!clan.getChieftain().equals(user))
            throw new BusinessLogicException("Solo el chieftain puede modificar rangos de los integrantes.");

        Usuario demoteado = userFac.findByUsername(username);
        if(demoteado == null)
            throw new BusinessLogicException("Username incorrecto.");

        if(!clan.equals(demoteado.getClan()))
            throw new BusinessLogicException("El player debe pertenecer a tu mismo clan para usar esta caracteristica.");

        ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan está baneado, razón: " + clanBan.getRazon());
        }
        
        if(clan.getPeones().contains(demoteado)) {
            throw new BusinessLogicException("El usuario ya tiene el menor rango posible.");
        }else if(clan.getGrunts().contains(demoteado)) {
            clan.getGrunts().remove(demoteado);
            clan.getPeones().add(demoteado);
        }else if(clan.getShamanes().contains(demoteado)) {
            clan.getShamanes().remove(demoteado);
            clan.getGrunts().add(demoteado);
        }
        demoteado.setClan(clan);
        userFac.edit(user);
        clanFac.edit(clan);
    }

    /**
     * Desarmar el clan
     * 
     * @throws BusinessLogicException Si no tiene clan, o si el clan tiene mas de
     * 1 integrante, o si el clan está en algún torneo en la fase de REGISTRO...
     */
    public void desarmarClan() throws BusinessLogicException {

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        Clan clan = user.getClan();
        if(clan == null)
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");

        if(clan.getIntegrantes().size() > 1)
            throw new BusinessLogicException("El clan no puede tener mas de 1 integrante (chieftain debe estar solo).");

        ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan está baneado, razón: " + clanBan.getRazon());
        }
        
        List<Torneo> torneos = clan.getTorneos();
        for(int i = 0; i < torneos.size(); i++) {
            Torneo t = torneos.get(i);
            if(t.getFaseTorneo().equals(FaseTorneo.REGISTRATION)) {
                throw new BusinessLogicException("El clan está inscrito en un torneo que aún no comienza, desinscribelo antes de desarmar el clan. Nombre torneo: " + t.getNombre() + " (id:" + t.getId() + ").");
            }
        }

        clan.getIntegrantes().remove(user);
        user.setClan(null);
        
        Movimiento movimiento = new Movimiento();
        movimiento.setClan(clan);
        movimiento.setFechaMovimiento(new Date());
        movimiento.setTipoMovimiento(TipoMovimiento.DESARMO_CLAN);
        movimiento.setUsuario(user);
        movFac.create(movimiento);
        
        clan.getMovimientos().add(movimiento);
        clanFac.edit(clan);
        user.getMovimientos().add(movimiento);
        userFac.edit(user);

    }

    /**
     * Traspasar el chieftain
     *
     * @param username Username del player a traspasar.
     * @throws BusinessLogicException Si el caller no tiene clan, si no es el chieftain,
     * si el username es incorrecto o no existe, si no es del mismo clan el usuario.
     */
    public void traspasarChieftain(String username) throws BusinessLogicException {
        if(username == null)
            throw new BusinessLogicException("Valor requerido.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        Clan clan = user.getClan();
        if(clan == null)
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");

        if(!clan.getChieftain().equals(user))
            throw new BusinessLogicException("Solo el chieftain del clan puede usar esta caracteristica.");

        Usuario newchieftain = userFac.findByUsername(username);
        if(newchieftain == null)
            throw new BusinessLogicException("Username incorrecto.");

        if(newchieftain.equals(user))
            throw new BusinessLogicException("No puedes traspasarte el chieftain a ti mismo...");

        if(newchieftain.getClan() == null || !newchieftain.getClan().equals(user.getClan()))
            throw new BusinessLogicException("Debe pertenecer a tu mismo clan para poder traspasar el chieftain.");

        ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan está baneado, razón: " + clanBan.getRazon());
        }
        
        clan.setChieftain(newchieftain);
        clan.getShamanes().remove(newchieftain);
        clan.getGrunts().remove(newchieftain);
        clan.getPeones().remove(newchieftain);
        clan.getShamanes().add(user);

        user.setClan(clan);
        newchieftain.setClan(clan);

        clanFac.edit(clan);
        userFac.edit(user);
        userFac.edit(newchieftain);

    }
    
    //METODO PARA CONFIRMAR CUALQUIER COSA... SE HIZO A LA RÁPIDA PARA UN MOTIVO BIEN ESPECÍFICO. VER ENTIDAD CONFIRMACION.
    public void confirmar() throws BusinessLogicException {
        
        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        Clan clan = user.getClan();
        if(clan == null)
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");

        if(!clan.getChieftain().equals(user))
            throw new BusinessLogicException("Solo el chieftain del clan puede usar esta caracteristica.");
        Confirmacion test = confirmacionFac.findByTag(clan.getTag());
        if (test != null) {
            throw new BusinessLogicException("Ya confirmó el clan.");
        }
        
        Confirmacion confirmacion = new Confirmacion();
        confirmacion.setClan(clan);
        confirmacion.setFechaConfirmacion(Calendar.getInstance());
        confirmacionFac.create(confirmacion);       
        
    }
    
    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
    public void cambiarTag(String oldTag, String nuevoTag) throws BusinessLogicException {
        if (oldTag == null || nuevoTag == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        Clan clan = clanFac.findByTag(oldTag);
        if (clan == null) {
            throw new BusinessLogicException("Clan no existe.");
        }
        if (nuevoTag.length() > 5) {
            throw new BusinessLogicException("Tag debe ser maximo 5 caracteres.");
        }
        Clan tagOcupado = clanFac.findByTag(nuevoTag);
        if (tagOcupado != null) {
            throw new BusinessLogicException("El tag " + nuevoTag + " ya está ocupado.");
        }
//        if(clanFac.findByNombre(nuevoNombre) != null)
//            throw new BusinessLogicException("Nombre ya existe.");
        
        clan.setTag(nuevoTag);
        //clan.setNombre(nuevoNombre);
        clanFac.edit(clan);
    }
    
    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
    public void cambiarNombre(String tag, String nuevoNombre) throws BusinessLogicException {
        if (tag == null || nuevoNombre == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        Clan clan = clanFac.findByTag(tag);
        if (clan == null) {
            throw new BusinessLogicException("Clan no existe.");
        }
        
        Clan nombreOcupado = clanFac.findByNombre(nuevoNombre);
        if (nombreOcupado != null) {
            throw new BusinessLogicException("El nombre '" + nuevoNombre + "' ya está ocupado.");
        }
//        if(clanFac.findByNombre(nuevoNombre) != null)
//            throw new BusinessLogicException("Nombre ya existe.");
        
        clan.setNombre(nuevoNombre);
        clanFac.edit(clan);
    }
    
}
