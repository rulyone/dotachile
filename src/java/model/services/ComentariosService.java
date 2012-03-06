/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.services;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import model.entities.base.Clan;
import model.entities.base.Comentario;
import model.entities.base.Perfil;
import model.entities.base.Usuario;
import model.entities.base.facades.ClanFacade;
import model.entities.base.facades.ComentarioFacade;
import model.entities.base.facades.PerfilFacade;
import model.entities.base.facades.UsuarioFacade;
import model.entities.noticias.Noticia;
import model.entities.noticias.facades.NoticiaFacade;
import model.entities.torneos.Game;
import model.entities.torneos.GameMatch;
import model.entities.torneos.Ronda;
import model.entities.torneos.Torneo;
import model.entities.torneos.facades.GameFacade;
import model.entities.torneos.facades.GameMatchFacade;
import model.entities.torneos.facades.RondaFacade;
import model.entities.torneos.facades.TorneoFacade;
import model.exceptions.BusinessLogicException;
import utils.Util;

/**
 *
 * @author Pablo
 */
@Stateless
@DeclareRoles({"ADMIN_ROOT", "ADMIN_DOTA", "MODERADOR"})
public class ComentariosService {

    @Resource
    SessionContext ctx;

    @EJB private UsuarioFacade userFac;
    @EJB private ComentarioFacade comentarioFacade;
    @EJB private NoticiaFacade newsFac;
    @EJB private TorneoFacade torneoFac;
    @EJB private GameMatchFacade matchFac;
    @EJB private GameFacade gameFac;
    @EJB private RondaFacade rondaFac;
    @EJB private ClanFacade clanFac;
    @EJB private PerfilFacade perfilFac;

