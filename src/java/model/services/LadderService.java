/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import model.entities.base.Clan;
import model.entities.base.ClanBan;
import model.entities.base.Replay;
import model.entities.base.Usuario;
import model.entities.base.facades.ClanBanFacade;
import model.entities.base.facades.ClanFacade;
import model.entities.base.facades.ReplayFacade;
import model.entities.base.facades.UsuarioFacade;
import model.entities.torneos.Desafio;
import model.entities.torneos.FactorK;
import model.entities.torneos.FaseLadder;
import model.entities.torneos.Game;
import model.entities.torneos.Ladder;
import model.entities.torneos.Resultado;
import model.entities.torneos.facades.DesafioFacade;
import model.entities.torneos.facades.GameFacade;
import model.entities.torneos.facades.LadderFacade;
import model.exceptions.BusinessLogicException;

/**
 *
 * @author Pablo
 */
@Stateless
@DeclareRoles({"ADMIN_ROOT", "ADMIN_DOTA", "ADMIN_TORNEO"})
@RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
public class LadderService {
    
    @Resource SessionContext ctx;

    @EJB private UsuarioFacade userFac;
    @EJB private ClanFacade clanFac;
    @EJB private DesafioFacade desafioFac;
    @EJB private LadderFacade ladderFac;
    @EJB private GameFacade gameFac;
    @EJB private ReplayFacade replayFac;
    @EJB private ClanBanFacade clanBanFac;

    public void crearLadderYComenzarlo(String nombre, String informacion, FactorK factorK) throws BusinessLogicException {
        if(nombre == null || informacion == null || factorK == null)
            throw new BusinessLogicException("Valor requerido.");

        if(ladderFac.count() > 0)
            throw new BusinessLogicException("Por el momento solo se puede tener 1 ladder.");

        Ladder ladder = new Ladder();
        ladder.setFactorK(factorK);
        ladder.setNombre(nombre);
        ladder.setInformacion(informacion);
        ladder.setFaseLadder(FaseLadder.STARTED);

        ladderFac.create(ladder);
    }

    public void pausarLadder(Long id) throws BusinessLogicException {
        if(id == null)
            throw new BusinessLogicException("Valor requerido.");
        Ladder ladder = ladderFac.find(id);
        if(ladder == null)
            throw new BusinessLogicException("Ladder no encontrado.");
        if(ladder.getFaseLadder().equals(FaseLadder.PAUSED))
            throw new BusinessLogicException("El ladder ya esta pausado...");
        ladder.setFaseLadder(FaseLadder.PAUSED);
        ladderFac.edit(ladder);
    }
    
    public void despausarLadder(Long id) throws BusinessLogicException {
        if(id == null)
            throw new BusinessLogicException("Valor requerido.");
        Ladder ladder = ladderFac.find(id);
        if(ladder == null)
            throw new BusinessLogicException("Ladder no encontrado.");
        if(ladder.getFaseLadder().equals(FaseLadder.STARTED))
            throw new BusinessLogicException("El ladder no esta pausado...");
        ladder.setFaseLadder(FaseLadder.STARTED);
        ladderFac.edit(ladder);
    }

