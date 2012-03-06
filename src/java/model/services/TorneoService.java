/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import model.entities.base.Clan;
import model.entities.base.ClanBan;
import model.entities.base.Replay;
import model.entities.base.Usuario;
import model.entities.base.facades.*;
import model.entities.torneos.FactorK;
import model.entities.torneos.FaseTorneo;
import model.entities.torneos.Game;
import model.entities.torneos.GameMatch;
import model.entities.torneos.Resultado;
import model.entities.torneos.Ronda;
import model.entities.torneos.TemporadaModificacion;
import model.entities.torneos.TipoTorneo;
import model.entities.torneos.Torneo;
import model.entities.torneos.facades.*;
import model.exceptions.BusinessLogicException;
import utils.Util;

/**
 *
 * Acotaciones sobre los cargos en general.
 * ADMIN_ROOT = lo maximo.
 * ADMIN_DOTA = despues de ADMIN_ROOT
 * ADMIN_TORNEO = despues de ADMIN_DOTA (es staff torneos...)
 *
 * @author Pablo
 */
@Stateless
@DeclareRoles({"ADMIN_ROOT", "ADMIN_DOTA", "ADMIN_TORNEO"})
@RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA", "ADMIN_TORNEO"})
public class TorneoService {

    @Resource
    SessionContext ctx;
    @EJB private TorneoFacade torneoFac;
    @EJB private UsuarioFacade userFac;
    @EJB private ClanFacade clanFac;
    @EJB private RondaFacade rondaFac;
    @EJB private GameMatchFacade matchFac;
    @EJB private GameFacade gameFac;
    @EJB private TemporadaModificacionFacade tempFac;
    @EJB private ModificacionFacade modFac;
    @EJB private ReplayFacade replayFac;
//    @EJB private ConfirmacionFacade confirmacionFac;
    @EJB private ClanBanFacade clanBanFac;

    /**
     * Solo un ADMIN_TORNEO o superior puede crear un torneo. El usuario que lo cree pasa a
     * ser automaticamente el 'Encargado' de el torneo creado.
     * @param nombre El nombre del torneo
     * @param info Informacion del torneo (reglas, etc)
     * @param tipo Tipo de torneo (SINGLE_ELIMINATION, etc...)
     * @param factorK Factor K del sistema de rank de clanes (ELO)
     * @param cantClanes Cantidad maxima de clanes permitidos.
     * @throws BusinessLogicException Si factorK es negativo, si maxCantidadClanes es menor a 2, si
     * maxCantidadClanes es menor a minCantidadClanes, si minCantidadClanes es menor a 2
     * si algun valor es NULL, si el
     * nombre del torneo ya existe...
     */
    public void crearTorneo(String nombre,
            String info,
            TipoTorneo tipo,
            FactorK factorK,
            Integer maxCantidadClanes,
            Integer minCantidadClanes)
            throws BusinessLogicException {

        if (nombre == null || info == null || tipo == null || factorK == null || maxCantidadClanes == null || minCantidadClanes == null) {
            throw new BusinessLogicException("Valor requerido.");
        }

        if (maxCantidadClanes < 2) {
            throw new BusinessLogicException("Max cantidad de clanes debe ser mayor o igual a 2.");
        }
        if (minCantidadClanes < 2) {
            throw new BusinessLogicException("Min cantidad de clanes debe ser mayor o igual a 2.");
        }
        if (maxCantidadClanes < minCantidadClanes) {
            throw new BusinessLogicException("Max cantidad de clanes debe ser mayor o igual a Min cantidad de clanes.");
        }        

        if (torneoFac.findByNombre(nombre) != null) {
            throw new BusinessLogicException("Nombre del torneo ya existe...");
        }

        Principal principal = ctx.getCallerPrincipal();
        Usuario encargado = userFac.findByUsername(principal.getName());

        Torneo torneo = new Torneo();
        //torneo.setClanCampeon(null);
        //torneo.setClanesInscritos(null);
        torneo.setEncargado(encargado);
        torneo.setFactorK(factorK);
        torneo.setFaseTorneo(FaseTorneo.REGISTRATION);
        torneo.setInformacion(info);
        torneo.setMaxCantidadClanes(maxCantidadClanes);
        torneo.setMinCantidadClanes(minCantidadClanes);
        torneo.setNombre(nombre);
        //torneo.setRondas(null);
        torneo.setTipoTorneo(tipo);

        torneoFac.create(torneo);
    }

    /**
     * Eliminar un torneo. Solo se puede eliminar un torneo cuando esta en la fase REGISTRATION.
     *
     * @param idTorneo
     * @throws BusinessLogicException
     */
    @RolesAllowed({"ADMIN_DOTA", "ADMIN_ROOT"})
    public void eliminarTorneo(Long idTorneo) throws BusinessLogicException {
        if (idTorneo == null) {
            throw new BusinessLogicException("Valor requerido.");
        }

        Torneo torneo = torneoFac.find(idTorneo);
        if (torneo == null) {
            throw new BusinessLogicException("Torneo no encontrado.");
        }
        if (!torneo.getFaseTorneo().equals(FaseTorneo.REGISTRATION)) {
            throw new BusinessLogicException("Un torneo solo se puede eliminar en la fase de REGISTRATION.");
        }

        torneoFac.remove(torneo);
    }

    /**
     * Solo el 'Encargado', ADMIN_DOTA o superior puede comenzar un torneo.
     * Para comenzar un torneo deben existir al menos 2 clanes inscritos
     * @param idTorneo ID del torneo a comenzar
     * @throws BusinessLogicException si el torneo NO esta en la fase REGISTRATION,
     * o algun valor es NULL, si no se respeta minCantidadClanes y maxCantidadClanes
     */
    public void startTorneo(Long idTorneo) throws BusinessLogicException {
        if (idTorneo == null) {
            throw new BusinessLogicException("Valor requerido.");
        }

        Torneo torneo = torneoFac.find(idTorneo);
        if (torneo == null) {
            throw new BusinessLogicException("Torneo no existe.");
        }
        if (!torneo.getFaseTorneo().equals(FaseTorneo.REGISTRATION)) {
            throw new BusinessLogicException("Torneo debe estar en la fase de Registro para comenzarlo.");
        }
        int totalInscritos = torneoFac.cantidadClanesInscritos(idTorneo);
        if (totalInscritos < torneo.getMinCantidadClanes()) {
            throw new BusinessLogicException("El minimo de clanes inscritos no se ha alcanzado.");
        }
        if (totalInscritos > torneo.getMaxCantidadClanes()) {
            throw new BusinessLogicException("El maximo de clanes inscritos se ha sobrepasado.");
        }
        List<Clan> clanesInscritos = torneo.getClanesInscritos();
        for (int i = 0; i < clanesInscritos.size(); i++) {
            Clan clan = clanesInscritos.get(i);
            ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
            if (clanBan != null) {
                throw new BusinessLogicException("El clan " + clan.getTag() + " está baneado, razón: " + clanBan.getRazon());
            }
        }
        
        Principal principal = ctx.getCallerPrincipal();
        Usuario encargado = userFac.findByUsername(principal.getName());
        if (encargado == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }

        if (!ctx.isCallerInRole("ADMIN_ROOT") && !ctx.isCallerInRole("ADMIN_DOTA")) {
            if (!torneo.getEncargado().equals(encargado)) {
                throw new BusinessLogicException("Debes ser el Encargado del torneo para comenzarlo (o ADMIN_ROOT y/o ADMIN_DOTA");
            }
        }
        Ronda ronda = new Ronda();
        ronda.setTorneo(torneo);
        ronda.setFechaCreacion(new Date());
        rondaFac.create(ronda);
        torneo.getRondas().add(ronda);
        torneo.setFaseTorneo(FaseTorneo.STARTED);
        torneoFac.edit(torneo);

    }