    public void agregarComentarioNoticia(Long idNoticia, String comentario) throws BusinessLogicException {
        if(idNoticia == null || comentario == null)
            throw new BusinessLogicException("Valor requerido.");

        Noticia noticia = newsFac.find(idNoticia);
        if(noticia == null)
            throw new BusinessLogicException("Noticia no encontrada.");
        Principal principal = ctx.getCallerPrincipal();
        Usuario comentador = userFac.findByUsername(principal.getName());
        if(comentador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Date fecha = Util.dateSinMillis(new Date());

        Comentario coment = new Comentario();
        coment.setComentador(comentador);
        coment.setComentario(comentario);
        coment.setDenegado(false);
        coment.setFechaComentario(fecha);

        comentarioFacade.create(coment);
        noticia.getComentarios().add(coment);
        newsFac.edit(noticia);

    }
    public void agregarComentarioTorneo(Long idTorneo, String comentario) throws BusinessLogicException {
        if(idTorneo == null || comentario == null)
            throw new BusinessLogicException("Valor requerido.");

        Torneo torneo = torneoFac.find(idTorneo);
        if(torneo == null)
            throw new BusinessLogicException("Torneo no encontrado.");
        Principal principal = ctx.getCallerPrincipal();
        Usuario comentador = userFac.findByUsername(principal.getName());
        if(comentador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Date fecha = Util.dateSinMillis(new Date());

        Comentario coment = new Comentario();
        coment.setComentador(comentador);
        coment.setComentario(comentario);
        coment.setDenegado(false);
        coment.setFechaComentario(fecha);

        comentarioFacade.create(coment);
        torneo.getComentarios().add(coment);
        torneoFac.edit(torneo);

    }
    public void agregarComentarioMatch(Long idMatch, String comentario) throws BusinessLogicException {
        if(idMatch == null || comentario == null)
            throw new BusinessLogicException("Valor requerido.");

        GameMatch match = matchFac.find(idMatch);
        if(match == null)
            throw new BusinessLogicException("Match no encontrado.");
        Principal principal = ctx.getCallerPrincipal();
        Usuario comentador = userFac.findByUsername(principal.getName());
        if(comentador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Date fecha = Util.dateSinMillis(new Date());

        Comentario coment = new Comentario();
        coment.setComentador(comentador);
        coment.setComentario(comentario);
        coment.setDenegado(false);
        coment.setFechaComentario(fecha);

        comentarioFacade.create(coment);
        match.getComentarios().add(coment);
        matchFac.edit(match);
    }
    public void agregarComentarioGame(Long idGame, String comentario) throws BusinessLogicException {
        if(idGame == null || comentario == null)
            throw new BusinessLogicException("Valor requerido.");

        Game game = gameFac.find(idGame);
        if(game == null)
            throw new BusinessLogicException("Game no encontrado.");
        Principal principal = ctx.getCallerPrincipal();
        Usuario comentador = userFac.findByUsername(principal.getName());
        if(comentador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Date fecha = Util.dateSinMillis(new Date());

        Comentario coment = new Comentario();
        coment.setComentador(comentador);
        coment.setComentario(comentario);
        coment.setDenegado(false);
        coment.setFechaComentario(fecha);

        comentarioFacade.create(coment);
        game.getComentarios().add(coment);
        gameFac.edit(game);
    }
    public void agregarComentarioRonda(Long idRonda, String comentario) throws BusinessLogicException {
        if(idRonda == null || comentario == null)
            throw new BusinessLogicException("Valor requerido.");

        Ronda ronda = rondaFac.find(idRonda);
        if(ronda == null)
            throw new BusinessLogicException("Ronda no encontrada.");
        Principal principal = ctx.getCallerPrincipal();
        Usuario comentador = userFac.findByUsername(principal.getName());
        if(comentador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Date fecha = Util.dateSinMillis(new Date());

        Comentario coment = new Comentario();
        coment.setComentador(comentador);
        coment.setComentario(comentario);
        coment.setDenegado(false);
        coment.setFechaComentario(fecha);

        comentarioFacade.create(coment);
        ronda.getComentarios().add(coment);
        rondaFac.edit(ronda);
    }
    public void agregarComentarioClan(Long idClan, String comentario) throws BusinessLogicException {
        if(idClan == null || comentario == null)
            throw new BusinessLogicException("Valor requerido.");

        Clan clan = clanFac.find(idClan);
        if(clan == null)
            throw new BusinessLogicException("Clan no encontrado.");
        Principal principal = ctx.getCallerPrincipal();
        Usuario comentador = userFac.findByUsername(principal.getName());
        if(comentador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Date fecha = Util.dateSinMillis(new Date());

        Comentario coment = new Comentario();
        coment.setComentador(comentador);
        coment.setComentario(comentario);
        coment.setDenegado(false);
        coment.setFechaComentario(fecha);

        comentarioFacade.create(coment);
        clan.getComentarios().add(coment);
        clanFac.edit(clan);
    }
    public void agregarComentarioPerfil(Long idPerfil, String comentario) throws BusinessLogicException {
        if(idPerfil == null || comentario == null)
            throw new BusinessLogicException("Valor requerido.");

        Perfil perfil = perfilFac.find(idPerfil);
        if(perfil == null)
            throw new BusinessLogicException("Mstch no encontrado.");
        Principal principal = ctx.getCallerPrincipal();
        Usuario comentador = userFac.findByUsername(principal.getName());
        if(comentador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Date fecha = Util.dateSinMillis(new Date());

        Comentario coment = new Comentario();
        coment.setComentador(comentador);
        coment.setComentario(comentario);
        coment.setDenegado(false);
        coment.setFechaComentario(fecha);

        comentarioFacade.create(coment);
        perfil.getComentarios().add(coment);
        perfilFac.edit(perfil);
    }

    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA", "MODERADOR"})
    public void denegarComentario(Long idComentario) throws BusinessLogicException {
        if(idComentario == null)
            throw new BusinessLogicException("Valor requerido.");
        Comentario comentario = comentarioFacade.find(idComentario);
        if(comentario == null)
            throw new BusinessLogicException("Comentario no encontrado.");
        comentario.setDenegado(true);
        comentarioFacade.edit(comentario);
    }
 
}