    @PermitAll
    public void desafiarClan(String tagRival) throws BusinessLogicException {
        if(tagRival == null)
            throw new BusinessLogicException("Valor requerido.");
        
        Principal principal = ctx.getCallerPrincipal();
        Usuario desafiador = userFac.findByUsername(principal.getName());
        if(desafiador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Clan clanDesafiador = desafiador.getClan();
        if(clanDesafiador == null)
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");
        if(!clanDesafiador.getChieftain().equals(desafiador) && !clanDesafiador.getShamanes().contains(desafiador)){
            throw new BusinessLogicException("Debes ser chieftain o shaman para desafiar a otro clan.");
        }
        Clan clanRival = clanFac.findByTag(tagRival);
        if(clanRival == null)
            throw new BusinessLogicException("El clan rival no existe...");

        ClanBan clanBan = clanBanFac.findByTag(clanDesafiador.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan " + clanDesafiador.getTag() + " está baneado, razón: " + clanBan.getRazon());
        }
        
        clanBan = clanBanFac.findByTag(clanRival.getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan " + clanRival.getTag() + " está baneado, razón: " + clanBan.getRazon());
        }
        
        List<Desafio> desafiosPendientes1 = desafioFac.desafiosPendientes(clanDesafiador.getTag());
        if(desafiosPendientes1.size() > 9)
            throw new BusinessLogicException("Tu clan tiene 10 desafios pendientes (máximo), imposible seguir desafiar a otro clan.");
        List<Desafio> desafiosPendientes2 = desafioFac.desafiosPendientes(clanRival.getTag());
        if(desafiosPendientes2.size() > 9)
            throw new BusinessLogicException("El clan rival tiene 10 desafios pendientes (máximo), imposible desafiarlo.");

        if(clanDesafiador.equals(clanRival))
            throw new BusinessLogicException("No puedes desafiar a tu propio clan.");

        if(clanDesafiador.getIntegrantes().size() < 5)
            throw new BusinessLogicException("Tu clan debe tener al menos 5 integrantes para desafiar.");
        if(clanRival.getIntegrantes().size() < 5)
            throw new BusinessLogicException("El clan rival debe tener al menos 5 integrantes para desafiarlo.");

        Ladder ladder = ladderFac.find(1L); //por el momento el unico ladder tendrá ID = 1...
        if(ladder == null)
            throw new BusinessLogicException("ERROR DE APLICACION... LADDER CON ID 1 DEBE EXISTIR.");
        //todo ok???

        Desafio desafio = new Desafio();
        desafio.setDesafiador(clanDesafiador);
        desafio.setRival(clanRival);
        desafio.setDesafioAceptado(false);
        desafio.setFechaDesafio(new Date());
        desafio.setLadder(ladder);
        desafio.setResultadoConfirmado(false);

        desafioFac.create(desafio);

        ladder.getDesafios().add(desafio);
        ladderFac.edit(ladder);

    }

    @PermitAll
    public void aceptarDesafio(Long idDesafio) throws BusinessLogicException {
        if(idDesafio == null)
            throw new BusinessLogicException("Valor requerido.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Clan clan = user.getClan();
        if(clan == null)
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");
        if(!clan.getChieftain().equals(user) && !clan.getShamanes().contains(user))
            throw new BusinessLogicException("Debes ser chieftain o shaman para aceptar desafios.");

        Desafio desafio = desafioFac.find(idDesafio);
        if(desafio == null)
            throw new BusinessLogicException("Desafio no encontrado.");
        if(!desafio.getRival().equals(clan))
            throw new BusinessLogicException("Debes pertenecer al clan DESAFIADO para aceptar el desafio.");
        if(desafio.isDesafioAceptado())
            throw new BusinessLogicException("El desafio ya fue aceptado...");
        if(desafio.isResultadoConfirmado())
            throw new BusinessLogicException("El resultado ya fue confirmado...");

        if(desafio.getDesafiador().getIntegrantes().size() < 5)
            throw new BusinessLogicException("El clan desafiador debe tener al menos 5 integrantes para aceptar el desafio.");
        if(desafio.getRival().getIntegrantes().size() < 5)
            throw new BusinessLogicException("Tu clan debe tener al menos 5 integrantes para aceptar el desafio.");
        //todo ok?. . .
        
        ClanBan clanBan = clanBanFac.findByTag(desafio.getDesafiador().getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan " + desafio.getDesafiador().getTag() + " está baneado, razón: " + clanBan.getRazon());
        }
        
        clanBan = clanBanFac.findByTag(desafio.getRival().getTag());
        if (clanBan != null) {
            throw new BusinessLogicException("El clan " + desafio.getRival() + " está baneado, razón: " + clanBan.getRazon());
        }
        
        desafio.setDesafioAceptado(true);
        desafioFac.edit(desafio);
    }

    @PermitAll
    public void rechazarDesafio(Long idDesafio) throws BusinessLogicException {
        if(idDesafio == null)
            throw new BusinessLogicException("Valor requerido.");
        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        Clan clan = user.getClan();
        if(clan == null)
            throw new BusinessLogicException("Debes tener clan para usar esta caracteristica.");
        if(!clan.getChieftain().equals(user) && !clan.getShamanes().contains(user))
            throw new BusinessLogicException("Debes ser chieftain o shaman para rechazar desafios.");
        Desafio desafio = desafioFac.find(idDesafio);
        if(desafio == null)
            throw new BusinessLogicException("Desafio no encontrado.");
        if(!desafio.getRival().equals(clan) && !desafio.getDesafiador().equals(clan))
            throw new BusinessLogicException("Debes pertenecer a alguno de los 2 clanes para rechazar el desafio.");
        if(desafio.isDesafioAceptado())
            throw new BusinessLogicException("El desafio ya fue aceptado...");
        if(desafio.isResultadoConfirmado())
            throw new BusinessLogicException("El resultado ya fue confirmado...");
        //todo ok?

        desafioFac.remove(desafio);

    }
    
    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA", "ADMIN_LADDER"})
    public void cancelarDesafioByAdmin(Long idDesafio) throws BusinessLogicException {
        if(idDesafio == null)
            throw new BusinessLogicException("Valor requerido.");
        Principal principal = ctx.getCallerPrincipal();
        Usuario user = userFac.findByUsername(principal.getName());
        if(user == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
               
        Desafio desafio = desafioFac.find(idDesafio);
        if(desafio == null)
            throw new BusinessLogicException("Desafio no encontrado.");
        
        if(desafio.getGame() != null)
            throw new BusinessLogicException("El desafio ya fue reportado...");
        if(desafio.isResultadoConfirmado())
            throw new BusinessLogicException("El resultado ya fue confirmado...");
        //todo ok?

        desafioFac.remove(desafio);

    }
    
    /**
    Long idMatch,
            String tagSentinel,
            String tagScourge,
            List<String> sentinelUsernames,
            List<String> scourgeUsernames,
            Resultado resultado,
            Long idReplay
            **/
    @PermitAll
    public void reportarGameLadder(
            Long idDesafio,
            String tagSentinel,
            String tagScourge,
            List<Usuario> sentinels,
            List<Usuario> scourges,
            Resultado resultado,
            byte[] replayContent) throws BusinessLogicException {

        //solo el clan ganador puede reportar...
        if(idDesafio == null || tagSentinel == null || tagScourge == null || resultado == null)
            throw new BusinessLogicException("Valor requerido.");

        Desafio desafio = desafioFac.find(idDesafio);
        if(desafio == null)
            throw new BusinessLogicException("Desafio no encontrado...");

        if(!desafio.isDesafioAceptado())
            throw new BusinessLogicException("El desafio aun no es aceptado, imposible reportar.");
        if(desafio.isResultadoConfirmado()) 
            throw new BusinessLogicException("El desafio ya fue reportado y confirmado.");
        if(desafio.getGame() != null)
            throw new BusinessLogicException("El desafio ya fue reportado, debe ser confirmado o eliminado el reporte para reportar nuevamente.");
        Principal principal = ctx.getCallerPrincipal();
        Usuario reportador = userFac.findByUsername(principal.getName());
        if(reportador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        

        Clan desafiador = desafio.getDesafiador();
        Clan rival = desafio.getRival();

        Clan sentinel = clanFac.findByTag(tagSentinel);
        Clan scourge = clanFac.findByTag(tagScourge);

        if(sentinel == null)
            throw new BusinessLogicException("Clan sentinel no existe.");
        if(scourge == null)
            throw new BusinessLogicException("Clan scourge no existe.");

        if(tagSentinel.equals(tagScourge))
            throw new BusinessLogicException("Clanes identicos...");

        if(ctx.isCallerInRole("ADMIN_ROOT") || ctx.isCallerInRole("ADMIN_DOTA")) {
            //puede reportar sin ataos, veamos si los clanes estan bien...
            if(!(desafiador.getTag().equals(tagSentinel) && rival.getTag().equals(tagScourge))
                    && !(desafiador.getTag().equals(tagScourge) && rival.getTag().contains(tagSentinel))) {
                throw new BusinessLogicException("Clanes incorrectos...");
            }
        }else{
            Clan clan = reportador.getClan();
            if(clan == null)
                throw new BusinessLogicException("Debes tener clan para reportar un resultado...");
            if(!desafio.getDesafiador().equals(clan) && !desafio.getRival().equals(clan))
                throw new BusinessLogicException("Debes pertenecer a alguno de los 2 clanes para reportar un resultado.");
            if(desafiador.getTag().equals(tagSentinel) && rival.getTag().equals(tagScourge)) {
                if(resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SENTINEL_WO)) {
                    //desafiador jugo de sentinel y GANO
                     if(!desafiador.equals(reportador.getClan()))
                        throw new BusinessLogicException("Solo el clan ganador puede reportar.");
                }else if(resultado.equals(Resultado.WIN_SCOURGE) || resultado.equals(Resultado.WIN_SCOURGE_WO)) {
                    //desafiador jugo de sentinel y PERDIO
                     if(desafiador.equals(reportador.getClan()))
                        throw new BusinessLogicException("Solo el clan ganador puede reportar.");
                }else{
                    //DOBLE WO... cancelaron el desafio... cualquiera puede reportar.
                }
            }else if(desafiador.getTag().equals(tagScourge) && rival.getTag().equals(tagSentinel)){
                if(resultado.equals(Resultado.WIN_SENTINEL)) {
                    //desafiador jugo de scourge y PERDIO
                    if(desafiador.equals(reportador.getClan()))
                        throw new BusinessLogicException("Solo el clan ganador puede reportar.");
                }else if(resultado.equals(Resultado.WIN_SENTINEL_WO)) {
                    //desafiador jugo de scourge y PERDIO por WO
                    if(desafiador.equals(reportador.getClan()))
                        throw new BusinessLogicException("Solo el clan ganador puede reportar.");
                }else if(resultado.equals(Resultado.WIN_SCOURGE)) {
                    //desafiador jugo de scourge y GANO
                    if(!desafiador.equals(reportador.getClan()))
                        throw new BusinessLogicException("Solo el clan ganador puede reportar.");
                }else if(resultado.equals(Resultado.WIN_SCOURGE_WO)){
                    //desafiador jugo de scourge y GANO por WO.
                    if(!desafiador.equals(reportador.getClan()))
                        throw new BusinessLogicException("Solo el clan ganador puede reportar.");
                }else{
                    //DOBLE WO... cancelaron el desafio... cualquiera puiede reportar.
                }
            }else{
                throw new BusinessLogicException("Clanes incorrectos...");
            }
        }
        
        
        List<Usuario> sents = new ArrayList<Usuario>();
        List<Usuario> scrgs = new ArrayList<Usuario>();
        if (resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SCOURGE)) {
            if(sentinels.size() < 3 || scourges.size() < 3 || sentinels.size() > 5 || scourges.size() > 5) {
                throw new BusinessLogicException("Para reportar un game que no sea W.O. deben haber jugado entre 3 a 5 players por clan, sino se debe reportar como W.O.");
            }
            for (Usuario u : sentinels) {                
                if (sents.contains(u)) {
                    throw new BusinessLogicException("Player " + u.getUsername() + " esta duplicado.");
                }
                sents.add(u);
            }
            
            for (Usuario u : scourges) {                
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
        Game game = new Game();
        game.setSentinel(sentinel);
        game.setScourge(scourge);
        game.setResultado(resultado);
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

        desafio.setGame(game);
        desafioFac.edit(desafio);

    }

    @PermitAll
    public void eliminarReporteLadder(Long idDesafio) throws BusinessLogicException {
        //solo el clan ganador puede eliminar el reporte (ya que ellos lo reportaron...)
        //y los admins.
        if(idDesafio == null)
            throw new BusinessLogicException("Valor requerido.");
        Desafio desafio = desafioFac.find(idDesafio);
        if(desafio == null)
            throw new BusinessLogicException("Desafio no encontrado.");
        Game game = desafio.getGame();
        if(game == null)
            throw new BusinessLogicException("Game no encontrado.");
        if(desafio.isResultadoConfirmado())
            throw new BusinessLogicException("El resultado ya fue confirmado.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario reportador = userFac.findByUsername(principal.getName());
        if(reportador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        

        Resultado resultado = game.getResultado();
        Clan sentinel = game.getSentinel();
        Clan scourge = game.getScourge();
        if (ctx.isCallerInRole("ADMIN_DOTA") || ctx.isCallerInRole("ADMIN_ROOT")) {
            //puede eliminar el reporte
            //evitamos q entre a los proximos else if.
        } else {
            Clan clan = reportador.getClan();
            if(clan == null)
                throw new BusinessLogicException("Debes tener clan para eliminar un resultado...");
            if(!desafio.getDesafiador().equals(clan) && !desafio.getRival().equals(clan))
                throw new BusinessLogicException("Debes pertenecer a alguno de los 2 clanes para eliminar un resultado.");
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
                //DOBLE WO... cualquiera puiede eliminar el reporte.
            }
        }

        gameFac.remove(game);
        desafio.setGame(null);
        desafioFac.edit(desafio);

    }

    @PermitAll
    public void confirmarResultadoDesafio(Long idDesafio) throws BusinessLogicException {
        if(idDesafio == null)
            throw new BusinessLogicException("Valor requerido.");
        Desafio desafio = desafioFac.find(idDesafio);
        if(desafio == null)
            throw new BusinessLogicException("Desafio no encontrado.");
        Game game = desafio.getGame();
        if(game == null)
            throw new BusinessLogicException("Game no encontrado.");
        if(desafio.isResultadoConfirmado())
            throw new BusinessLogicException("El resultado ya fue confirmado.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario reportador = userFac.findByUsername(principal.getName());
        if(reportador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");
        
        Resultado resultado = game.getResultado();
        Clan sentinel = game.getSentinel();
        Clan scourge = game.getScourge();
        //el perdedor puede reportar, a menos q sea admin
        if(ctx.isCallerInRole("ADMIN_ROOT") || ctx.isCallerInRole("ADMIN_DOTA")) {
            //puede confirmar.
        }else{
            Clan clan = reportador.getClan();
            if(clan == null)
                throw new BusinessLogicException("Debes tener clan para confirmar un resultado...");
            if(!sentinel.equals(clan) && !scourge.equals(clan))
                throw new BusinessLogicException("Debes pertenecer a alguno de los 2 clanes para confirmar un resultado.");
            if(!clan.getChieftain().equals(reportador) && !clan.getShamanes().contains(reportador))
                throw new BusinessLogicException("Debes ser CHIEFTAIN o SHAMAN para confirmar resultado.");
            if (resultado.equals(Resultado.WIN_SENTINEL) || resultado.equals(Resultado.WIN_SENTINEL_WO)) {
                if(!scourge.getIntegrantes().contains(reportador)) {
                    throw new BusinessLogicException("Debes pertenecer al clan perdedor para confirmar resultado.");
                }
            }else if(resultado.equals(Resultado.WIN_SCOURGE) || resultado.equals(Resultado.WIN_SCOURGE_WO)) {
                if(!sentinel.getIntegrantes().contains(reportador)) {
                    throw new BusinessLogicException("Debes pertenecer al clan perdedor para confirmar resultado.");
                }
            }else {
                //doble wo, SÓLO ADMIN_DOTA Y ADMIN_ROOT PUEDE REPORTAR.
                throw new BusinessLogicException("Sólo los ADMINS pueden confirmar DOBLE_WO's.");
            }
        }

        desafio.setResultadoConfirmado(true);
        desafioFac.edit(desafio);

        if(!game.getResultado().equals(Resultado.DOBLE_WO))
            this.modificarElos(game, desafio.getLadder().getFactorK().getNumero());
        
    }

    private void modificarElos(Game game, int factorK) {

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