    /**
     * Solo el 'Encargado', ADMIN_DOTA o superior puede terminar un torneo.
     * Para terminar un torneo deben estar todos los Matchs con resultadoConfirmado = true.
     * @param nombre Nombre del torneo.
     * @param tagCampeon Tag del clan campeon de este torneo.
     * @throws BusinessLogicException Si algun valor es NULL, si el torneo NO esta
     * en la fase STARTED, si el clan campeon NO es parte de los participantes del torneo, si
     * algun match no esta confirmado
     */
    public void finalizarTorneo(Long idTorneo, String tagCampeon) throws BusinessLogicException {
        if (idTorneo == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        Torneo torneo = torneoFac.find(idTorneo);
        if (torneo == null) {
            throw new BusinessLogicException("Torneo no existe.");
        }
        if (!torneo.getFaseTorneo().equals(FaseTorneo.STARTED)) {
            throw new BusinessLogicException("El torneo debe estar comenzado para poder finalizarlo.");
        }

        Principal principal = ctx.getCallerPrincipal();
        Usuario encargado = userFac.findByUsername(principal.getName());
        if (encargado == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }

        if (!ctx.isCallerInRole("ADMIN_ROOT") && !ctx.isCallerInRole("ADMIN_DOTA")) {
            if (!torneo.getEncargado().equals(encargado)) {
                throw new BusinessLogicException("Debes ser el Encargado del torneo para finalizarlo (o ADMIN_ROOT y/o ADMIN_DOTA");
            }
        }
        //TODO: Crear QUERY SQL para mayor eficiencia...
        List<Ronda> rondas = torneo.getRondas();
        for (Ronda r : rondas) {
            List<GameMatch> matches = r.getMatches();
            for (GameMatch m : matches) {
                if (!m.isResultadoConfirmado()) {
                    throw new BusinessLogicException("Hay matchs no confirmados, imposible finalizar torneo.");
                }
            }
        }
        Clan campeon = clanFac.findByTag(tagCampeon);
        if (campeon == null) {
            throw new BusinessLogicException("Clan campeon no existe.");
        }
        if (!torneo.getClanesInscritos().contains(campeon)) {
            throw new BusinessLogicException("El clan campeon NO es parte de los clanes inscritos en el torneo.");
        }

        //modificar elos de la ultima ronda
        //Ronda last = rondas.get(rondas.size() - 1);
        //this.modificarElos(last);

        torneo.setClanCampeon(campeon);
        torneo.setFaseTorneo(FaseTorneo.FINISHED);
        torneoFac.edit(torneo);

        List<Clan> clanes = torneo.getClanesInscritos();
        for(int i = 0; i < clanes.size(); i++) {
            Clan clan = clanes.get(i);
            clan.getTorneos().remove(torneo);
            clan.getTorneos().add(torneo);
            clanFac.edit(clan);
        }

    }

    //@see http://en.wikipedia.org/wiki/Go_ranks_and_ratings#Elo_Ratings_as_used_in_Go
    private void modificarElos(Ronda ronda) {
        int factorK = ronda.getTorneo().getFactorK().getNumero();
        List<GameMatch> matches = ronda.getMatches();
        for(int i = 0; i < matches.size(); i++) {
            GameMatch match = matches.get(i);
            List<Game> games = match.getGames();
            for(int j = 0; j < games.size(); j++) {
                Game game = games.get(j);

                Clan sentinel = game.getSentinel();
                Clan scourge = game.getScourge();

                Resultado resultado = game.getResultado();

                int varSentinel = 0;
                int varScourge = 0;
                if(resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SENTINEL_WO)) {
                    varSentinel = EloSystem.calculoVariacion(sentinel.getElo(), scourge.getElo(), true, factorK);
                    varScourge = EloSystem.calculoVariacion(scourge.getElo(), sentinel.getElo(), false, factorK);
                }else if(resultado.equals(Resultado.WIN_SCOURGE) || resultado.equals(Resultado.WIN_SCOURGE_WO)) {
                    varSentinel = EloSystem.calculoVariacion(sentinel.getElo(), scourge.getElo(), false, factorK);
                    varScourge = EloSystem.calculoVariacion(scourge.getElo(), sentinel.getElo(), true, factorK);
                }

                int eloSentinel = sentinel.getElo();
                int eloScourge = scourge.getElo();
                sentinel.setElo(eloSentinel + varSentinel);
                scourge.setElo(eloScourge + varScourge);

                clanFac.edit(sentinel);
                clanFac.edit(scourge);

            }
        }
        
    }

    /**
     * Solo puede agregar un pareo el 'Encargado', ADMIN_DOTA o superior.
     * Un clan NO puede aparecer en más de 1 Match por ronda,
     * los clanes deben estar ambos registrados.
     * @param tag1 Tag del clan 1.
     * @param tag2 Tag del clan 2.
     * @param idRonda Id de la ronda
     * @param bestOf Mejor de (1,3,5,7,9).
     * @param arbitroUsername Username del arbitro.
     * @throws BusinessLogicException si algun valor es null, si bestof es negativo o numero par,
     * si alguna entidad no existe (a excepcion del arbitro q puede ser null), si alguno de
     * los clanes ya tuvo un pareo en la ronda indicada, si los clanes son identicos, si
     * alguno de los clanes no esta inscrito en el torneo, si la fase del torneo NO es STARTED
     */
    public void agregarPareo(
            String tag1,
            String tag2,
            Long idRonda,
            int bestOf,
            String arbitroUsername,
            Date fechaMatch)
            throws BusinessLogicException {

        if (tag1 == null || tag2 == null || idRonda == null || fechaMatch == null) {
            throw new BusinessLogicException("Valor requerido");
        }
        if (bestOf < 1 || bestOf % 2 == 0) {
            throw new BusinessLogicException("Valor de 'Best Of' debe ser positivo e impar.");
        }
        
        Date actual = Util.dateSinMillis(new Date());
        if(fechaMatch.before(actual))
            throw new BusinessLogicException("La fecha del match debe ser mayor o igual a la actual.");

        Clan clan1 = clanFac.findByTag(tag1);
        Clan clan2 = clanFac.findByTag(tag2);
        if (clan1 == null) {
            throw new BusinessLogicException("Clan " + tag1 + " no registrado.");
        }
        if (clan2 == null) {
            throw new BusinessLogicException("Clan " + tag2 + " no registrado.");
        }
        if (clan1.equals(clan2)) {
            throw new BusinessLogicException("Clanes deben ser distintos.");
        }
        Ronda ronda = rondaFac.find(idRonda);
        if (ronda == null) {
            throw new BusinessLogicException("Ronda no valida o torneo no existe.");
        }
        Torneo torneo = ronda.getTorneo();
        if (!torneo.getFaseTorneo().equals(FaseTorneo.STARTED)) {
            throw new BusinessLogicException("El torneo no ha comenzado o ya fue finalizado.");
        }
//        if (!torneo.getRondas().get(torneo.getRondas().size() - 1).equals(ronda))
//            throw new BusinessLogicException("Solo se pueden agregar pareos a la ultima ronda.");
        //TODO: Crear QUERY
        List<GameMatch> matches = ronda.getMatches();
        for (GameMatch m : matches) {            
            if(m.getClan1().equals(clan1) || m.getClan1().equals(clan2)) {
                throw new BusinessLogicException("El clan " + m.getClan1().getTag() + " ya tiene un pareo en esta ronda.");
            }
            if (m.getClan2().equals(clan1) || m.getClan2().equals(clan2)) {
                throw new BusinessLogicException("El clan " + m.getClan2().getTag() + " ya tiene un pareo en esta ronda.");
            }
        }
        if (!torneo.getClanesInscritos().contains(clan1)) {
            throw new BusinessLogicException("Clan " + clan1.getTag() + " no esta inscrito en el torneo.");
        }
        if (!torneo.getClanesInscritos().contains(clan2)) {
            throw new BusinessLogicException("Clan " + clan2.getTag() + " no esta inscrito en el torneo.");
        }

        Principal principal = ctx.getCallerPrincipal();
        Usuario encargado = userFac.findByUsername(principal.getName());
        if (encargado == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }

        if (!ctx.isCallerInRole("ADMIN_ROOT") && !ctx.isCallerInRole("ADMIN_DOTA")) {
            if (!torneo.getEncargado().equals(encargado)) {
                throw new BusinessLogicException("Debes ser el Encargado del torneo puede agregar un pareo (o un ADMIN_ROOT y/o ADMIN_DOTA");
            }
        }

        Usuario arbitro = null;
        if (arbitroUsername != null) {
            arbitro = userFac.findByUsername(arbitroUsername);
        }

        GameMatch pareo = new GameMatch();
        pareo.setArbitro(arbitro);
        pareo.setBestOf(bestOf);
        pareo.setClan1(clan1);
        pareo.setClan2(clan2);
        pareo.setResultadoConfirmado(false);
        pareo.setRonda(ronda);
        pareo.setFechaMatch(fechaMatch);
        matchFac.create(pareo);
        ronda.getMatches().add(pareo);
        rondaFac.edit(ronda);
    }

    /**
     * Solo el 'Encargado', ADMIN_DOTA o superior puede eliminar un pareo, y este
     * pareo no puede tener games.
     * @param idMatch Id del match
     * @throws BusinessLogicException Si algun valor es NULL, si el match no existe, si
     * el match tiene games reportados, si la fase del torneo NO es STARTED
     */
    public void eliminarPareo(Long idMatch) throws BusinessLogicException {
        if (idMatch == null) {
            throw new BusinessLogicException("Valor requerido.");
        }

        GameMatch match = matchFac.find(idMatch);
        if (match == null) {
            throw new BusinessLogicException("Pareo no encontrado.");
        }
        Torneo torneo = match.getRonda().getTorneo();
        if (!torneo.getFaseTorneo().equals(FaseTorneo.STARTED)) {
            throw new BusinessLogicException("El torneo no ha comenzado o ya fue finalizado.");
        }
        if (match.getGames().size() > 0 || match.isResultadoConfirmado()) {
            throw new BusinessLogicException("El match o pareo ya tiene games reportados y/o el resultado ya fue confirmado.");
        }

        Principal principal = ctx.getCallerPrincipal();
        Usuario encargado = userFac.findByUsername(principal.getName());
        if (encargado == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }

        if (!ctx.isCallerInRole("ADMIN_ROOT") && !ctx.isCallerInRole("ADMIN_DOTA")) {
            if (!torneo.getEncargado().equals(encargado)) {
                throw new BusinessLogicException("Debes ser el Encargado del torneo para eliminar un pareo (o un ADMIN_ROOT y/o ADMIN_DOTA");
            }
        }

        Ronda ronda = match.getRonda();
        ronda.getMatches().remove(match);
        rondaFac.edit(ronda);
        matchFac.remove(match);
        
    }
    
    /**
     * Solo el 'Encargado', ADMIN_DOTA o superior puede eliminar un pareo, 
     * ACÁ SI EL GAME ESTÁ CONFIRMADO, SE LE DARÁ FACTORK/2 AL CLAN Q HABÍA PERDIDO Y 
     * SE LE QUITARÁ FACTORK/2 AL CLAN QUE HABÍA GANADO.
     * @param idMatch Id del match
     * @throws BusinessLogicException Si algun valor es NULL, si el match no existe,
     * si la fase del torneo NO es STARTED
     */
    @RolesAllowed({"ADMIN_DOTA", "ADMIN_ROOT"})
    public void eliminarPareoInseguro(Long idMatch) throws BusinessLogicException {
        if (idMatch == null) {
            throw new BusinessLogicException("Valor requerido.");
        }

        GameMatch match = matchFac.find(idMatch);
        if (match == null) {
            throw new BusinessLogicException("Pareo no encontrado.");
        }
        Torneo torneo = match.getRonda().getTorneo();
        if (!torneo.getFaseTorneo().equals(FaseTorneo.STARTED)) {
            throw new BusinessLogicException("El torneo no ha comenzado o ya fue finalizado.");
        }
//        if (match.getGames().size() > 0 || match.isResultadoConfirmado()) {
//            throw new BusinessLogicException("El match o pareo ya tiene games reportados y/o el resultado ya fue confirmado.");
//        }

        if (!match.isResultadoConfirmado()) {
            throw new BusinessLogicException("Esta función sólo se puede utilizar en games CONFIRMADOS.");
        }
        
        Principal principal = ctx.getCallerPrincipal();
        Usuario encargado = userFac.findByUsername(principal.getName());
        if (encargado == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }

        if (!ctx.isCallerInRole("ADMIN_ROOT") && !ctx.isCallerInRole("ADMIN_DOTA")) {
            if (!torneo.getEncargado().equals(encargado)) {
                throw new BusinessLogicException("Debes ser el Encargado del torneo para eliminar un pareo (o un ADMIN_ROOT y/o ADMIN_DOTA");
            }
        }
        
        /**
         * ASDF
         */
        //TODO: Refactorizar esto y llevarlo a un metodo 'Clan clanGanador(Long idMatch);'.
        List<Game> games = match.getGames();
        int bestof = match.getBestOf();
        int gamesToWin = (int) Math.ceil((double) bestof / 2);
        int len = games.size();
        Clan clan1 = match.getClan1();
        Clan clan2 = match.getClan2();
        int winsClan1 = 0;
        int winsClan2 = 0;

        //sanity check
        if (len > bestof) {
            throw new BusinessLogicException("ERROR, NUMERO DE GAMES SOBREPASA EL PERMITIDO POR 'BESTOF'. Comunicar a un ADMIN.");
        }

        for (int i = 0; i < len; i++) {
            Game game = games.get(i);
            Resultado result = game.getResultado();
            if (result.equals(Resultado.WIN_SENTINEL) || result.equals(Resultado.WIN_SENTINEL_WO)) {
                if (game.getSentinel().equals(clan1) && game.getScourge().equals(clan2)) {
                    //sentinel gano y sentinel es clan1.
                    winsClan1++;
                } else if (game.getSentinel().equals(clan2) && game.getScourge().equals(clan1)) {
                    //sentinel gano y sentinel es clan2
                    winsClan2++;
                } else {
                    //ERROR, bd corrupta :/, no debiese poder ocurrir esto al reportar un game.
                    throw new BusinessLogicException("ERROR, CLANES NO SON LOS INDICADOS POR EL MATCH. Comunicarle esto a un ADMIN.");
                }
            } else if (result.equals(Resultado.WIN_SCOURGE) || result.equals(Resultado.WIN_SCOURGE_WO)) {
                if (game.getSentinel().equals(clan1) && game.getScourge().equals(clan2)) {
                    //scourge gano y scourge es clan2.
                    winsClan2++;
                } else if (game.getSentinel().equals(clan2) && game.getScourge().equals(clan1)) {
                    //scourge gano y scourge es clan1
                    winsClan1++;
                } else {
                    //ERROR, bd corrupta :/, no debiese poder ocurrir esto al reportar un game.
                    throw new BusinessLogicException("ERROR, CLANES NO SON LOS INDICADOS POR EL MATCH. Comunicarle esto a un ADMIN.");
                }
            } else {
                //DOBLE_WO.
                //le damos el win a ambos clanes para "finalizar el match" solamente.
                winsClan1++;
                winsClan2++;
            }
        }
        
        boolean ganoClan1 = false;
        boolean ganoClan2 = false;

        if(winsClan1 == winsClan2 && winsClan1 >= gamesToWin) {
            //Se finalizo el match con puros DOBLE_WO...
            //perdieron ambos clanes, solo un ADMIN puede confirmar el match.
            if(!ctx.isCallerInRole("ADMIN_DOTA") && !ctx.isCallerInRole("ADMIN_ROOT")) {
                throw new BusinessLogicException("Sólo un ADMIN puede confirmar este match.");
            }
        }else if (winsClan1 >= gamesToWin) {
            //gano clan1
            ganoClan1 = true;            
        } else if (winsClan2 >= gamesToWin) {
            //gano clan2
            ganoClan2 = true;            
        }
                
       
//        if (match.isResultadoConfirmado()) {
//            //resultado confirmado, por lo tanto los elos variaron.
//            int factorK = match.getRonda().getTorneo().getFactorK().getNumero();
//            int elo = match.getClan1().getElo();
//            elo -= (factorK/2 * winsClan1);
//            elo += (factorK/2 * winsClan2);
//            match.getClan1().setElo(elo);
//            clanFac.edit(match.getClan1());
//
//            elo = match.getClan2().getElo();
//            elo += (factorK/2 * winsClan1);
//            elo -= (factorK/2 * winsClan2);
//            match.getClan2().setElo(elo);
//            clanFac.edit(match.getClan2());
//
//        }
        
        
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            
             /**
             * 
             */                
    
            Clan sentinel = game.getSentinel();
            Clan scourge = game.getScourge();

            Resultado resultado = game.getResultado();
            int factorK = match.getRonda().getTorneo().getFactorK().getNumero();
            int varSentinel = 0;
            int varScourge = 0;
            if(resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SENTINEL_WO)) {
                varSentinel = EloSystem.calculoVariacion(sentinel.getElo(), scourge.getElo(), true, factorK);
                varScourge = EloSystem.calculoVariacion(scourge.getElo(), sentinel.getElo(), false, factorK);
            }else if(resultado.equals(Resultado.WIN_SCOURGE) || resultado.equals(Resultado.WIN_SCOURGE_WO)) {
                varSentinel = EloSystem.calculoVariacion(sentinel.getElo(), scourge.getElo(), false, factorK);
                varScourge = EloSystem.calculoVariacion(scourge.getElo(), sentinel.getElo(), true, factorK);
            }

            int eloSentinel = sentinel.getElo();
            int eloScourge = scourge.getElo();
            sentinel.setElo(eloSentinel - varSentinel); //aca revertimos la variacion... considerando el elo actual
            scourge.setElo(eloScourge - varScourge); //aca revertimos la variacion... considerando el elo actual.

            clanFac.edit(sentinel);
            clanFac.edit(scourge);
            /**
             * 
             */
            
            gameFac.remove(game);
        }
        match.getGames().clear();
        
        matchFac.edit(match);
        
        Ronda ronda = match.getRonda();
        ronda.getMatches().remove(match);
        rondaFac.edit(ronda);
        matchFac.remove(match);
        
        
    }

    //TODO: ELIMINAR ESTE METODO... NO TIENE SENTIDO...
    /**
     * Se modifica el bestOf de algun match, solo el 'Encargado', ADMIN_DOTA o superior
     * pueden modificarlo.
     * @param idMatch Id del match a modificar
     * @param bestOf Bestof
     * @throws BusinessLogicException Si el match tiene games reportados, si
     * el valor de bestOf es negativo o numero par
     * el bestOf permitido solo es BO1, si el match no se encuentra..., si la
     * fase del torneo NO es STARTED.
     */
    public void modificarBestOfMatch(Long idMatch, int bestOf) throws BusinessLogicException {
        if (idMatch == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        if (bestOf < 1 || bestOf % 2 == 0) {
            throw new BusinessLogicException("Valor de 'Best Of' debe ser positivo e impar.");
        }
        GameMatch match = matchFac.find(idMatch);
        if (match == null) {
            throw new BusinessLogicException("Match o pareo no encontrado.");
        }
        if (match.getGames().size() > 0 || match.isResultadoConfirmado()) {
            throw new BusinessLogicException("El match o pareo ya tiene games reportados y/o el resultado ya fue confirmado.");
        }
        Torneo torneo = match.getRonda().getTorneo();
        if (!torneo.getFaseTorneo().equals(FaseTorneo.STARTED)) {
            throw new BusinessLogicException("El torneo no ha comenzado o ya fue finalizado.");
        }

        Principal principal = ctx.getCallerPrincipal();
        Usuario encargado = userFac.findByUsername(principal.getName());
        if (encargado == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }

        if (!ctx.isCallerInRole("ADMIN_ROOT") && !ctx.isCallerInRole("ADMIN_DOTA")) {
            if (!torneo.getEncargado().equals(encargado)) {
                throw new BusinessLogicException("Debes ser el Encargado del torneo para modificar un pareo (o un ADMIN_ROOT y/o ADMIN_DOTA");
            }
        }

        match.setBestOf(bestOf);
        matchFac.edit(match);

    }

    /**
     * Solo el 'Encargado', ADMIN_DOTA o superior pueden avanzar ronda en un torneo.
     * Avanza la ronda en un torneo, para avanzar ronda, la ultima ronda debe
     * tener al menos 1 match programado, y el torneo debe estar en
     * la fase STARTED.
     * @param idTorneo Id del torneo
     * @throws BusinessLogicException si la ultima ronda tiene matchs pendientes, si el
     * torneo NO esta en la fase STARTED, si el torneo no existe, si algun valor es NULL,
     * si la ultima ronda NO tiene matchs o pareos. . .
     */
    public void avanzarRonda(Long idTorneo) throws BusinessLogicException {
        if (idTorneo == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        Torneo torneo = torneoFac.find(idTorneo);
        if (torneo == null) {
            throw new BusinessLogicException("Torneo no encontrado.");
        }
        Principal principal = ctx.getCallerPrincipal();
        Usuario encargado = userFac.findByUsername(principal.getName());
        if (encargado == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }

        if (!ctx.isCallerInRole("ADMIN_ROOT") && !ctx.isCallerInRole("ADMIN_DOTA")) {
            if (!torneo.getEncargado().equals(encargado)) {
                throw new BusinessLogicException("Debes ser el Encargado del torneo para avanzar de ronda (o un ADMIN_ROOT y/o ADMIN_DOTA");
            }
        }
        if (!torneo.getFaseTorneo().equals(FaseTorneo.STARTED)) {
            throw new BusinessLogicException("Torneo no esta comenzado, verificar que no este en la fase de Registro o Finalizado.");
        }
        //TODO: Crear QUERY
        List<Ronda> rondas = torneo.getRondas();
        Ronda current = rondas.get(rondas.size() - 1);
        List<GameMatch> lastMatches = current.getMatches();
        if (lastMatches.isEmpty()) {
            throw new BusinessLogicException("La ultima ronda no tiene matchs (Pareos), imposible avanzar ronda (debe tener al menos UN pareo/match).");
        }
//        for (GameMatch m : lastMatches) {
//            if (!m.isResultadoConfirmado()) {
//                throw new BusinessLogicException("En la ultima ronda hay matchs (pareos) pendientes o no confirmados. ID: " + m.getId() + " (" + m.getClan1().getTag() + " vs " + m.getClan2().getTag() + ").");
//            }
//        }

        //this.modificarElos(current);

        Ronda ronda = new Ronda();
        ronda.setTorneo(torneo);
        ronda.setFechaCreacion(new Date());
        rondaFac.create(ronda);
        torneo.getRondas().add(ronda);
        torneoFac.edit(torneo);

    }

    /**
     * Solo un SHAMAN/CHIEFTAIN de un clan pueden reportar cuando GANAN. El perdedor NO 
     * puede reportar un game. De otra manera, el 'Encargado', ADMIN_DOTA o superior
     * pueden reportar un game.
     * @param idMatch
     * @param tagSentinel
     * @param tagScourge
     * @param sentinelUsernames
     * @param scourgeUsernames
     * @param resultado
     * @param idReplay
     * @throws BusinessLogicException Si el resultado es DOBLE_WO y el q reporta
     * es parte de uno de los 2 clanes, solo un ADMIN o 'Encargado' puede reportar
     * DOBLE_WO, si los clanes no son parte del MATCH, o si se repiten estos
     * Si se supera el limite de 'Best Of'.
     * TODO: que el arbitro tmbn pueda reportar games.
     */
    @PermitAll
    public void reportarGame(
            Long idMatch,
            String tagSentinel,
            String tagScourge,
            List<Usuario> sentinels,
            List<Usuario> scourges,
            Resultado resultado,
            byte[] replayContent) throws BusinessLogicException {

        if (idMatch == null || tagSentinel == null || tagScourge == null || resultado == null) {
            throw new BusinessLogicException("Valor requerido.");
        }

        Principal principal = ctx.getCallerPrincipal();
        Usuario reportador = userFac.findByUsername(principal.getName());
        if (reportador == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }

        GameMatch match = matchFac.find(idMatch);
        if (match == null) {
            throw new BusinessLogicException("Match no encontrado");
        }
        if (match.isResultadoConfirmado())
            throw new BusinessLogicException("El Match ya fue confirmado, imposible agregar otro game.");
        Clan sentinel = clanFac.findByTag(tagSentinel);
        if (sentinel == null) {
            throw new BusinessLogicException("Clan " + tagSentinel + " no encontrado.");
        }
        Clan scourge = clanFac.findByTag(tagScourge);
        if (scourge == null) {
            throw new BusinessLogicException("Clan " + tagScourge + " no encontrado.");
        }
        if ((!match.getClan1().equals(sentinel) || !match.getClan2().equals(scourge))
                && (!match.getClan1().equals(scourge) || !match.getClan2().equals(sentinel))) {
            throw new BusinessLogicException("Los clanes no son parte del match, o se repite uno.");
        }

        //TODO: HOLA
        List<Game> games = match.getGames();
        int bestof = match.getBestOf();
        int len = games.size();
        if(len >= bestof)
            throw new BusinessLogicException("El numero de games por match ha alcanzado el limite puesto por el parametro 'Best Of'.");

        Game game = new Game();
        game.setSentinel(sentinel);
        game.setScourge(scourge);

        if (ctx.isCallerInRole("ADMIN_DOTA") || ctx.isCallerInRole("ADMIN_ROOT")) {
            //puede reportar todos los tipos de resultados...
            //evitamos q entre a los demas if else...
        } else if (ctx.isCallerInRole("ADMIN_TORNEO")
                && match.getRonda().getTorneo().getEncargado().equals(reportador)
                && !sentinel.getIntegrantes().contains(reportador)
                && !scourge.getIntegrantes().contains(reportador)) {
            //es el encargado y no pertenece a ningun clan involucrado.
            //puede reportar todos los tipos de resultados...
            //evitamos q entre al proximo else...
        } else {
            //veamos si es parte del clan ganador (y SHAMAN o Chieftain a su vez)
            if (resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SENTINEL_WO)) {
                if (!sentinel.getIntegrantes().contains(reportador)) {
                    throw new BusinessLogicException("Debes ser parte del clan GANADOR para reportar.");
                }
                if (!sentinel.getChieftain().equals(reportador) && !sentinel.getShamanes().contains(reportador)) {
                    throw new BusinessLogicException("Debes ser CHIEFTAIN o SHAMAN para reportar.");
                }
            } else if (resultado.equals(Resultado.WIN_SCOURGE) || resultado.equals(Resultado.WIN_SCOURGE_WO)) {
                if (!scourge.getIntegrantes().contains(reportador)) {
                    throw new BusinessLogicException("Debes ser parte del clan GANADOR para reportar.");
                }
                if (!scourge.getChieftain().equals(reportador) && !scourge.getShamanes().contains(reportador)) {
                    throw new BusinessLogicException("Debes ser CHIEFTAIN o SHAMAN  para reportar.");
                }
            } else {
                //no se puede reportar como  DOBLE_WO .
                throw new BusinessLogicException("DOBLE_WO solo puede repotarlo un ADMIN_DOTA o el 'Encargado' del torneo.");
            }
        }
        game.setResultado(resultado);
        List<Usuario> sents = new ArrayList<Usuario>();
        List<Usuario> scrgs = new ArrayList<Usuario>();
        if (resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SCOURGE)) {
            if(sentinels.size() < 3 || scourges.size() < 3 || sentinels.size() > 5 || scourges.size() > 5) {
                throw new BusinessLogicException("Para reportar un game que no sea W.O. deben haber jugado entre 3 a 5 players por clan, sino se debe reportar como W.O.");
            }
            for (Usuario u : sentinels) {
                //COMENTADO ESTO, PARA EVITAR PROBLEMAS A FUTURO EN CASO DE MODIFICACION DE PLANILLA Y HAY UN GAME NO REPORTADO :]
//            if(!sentinel.getIntegrantes().contains(u))
//                throw new BusinessLogicException("El player " + u.getUsername() + " no es parte del clan " + sentinel.getTag());
                if (sents.contains(u)) {
                    throw new BusinessLogicException("Player " + u.getUsername() + " esta duplicado.");
                }
                sents.add(u);
            }
            
            for (Usuario u : scourges) {
                //COMENTADO ESTO, PARA EVITAR PROBLEMAS A FUTURO EN CASO DE MODIFICACION DE PLANILLA Y HAY UN GAME NO REPORTADO :]
//            if(!sentinel.getIntegrantes().contains(u))
//                throw new BusinessLogicException("El player " + u.getUsername() + " no es parte del clan " + scourge.getTag());
                if (sents.contains(u)) {
                    throw new BusinessLogicException("Player " + u.getUsername() + " esta duplicado.");
                }
                scrgs.add(u);
            }
            for (Usuario u : sents) {
                if (scrgs.contains(u)) {
                    throw new BusinessLogicException("Player " + u.getUsername() + " esta en ambos clanes.");
                }
            }
            for (Usuario u : scrgs) {
                if (sents.contains(u)) {
                    throw new BusinessLogicException("Player " + u.getUsername() + " esta en ambos clanes.");
                }
            }
        }

        game.setPlayersSentinel(sents);
        game.setPlayersScourge(scrgs);

        Replay replay = null;
        if(replayContent != null && replayContent.length > 0) {
            replay = new Replay();
            replayFac.create(replay);
            long pseudoID = System.currentTimeMillis();
            String relativeURL = "/uploads/replays/" + pseudoID + "_replay.w3g";
            String absolutePath = "/home/dotachile/UPLOADS/replays/" + pseudoID + "_replay.w3g";
            FileOutputStream out = null;

            try {
                out = new FileOutputStream(new File(absolutePath));
                out.write(replayContent);
                replay.setRelativeUrl(relativeURL);
                replayFac.edit(replay);
                game.setReplay(replay);
            } catch(FileNotFoundException ex) {
                Logger.getLogger(LadderService.class.getName()).log(Level.SEVERE, null, ex);
                throw new BusinessLogicException("Hubo un error al intentar guardar el replay.");
            } catch (IOException ex) {
                Logger.getLogger(LadderService.class.getName()).log(Level.SEVERE, null, ex);
                throw new BusinessLogicException("Hubo un error al intentar guardar el replay.");
            } finally {
                if(out != null) try { out.close(); } catch (IOException ex) { }
            }
        }

        gameFac.create(game);
        match.getGames().add(game);
        matchFac.edit(match);

    }

    /**
     * El shaman/chieftain del clan ganador puede eliminar un reporte
     * si este no esta confirmado, por otra parte el 'Encargado', ADMIN_DOTA
     * o superior puede eliminar un reporte, siempre
     * y cuando este reporte no este confirmado (el match q contenga al game).
     * @param idGame Id del game.
     * @throws BusinessLogicException Si el game no existe, si el q lo elimina
     * es parte del clan ganador pero no es CHIEFTAIN/SHAMAN, o si NO es parte
     * del clan ganador...
     */
    @PermitAll
    public void eliminarReporte(Long idGame) throws BusinessLogicException {
        if (idGame == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        Game game = gameFac.find(idGame);
        if (game == null) {
            throw new BusinessLogicException("Game no encontrado.");
        }
        GameMatch match = matchFac.findByGame(game);
        if (match == null) {
            throw new BusinessLogicException("El game no es parte de un Match (torneo).");
        }
        if (match.isResultadoConfirmado()) {
            throw new BusinessLogicException("El game es parte de un match que ya fue 'Confirmado'.");
        }

        Principal principal = ctx.getCallerPrincipal();
        Usuario reportador = userFac.findByUsername(principal.getName());
        if (reportador == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }

        Resultado resultado = game.getResultado();
        Clan sentinel = game.getSentinel();
        Clan scourge = game.getScourge();
        if (ctx.isCallerInRole("ADMIN_DOTA") || ctx.isCallerInRole("ADMIN_ROOT")) {
            //puede eliminar el reporte
            //evitamos q entre a los proximos else if.
        } else if (ctx.isCallerInRole("ADMIN_TORNEO")
                && match.getRonda().getTorneo().getEncargado().equals(reportador)
                && !sentinel.getIntegrantes().contains(reportador)
                && !scourge.getIntegrantes().contains(reportador)) {
            //es el encargado y no pertenece a ningun clan involucrado.
            //puede eliminar el reporte
            //evitamos q entre al proximo else...
        } else {
            //veamos si es parte del clan ganador (y SHAMAN o Chieftain a su vez)
            if (resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SENTINEL_WO)) {
                if (!sentinel.getIntegrantes().contains(reportador)) {
                    throw new BusinessLogicException("Debes ser parte del clan GANADOR para eliminar un reporte.");
                }
                if (!sentinel.getChieftain().equals(reportador) && !sentinel.getShamanes().contains(reportador)) {
                    throw new BusinessLogicException("Debes ser CHIEFTAIN o SHAMAN para eliminar un reporte.");
                }
            } else if (resultado.equals(Resultado.WIN_SCOURGE) || resultado.equals(Resultado.WIN_SCOURGE_WO)) {
                if (!scourge.getIntegrantes().contains(reportador)) {
                    throw new BusinessLogicException("Debes ser parte del clan GANADOR para eliminar un reporte.");
                }
                if (!scourge.getChieftain().equals(reportador) && !scourge.getShamanes().contains(reportador)) {
                    throw new BusinessLogicException("Debes ser CHIEFTAIN o SHAMAN  para eliminar un reporte.");
                }
            } else {
                //no se puede reportar como DOBLE_WO
                throw new BusinessLogicException("Solo el 'Encargado' del torneo, un ADMIN_DOTA o superior pueden eliminar este reporte (DOBLE_WO).");
            }
        }

        match.getGames().remove(game);
        matchFac.edit(match);
        gameFac.remove(game);
    }

    /**
     * El chieftain/shaman del clan PERDEDOR debe confirmar el MATCH, por tanto debe
     * revisar TODOS los games del match. Por otra parte el 'Encargado' tambien puede
     * confirmar un match, al igual q el ADMIN_DOTA y superiores. Para poder reportar el match,
     * este debe haber sido completamente reportado, por ejemplo si es BO3 y solo existe 1 game,
     * no se puede confirmar el match debido a q deben existir al menos 2 games a favor del mismo clan.
     * @param idMatch
     * @throws BusinessLogicException Si alguien no indicado en las instrucciones
     * intenta confirmar el match (clan perdedor, ADMINs etc), si el game se intenta confirmar
     * sin haber sido completamente reportado tomando en cuenta el Best of X y que un
     * clan debe tener mas de la mitad de los games GANADOS (o doble_wo...).
     */
    @PermitAll
    public void confirmarMatch(Long idMatch) throws BusinessLogicException {
        if (idMatch == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        GameMatch match = matchFac.find(idMatch);
        if (match == null) {
            throw new BusinessLogicException("Match no encontrado.");
        }

        if (match.isResultadoConfirmado()) {
            throw new BusinessLogicException("Match ya fue confirmado anteriormente.");
        }
        //TODO: Refactorizar esto y llevarlo a un metodo 'Clan clanGanador(Long idMatch);'.
        List<Game> games = match.getGames();
        int bestof = match.getBestOf();
        int gamesToWin = (int) Math.ceil((double) bestof / 2);
        int len = games.size();
        Clan clan1 = match.getClan1();
        Clan clan2 = match.getClan2();
        int winsClan1 = 0;
        int winsClan2 = 0;

        //sanity check
        if (len > bestof) {
            throw new BusinessLogicException("ERROR, NUMERO DE GAMES SOBREPASA EL PERMITIDO POR 'BESTOF'. Comunicar a un ADMIN.");
        }

        for (int i = 0; i < len; i++) {
            Game game = games.get(i);
            Resultado result = game.getResultado();
            if (result.equals(Resultado.WIN_SENTINEL) || result.equals(Resultado.WIN_SENTINEL_WO)) {
                if (game.getSentinel().equals(clan1) && game.getScourge().equals(clan2)) {
                    //sentinel gano y sentinel es clan1.
                    winsClan1++;
                } else if (game.getSentinel().equals(clan2) && game.getScourge().equals(clan1)) {
                    //sentinel gano y sentinel es clan2
                    winsClan2++;
                } else {
                    //ERROR, bd corrupta :/, no debiese poder ocurrir esto al reportar un game.
                    throw new BusinessLogicException("ERROR, CLANES NO SON LOS INDICADOS POR EL MATCH. Comunicarle esto a un ADMIN.");
                }
            } else if (result.equals(Resultado.WIN_SCOURGE) || result.equals(Resultado.WIN_SCOURGE_WO)) {
                if (game.getSentinel().equals(clan1) && game.getScourge().equals(clan2)) {
                    //scourge gano y scourge es clan2.
                    winsClan2++;
                } else if (game.getSentinel().equals(clan2) && game.getScourge().equals(clan1)) {
                    //scourge gano y scourge es clan1
                    winsClan1++;
                } else {
                    //ERROR, bd corrupta :/, no debiese poder ocurrir esto al reportar un game.
                    throw new BusinessLogicException("ERROR, CLANES NO SON LOS INDICADOS POR EL MATCH. Comunicarle esto a un ADMIN.");
                }
            } else {
                //DOBLE_WO.
                //le damos el win a ambos clanes para "finalizar el match" solamente.
                winsClan1++;
                winsClan2++;
            }
        }

        Principal principal = ctx.getCallerPrincipal();
        Usuario confirmador = userFac.findByUsername(principal.getName());
        if (confirmador == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }
        boolean ganoClan1 = false;
        boolean ganoClan2 = false;

        if(winsClan1 == winsClan2 && winsClan1 >= gamesToWin) {
            //Se finalizo el match con puros DOBLE_WO...
            //perdieron ambos clanes, solo un ADMIN puede confirmar el match.
            if(!ctx.isCallerInRole("ADMIN_DOTA") && !ctx.isCallerInRole("ADMIN_ROOT")) {
                throw new BusinessLogicException("Sólo un ADMIN puede confirmar este match.");
            }
        }else if (winsClan1 >= gamesToWin) {
            //gano clan1
            ganoClan1 = true;
            if (clan2.getIntegrantes().contains(confirmador)
                    && (clan2.getChieftain().equals(confirmador) || clan2.getShamanes().contains(confirmador))) {
                //el confirmador es parte de los q PERDIERON y es CHIEFTAIN o SHAMAN, puede confirmar.
                //este if es para evitar q entre en el 'else'.
            } else if (ctx.isCallerInRole("ADMIN_DOTA") || ctx.isCallerInRole("ADMIN_ROOT")) {
                //tambien puede confirmar
                //este if es apra evitar q entre en el 'else'.
            } else if (ctx.isCallerInRole("ADMIN_TORNEO") && match.getRonda().getTorneo().getEncargado().equals(confirmador)) {
                //es el encargado, tmbn puede confirmar, a menos q sea parte del clan GANADOR.
                if (clan1.getIntegrantes().contains(confirmador)) {
                    throw new BusinessLogicException("Como 'Encargado' del torneo NO puedes reportar matchs donde participes y el resultado sea a tu favor.");
                }
            } else {
                //no puede confirmar
                throw new BusinessLogicException("Solo el clan PERDEDOR o ADMINS pueden confirmar el resultado de este match.");
            }
        } else if (winsClan2 >= gamesToWin) {
            //gano clan2
            ganoClan2 = true;
            if (clan1.getIntegrantes().contains(confirmador)
                    && (clan1.getChieftain().equals(confirmador) || clan1.getShamanes().contains(confirmador))) {
                //el confirmador es parte de los q PERDIERON y es CHIEFTAIN o SHAMAN, puede confirmar.
                //este if es para evitar q entre en el 'else'.
            } else if (ctx.isCallerInRole("ADMIN_DOTA") || ctx.isCallerInRole("ADMIN_ROOT")) {
                //tambien puede confirmar
                //este if es apra evitar q entre en el 'else'.
            } else if (ctx.isCallerInRole("ADMIN_TORNEO") && match.getRonda().getTorneo().getEncargado().equals(confirmador)) {
                //es el encargado, tmbn puede confirmar, a menos q sea parte del clan GANADOR.
                if (clan2.getIntegrantes().contains(confirmador)) {
                    throw new BusinessLogicException("Como 'Encargado' del torneo NO puedes reportar matchs donde participes y el resultado sea a tu favor.");
                }
            } else {
                //no puede confirmar
                throw new BusinessLogicException("Solo el clan PERDEDOR o ADMINS pueden confirmar el resultado de este match.");
            }
        } else {
            //imposible confirmar, ninguno ha ganado aún.
            throw new BusinessLogicException("Imposible confirmar resultado, no se han jugado el mínimo de games (ya sean WO o no).");
        }

        //se confirma el match.
        match.setResultadoConfirmado(true);
        //se modifican los elos.
        int factorK = match.getRonda().getTorneo().getFactorK().getNumero();
        for(int j = 0; j < games.size(); j++) {
            Game game = games.get(j);

            Clan sentinel = game.getSentinel();
            Clan scourge = game.getScourge();

            Resultado resultado = game.getResultado();

            int varSentinel = 0;
            int varScourge = 0;
            if(resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SENTINEL_WO)) {
                varSentinel = EloSystem.calculoVariacion(sentinel.getElo(), scourge.getElo(), true, factorK);
                varScourge = EloSystem.calculoVariacion(scourge.getElo(), sentinel.getElo(), false, factorK);
            }else if(resultado.equals(Resultado.WIN_SCOURGE) || resultado.equals(Resultado.WIN_SCOURGE_WO)) {
                varSentinel = EloSystem.calculoVariacion(sentinel.getElo(), scourge.getElo(), false, factorK);
                varScourge = EloSystem.calculoVariacion(scourge.getElo(), sentinel.getElo(), true, factorK);
            }

            int eloSentinel = sentinel.getElo();
            int eloScourge = scourge.getElo();
            sentinel.setElo(eloSentinel + varSentinel);
            scourge.setElo(eloScourge + varScourge);

            clanFac.edit(sentinel);
            clanFac.edit(scourge);

        }
               
        matchFac.edit(match);

    }
    //SOLO USAR PA REPORTES WIN SENTINEL O WIN SCOURGE..
    private void modificarElos(Clan clan1, Clan clan2, boolean ganoClan1, int factorK) {

        int varSentinel = 0;
        int varScourge = 0;
        if(ganoClan1) {
            varSentinel = EloSystem.calculoVariacion(clan1.getElo(), clan2.getElo(), true, factorK);
            varScourge = EloSystem.calculoVariacion(clan2.getElo(), clan1.getElo(), false, factorK);
        }else{
            varSentinel = EloSystem.calculoVariacion(clan1.getElo(), clan2.getElo(), false, factorK);
            varScourge = EloSystem.calculoVariacion(clan2.getElo(), clan1.getElo(), true, factorK);
        }

        int eloSentinel = clan1.getElo();
        int eloScourge = clan2.getElo();
        clan1.setElo(eloSentinel + varSentinel);
        clan2.setElo(eloScourge + varScourge);

        clanFac.edit(clan1);
        clanFac.edit(clan2);

    }

    /**
     * Metodo que permite abrir la temporada de modificaciones, instancia ÚNICA en la que los clanes podràn
     * modificar sus planillas cuando estos estàn inscritos en algùn torneo.
     * Es importante entender que cuando un clan es recièn creado, este NO està inscrito en
     * ningùn torneo, por lo que las modificaciones de planilla son en este ÙNICO caso libres,
     * luego de que se inscriban en cualquier torneo, estos ya NO podràn seguir modificando su planilla
     * a rienda suelta, y se someteran al sistema de temporadas para modificacion establecidos por el Staff.
     *
     * @param startDate fecha de inicio inclusiva para permitir modificaciones de clan
     * @param endDate fecha de termino inclusiva para permitir modificaciones de clan
     * @param maxAgregaciones numero maximo de players q se puedan agregar a un clan
     * @param maxRemociones numero maximo de players q se puedan remover de un clan
     * @throws BusinessLogicException Si existe otra temporada de modificacion que se 'solape'
     * a las fechas de esta nueva temporada, si maxAgregaciones y maxRemociones son mayores a 10 o
     * menores a 0, si no se cumple q al menos UNA de las 2 sea mayor a 0 (sino no tendrìa sentido...),
     * si startDate es posterior a endDate, si startDate es anterior a la fecha actual.
     *
     */
    @RolesAllowed({"ADMIN_DOTA", "ADMIN_ROOT"})
    public void crearTemporadaModificaciones(
            Date startDate,
            Date endDate,
            int maxAgregaciones) throws BusinessLogicException {

        if (startDate == null || endDate == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        startDate = Util.dateSinTime(startDate);
        endDate = Util.dateSinTime(endDate);

        if (maxAgregaciones < 1 || maxAgregaciones > 10) {
            throw new BusinessLogicException("Valor de Max.Agregaciones debe ser entre 1 y 10, incluidos.");
        }

        Date actual = Util.dateSinTime(new Date());
        if (startDate.before(actual)) {
            throw new BusinessLogicException("La fecha de inicio debe ser mayor o igual a la fecha actual.");
        }
        if (startDate.after(endDate)) {
            throw new BusinessLogicException("La fecha de termino debe ser posterior o igual a la fecha de inicio.");
        }

        //TODO: crear QUERY para hacer esto... (màx eficiente)
        List<TemporadaModificacion> temporadas = tempFac.findAll();
        for (int i = 0; i < temporadas.size(); i++) {
            TemporadaModificacion temporada = temporadas.get(i);
            //valores de Tiempo (Hora, minutos, segundos, milisegundos.)
            if (!((startDate.before(temporada.getStartDate()) && endDate.before(temporada.getStartDate()))
                    || startDate.after(temporada.getEndDate()))) {
                throw new BusinessLogicException("En el rango de fechas dadas ya existe otra Temporada de modificacion.");
            }
        }

        TemporadaModificacion tmp = new TemporadaModificacion();
        tmp.setEndDate(endDate);
        tmp.setMaxAgregaciones(maxAgregaciones);
        tmp.setStartDate(startDate);
        tempFac.create(tmp);

    }

    /**
     * Eliminar una temporada de modificaciones, metodo util cuando se agrega una por error.
     *
     * @param idTemporada Id de la temporada
     * @throws BusinessLogicException Si la temporada no existe, o si esta
     * ya tiene modificaciones.
     */
    @RolesAllowed({"ADMIN_DOTA", "ADMIN_ROOT"})
    public void eliminarTemporadaModificaciones(Long idTemporada) throws BusinessLogicException {
        if (idTemporada == null) {
            throw new BusinessLogicException("Valor requerido.");
        }

        TemporadaModificacion tmp = tempFac.find(idTemporada);
        if (tmp == null) {
            throw new BusinessLogicException("Temporada no encontrada.");
        }
        if (!tmp.getModificaciones().isEmpty()) {
            throw new BusinessLogicException("La temporada ya tiene cambios de planillas registrados, imposible eliminar.");
        }

        tempFac.remove(tmp);
    }

    /**
     * Inscripcion de un clan a un torneo.
     *
     * @param idTorneo
     * @throws BusinessLogicException Si el torneo no existe, si el usuario
     * no tiene clan, si no es CHIEFTAIN del clan, si el clan ya esta inscrito en el torneo,
     * si el torneo NO esta en la fase REGISTRATION y el maxCantidadClanes no se ha
     * superado, si el minimo y maximo de integrantes POR clan no se cumple.
     */
    @PermitAll
    public void inscribirClanTorneo(Long idTorneo) throws BusinessLogicException {
        if (idTorneo == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        Torneo torneo = torneoFac.find(idTorneo);
        if (torneo == null) {
            throw new BusinessLogicException("Torneo no encontrado.");
        }

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if (user == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }
        Clan clan = user.getClan();
        if (clan == null) {
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");
        }
        if (!clan.getChieftain().equals(user) && !clan.getShamanes().contains(user)) {
            throw new BusinessLogicException("Solo el Chieftain o Shamanes pueden inscribir al clan en un torneo.");
        }
        
        ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan está baneado, razón: " + clanBan.getRazon());
        }
        
        FacesContext fctx = FacesContext.getCurrentInstance();
        String minstr = fctx.getExternalContext().getInitParameter("com.tarreo.dota.torneo.minPlayersPorClan");
        String maxstr = fctx.getExternalContext().getInitParameter("com.tarreo.dota.torneo.maxPlayersPorClan");
        int minPlayersPorClan = Integer.parseInt(minstr);
        int maxPlayersPorClan = Integer.parseInt(maxstr);
        int integrantesSize = clan.getIntegrantes().size();
        if (integrantesSize < minPlayersPorClan || integrantesSize > maxPlayersPorClan) {
            throw new BusinessLogicException("Min players por clan: " + minPlayersPorClan + ", Max players por clan: " + maxPlayersPorClan);
        }

        if (!torneo.getFaseTorneo().equals(FaseTorneo.REGISTRATION)) {
            throw new BusinessLogicException("El torneo No esta en la fase de Registro.");
        }
        int clanesInscritosSize = torneo.getClanesInscritos().size();
        if (clanesInscritosSize >= torneo.getMaxCantidadClanes()) {
            throw new BusinessLogicException("El torneo ha alcanzado el maximo permitido de clanes inscritos.");
        }
        if (torneo.getClanesInscritos().contains(clan)) {
            throw new BusinessLogicException("El clan " + clan.getTag() + " ya esta inscrito en el torneo.");
        }

        //todo ok (?)
        torneo.getClanesInscritos().add(clan);
        clan.getTorneos().add(torneo);
        torneoFac.edit(torneo);
        clanFac.edit(clan);
    }

    /**
     * Desinscribir clan de un torneo.
     *
     * @param idTorneo
     * @throws BusinessLogicException Si el torneo no existe, si el usuario no tiene clan,
     * si no es CHIEFTAIN del clan, si el clan no esta inscrito en el torneo,
     * si el torneo NO esta en la fase REGISTRATION.
     */
    @PermitAll
    public void cancelarInscripcionClanTorneo(Long idTorneo) throws BusinessLogicException {
        if (idTorneo == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        Torneo torneo = torneoFac.find(idTorneo);
        if (torneo == null) {
            throw new BusinessLogicException("Torneo no encontrado.");
        }

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if (user == null) {
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        }
        Clan clan = user.getClan();
        if (clan == null) {
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");
        }
        if (!clan.getChieftain().equals(user)) {
            throw new BusinessLogicException("Solo el Chieftain puede cancelar la inscripcion del clan en un torneo.");
        }

        if (!torneo.getFaseTorneo().equals(FaseTorneo.REGISTRATION)) {
            throw new BusinessLogicException("El torneo No esta en la fase de Registro.");
        }

        if (!torneo.getClanesInscritos().contains(clan)) {
            throw new BusinessLogicException("El clan " + clan.getTag() + " NO esta inscrito en el torneo.");
        }

        //todo ok (?)
        torneo.getClanesInscritos().remove(clan);
        clan.getTorneos().remove(torneo);
        torneoFac.edit(torneo);
        clanFac.edit(clan);
    }

    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
    public void inscribirClanTorneoForced(Long idTorneo, String tagClan) throws BusinessLogicException {
        if (idTorneo == null || tagClan == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        Torneo torneo = torneoFac.find(idTorneo);
        if (torneo == null) {
            throw new BusinessLogicException("Torneo no encontrado.");
        }

        Clan clan = clanFac.findByTag(tagClan);
        if (clan == null) {
            throw new BusinessLogicException("Clan no encontrado.");
        }
        
        ClanBan clanBan = clanBanFac.findByTag(clan.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan está baneado, razón: " + clanBan.getRazon());
        }

//        FacesContext fctx = FacesContext.getCurrentInstance();
//        String minstr = fctx.getExternalContext().getInitParameter("com.tarreo.dota.torneo.minPlayersPorClan");
//        String maxstr = fctx.getExternalContext().getInitParameter("com.tarreo.dota.torneo.maxPlayersPorClan");
//        int minPlayersPorClan = Integer.parseInt(minstr);
//        int maxPlayersPorClan = Integer.parseInt(maxstr);
//        int integrantesSize = clan.getIntegrantes().size();
//        if (integrantesSize < minPlayersPorClan || integrantesSize > maxPlayersPorClan) {
//            throw new BusinessLogicException("Min players por clan: " + minPlayersPorClan + ", Max players por clan: " + maxPlayersPorClan);
//        }

        if (torneo.getFaseTorneo().equals(FaseTorneo.FINISHED)) {
            throw new BusinessLogicException("El torneo ya esta FINALIZADO, solo puedes forzar la inscripcion en torneo STARTED o REGISTRATION.");
        }

        if (torneo.getClanesInscritos().contains(clan)) {
            throw new BusinessLogicException("El clan " + clan.getTag() + " ya esta inscrito en el torneo.");
        }

        //todo ok (?)
        torneo.getClanesInscritos().add(clan);
        clan.getTorneos().add(torneo);
        torneoFac.edit(torneo);
        clanFac.edit(clan);
    }

    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
    public void cancelarInscripcionClanTorneoForced(Long idTorneo, String tagClan) throws BusinessLogicException {
        if (idTorneo == null || tagClan == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        Torneo torneo = torneoFac.find(idTorneo);
        if (torneo == null) {
            throw new BusinessLogicException("Torneo no encontrado.");
        }

        if (torneo.getFaseTorneo().equals(FaseTorneo.FINISHED)) {
            throw new BusinessLogicException("El torneo ya está finalizado.");
        }

        Clan clan = clanFac.findByTag(tagClan);
        if (clan == null) {
            throw new BusinessLogicException("Clan no encontrado.");
        }

        if (!torneo.getClanesInscritos().contains(clan)) {
            throw new BusinessLogicException("El clan " + clan.getTag() + " NO esta inscrito en el torneo.");
        }

        //todo ok (?)
        torneo.getClanesInscritos().remove(clan);
        clan.getTorneos().remove(torneo);
        torneoFac.edit(torneo);
        clanFac.edit(clan);
    }
    
    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
    public void editarTorneo(Long idTorneo, String informacion) throws BusinessLogicException {
        if(idTorneo == null || informacion == null) 
            throw new BusinessLogicException("Valor requerido.");
        Torneo torneo = torneoFac.find(idTorneo);
        if (torneo == null) {
            throw new BusinessLogicException("Torneo no encontrado.");
        }
        torneo.setInformacion(informacion);
        torneoFac.edit(torneo);
    }
    
//    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
//    public void crearPreguntaConfirmacion(String pregunta, Calendar fechaInicio, Calendar fechaTermino, List<String> tagsClanesAConfirmar) throws BusinessLogicException {
//        if (pregunta == null || fechaInicio == null || fechaTermino == null || tagsClanesAConfirmar == null) {
//            throw new BusinessLogicException("Valor requerido.");
//        }
//        
//        Calendar now = Calendar.getInstance();
//        if (fechaInicio.after(fechaTermino)) {
//            throw new BusinessLogicException("Fecha de Inicio no puede ser posterior a la fecha de termino.");
//        }
//        if (fechaInicio.before(now)) {
//            throw new BusinessLogicException("Fecha de Inicio no puede ser posterior a la fecha actual.");
//        }
//        if (tagsClanesAConfirmar.isEmpty()) {
//            throw new BusinessLogicException("No pusiste clanes a confirmar...");
//        }
//        
//        List<Clan> clanesAConfirmar = new ArrayList<Clan>();
//        
//        for (int i = 0; i < tagsClanesAConfirmar.size(); i++) {
//            String tagClan = tagsClanesAConfirmar.get(i);
//            Clan clan = clanFac.findByTag(tagClan);
//            if (clan == null) {
//                throw new BusinessLogicException("Clan no existe. Tag: " + tagClan);
//            }
//            clanesAConfirmar.add(clan);
//        }
//        
//        Confirmacion confirmacion = new Confirmacion();
//        confirmacion.setClanesAConfirmar(clanesAConfirmar);
//        confirmacion.setFechaInicio(fechaInicio);
//        confirmacion.setFechaTermino(fechaTermino);
//        confirmacion.setPregunta(pregunta);
//        
//        confirmacionFac.create(confirmacion);
//        
//    }
//    
//    @PermitAll
//    public void confirmar(Long idConfirmacion) throws BusinessLogicException {
//        if (idConfirmacion == null) {
//            throw new BusinessLogicException("Valor requerido.");
//        }
//        Confirmacion confirmacion = confirmacionFac.find(idConfirmacion);
//        if (confirmacion == null) {
//            throw new BusinessLogicException("Confirmacion no encontrada.");
//        }
//        
//        Principal principal = ctx.getCallerPrincipal();
//        Usuario user = userFac.findByUsername(principal.getName());
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
//        }
//        
//        Clan clan = user.getClan();
//        if (clan == null) {
//            throw new BusinessLogicException("Debes tener clan para poder confirmar.");
//        }
//        if (clan.getIntegrantes().isEmpty()) {
//            throw new BusinessLogicException("El clan está inactivo, imposible confirmar.");
//        }
//        if (!clan.getChieftain().equals(user)) {
//            throw new BusinessLogicException("Solo el chieftain del clan puede confirmar.");
//        }
//        if (!confirmacion.getClanesAConfirmar().contains(clan)) {
//            throw new BusinessLogicException("Tu clan no esta dentro de los clanes a Confirmar.");
//        }
//        if (confirmacion.getClanesConfirmados().contains(clan)) {
//            throw new BusinessLogicException("Tu clan ya confirmo.");
//        }
//        confirmacion.getClanesConfirmados().add(clan);
//        confirmacionFac.edit(confirmacion);
//    }
//    
//    @PermitAll
//    public void eliminarConfirmacion(Long idConfirmacion) throws BusinessLogicException {
//        if (idConfirmacion == null) {
//            throw new BusinessLogicException("Valor requerido.");
//        }
//        Confirmacion confirmacion = confirmacionFac.find(idConfirmacion);
//        if (confirmacion == null) {
//            throw new BusinessLogicException("Confirmacion no encontrada.");
//        }
//        
//        Principal principal = ctx.getCallerPrincipal();
//        Usuario user = userFac.findByUsername(principal.getName());
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
//        }
//        
//        Clan clan = user.getClan();
//        if (clan == null) {
//            throw new BusinessLogicException("No tienes clan.");
//        }
//        
//        if (!clan.getChieftain().equals(user)) {
//            throw new BusinessLogicException("Solo el chieftain del clan puede cancelar confirmacion.");
//        }
//        if (!confirmacion.getClanesAConfirmar().contains(clan)) {
//            throw new BusinessLogicException("Tu clan no esta dentro de los clanes a Confirmar.");
//        }
//        if (!confirmacion.getClanesConfirmados().contains(clan)) {
//            throw new BusinessLogicException("Tu clan no ha confirmado aun.");
//        }
//        confirmacion.getClanesConfirmados().remove(clan);
//        confirmacionFac.edit(confirmacion);
//    }
//    
//    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
//    public void confirmarByAdmin(Long idConfirmacion, Long idClan) throws BusinessLogicException {
//        if (idConfirmacion == null || idClan == null) {
//            throw new BusinessLogicException("Valor requerido.");
//        }
//        Confirmacion confirmacion = confirmacionFac.find(idConfirmacion);
//        if (confirmacion == null) {
//            throw new BusinessLogicException("Confirmacion no encontrada.");
//        }
//        
//        Clan clan = clanFac.find(idClan);
//        if (clan == null) {
//            throw new BusinessLogicException("Clan no encontrado.");
//        }
//        if (clan.getIntegrantes().isEmpty()) {
//            throw new BusinessLogicException("El clan está inactivo, imposible confirmar.");
//        }
//        if (!confirmacion.getClanesAConfirmar().contains(clan)) {
//            throw new BusinessLogicException("El clan no esta dentro de los clanes a Confirmar.");
//        }
//        if (confirmacion.getClanesConfirmados().contains(clan)) {
//            throw new BusinessLogicException("El clan ya confirmo.");
//        }
//        confirmacion.getClanesConfirmados().add(clan);
//        confirmacionFac.edit(confirmacion);
//    }    
//    
//    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
//    public void cancelarConfirmacionByAdmin(Long idConfirmacion, Long idClan) throws BusinessLogicException {
//        if (idConfirmacion == null || idClan == null) {
//            throw new BusinessLogicException("Valor requerido.");
//        }
//        Confirmacion confirmacion = confirmacionFac.find(idConfirmacion);
//        if (confirmacion == null) {
//            throw new BusinessLogicException("Confirmacion no encontrada.");
//        }
//        
//        Clan clan = clanFac.find(idClan);
//        if (clan == null) {
//            throw new BusinessLogicException("Clan no encontrado.");
//        }
//        if (!confirmacion.getClanesAConfirmar().contains(clan)) {
//            throw new BusinessLogicException("El clan no esta dentro de los clanes a Confirmar.");
//        }
//        if (!confirmacion.getClanesConfirmados().contains(clan)) {
//            throw new BusinessLogicException("El clan no ha confirmado.");
//        }
//        confirmacion.getClanesConfirmados().remove(clan);
//        confirmacionFac.edit(confirmacion);
//    }
    
    @PermitAll
    public List<GameMatch> findMatchesPendientesByTag(String tag) {
        List<GameMatch> matches = matchFac.findMatchesPendientesByTag(tag);        
        return matches;
    }
    
    @PermitAll
    public Set<String> getTagsClanesEnTorneo(Long idTorneo) throws BusinessLogicException {
        if (idTorneo == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        
        Torneo torneo = torneoFac.find(idTorneo);
        if (torneo == null) {
            throw new BusinessLogicException("Torneo no encontrado.");
        }
        List<Clan> clanesInscritos = torneo.getClanesInscritos();
        Set<String> tags = new HashSet<String>();
        for (int i = 0; i < clanesInscritos.size(); i++) {
            Clan clan = clanesInscritos.get(i);
            tags.add(clan.getTag());
        }
        return tags;
    }
    
} 
