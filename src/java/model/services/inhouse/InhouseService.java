///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package model.services.inhouse;
//
//import model.entities.inhouse.BanIH;
//import model.entities.inhouse.GameIH;
//import model.entities.inhouse.ModeIH;
//import model.entities.inhouse.PickModeIH;
//import model.entities.inhouse.StatusIH;
//import model.entities.inhouse.PlayerIH;
//import model.entities.inhouse.facades.BanIHFacade;
//import model.entities.inhouse.facades.GameIHFacade;
//import model.entities.inhouse.facades.PlayerIHFacade;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import javax.annotation.Resource;
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//import javax.ejb.LocalBean;
//import javax.ejb.SessionContext;
//import model.entities.base.Usuario;
//import model.entities.inhouse.ChatLineIH;
//import model.entities.inhouse.facades.ChatLineIHFacade;
//import model.exceptions.BusinessLogicException;
//import utils.Util;
//
///**
// *
// * @author rulyone
// */
//@Stateless
//@LocalBean
//public class InhouseService {
//
//    @Resource
//    SessionContext ctx;
//    private @EJB
//    PlayerIHFacade playerFac;
//    private @EJB
//    GameIHFacade gameFac;
//    private @EJB
//    BanIHFacade banFac;
//    private @EJB
//    ChatLineIHFacade chatFac;
//
//    /**
//     * Players
//     */
//    /*
//     * Comienza un nuevo game. Este puede ser en cualquier modo. 
//     * El player no puede tener games pendientes, 
//     * y además queda como capitán de un team (al estar primero en la lista del pool).
//     */
//    public void startGame(ModeIH mode, PickModeIH pickMode) throws BusinessLogicException {
//
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        
//        PlayerIH player = user.getPlayerIH();
//        
//        List<GameIH> pendientes = gameFac.gamesFaseSigneoByPlayer(player);
//        if (!pendientes.isEmpty()) {
//            throw new BusinessLogicException("Tienes games pendientes.");
//        }
//        pendientes = gameFac.gamesFasePickByPlayer(player);
//        if (!pendientes.isEmpty()) {
//            throw new BusinessLogicException("Tienes games pendientes.");
//        }
//        pendientes = gameFac.gamesFasePlayingByPlayer(player);
//        if (!pendientes.isEmpty()) {
//            throw new BusinessLogicException("Tienes games pendientes.");
//        }
//        
//        if (!pickMode.equals(PickModeIH.SINGLE_ALWAYS) && !pickMode.equals(PickModeIH.SINGLE_FIRST_AND_LAST)) {
//            throw new BusinessLogicException("Modo de pickeo no implementado aún");
//        }
//
//        GameIH game = new GameIH();
//        game.setStatus(StatusIH.SIGN_PHASE);
//        game.setMode(mode);
//        game.setCreationDate(Calendar.getInstance());
//        game.setStartDate(null);
//        game.setPlayersTeamA(null);
//        game.setPlayersTeamB(null);
//        List<PlayerIH> pool = new ArrayList<PlayerIH>();
//        pool.add(player);
//        game.setPool(pool);
//        game.setReports(null);
//        game.setCapitanTeamAReady(false);
//        game.setCapitanTeamBReady(false);
//        game.setPickMode(pickMode);
//        game.setChat(new ArrayList<ChatLineIH>());
//        
//        gameFac.create(game);
//        
//        ChatLineIH chatLine = new ChatLineIH();
//        chatLine.setFecha(Calendar.getInstance());
//        chatLine.setPlayer(null);
//        chatLine.setText("Juego comenzado.");
//        chatLine.setGameIH(game);
//        chatFac.create(chatLine);
//        game.getChat().add(chatLine);
//        gameFac.edit(game);
//        player.getGamesSigned().add(game);
//        playerFac.edit(player);
//
//    }
//
//    /*
//     * Signea o entra a un game. El game debe estar en la fase de SIGNEO,
//     * y el player NO puede tener games pendientes 
//     * (pertenece a un game que NO está FINALIZADO). 
//     * El primero en entrar a un game, queda como capitán del otro team inmediatamente 
//     * (al estar segundo en la lista del pool).
//     */
//    public void sign(Long idGame) throws BusinessLogicException {
//        Usuario user = Util.getUsuarioLogeado();
//        
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        PlayerIH player = user.getPlayerIH();
//        
//        GameIH game = gameFac.find(idGame);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe...");
//        }
//
//        if (!game.getStatus().equals(StatusIH.SIGN_PHASE)) {
//            throw new BusinessLogicException("El game debe estar en la fase de SIGNEO.");
//        }
//
//        List<GameIH> pendientes = gameFac.gamesFaseSigneoByPlayer(player);
//        if (!pendientes.isEmpty()) {
//            throw new BusinessLogicException("Tienes games pendientes.");
//        }
//        pendientes = gameFac.gamesFasePickByPlayer(player);
//        if (!pendientes.isEmpty()) {
//            throw new BusinessLogicException("Tienes games pendientes.");
//        }
//        pendientes = gameFac.gamesFasePlayingByPlayer(player);
//        if (!pendientes.isEmpty()) {
//            throw new BusinessLogicException("Tienes games pendientes.");
//        }
//
//        game.getPool().add(player);
//        gameFac.edit(game);
//        player.getGamesSigned().add(game);
//        playerFac.edit(player);
//
//    }
//
//    /*
//     * Outea o se sale de un game. 
//     * El game debe estar en la fase de SIGNEO y el player debe estar Signeado a ese game.
//     * No puede salirse del game ni el creador ni el primero en signear... si uno de ellos
//     * se sale, el game se debe cancelar y eliminar.
//     */
//    public void out(Long idGame) throws BusinessLogicException {
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        PlayerIH player = user.getPlayerIH();
//        
//        GameIH game = gameFac.find(idGame);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe...");
//        }
//        if (!game.getStatus().equals(StatusIH.SIGN_PHASE)) {
//            throw new BusinessLogicException("El game debe estar en la fase de SIGNEO.");
//        }
//        List<PlayerIH> pool = game.getPool();
//        
//        if (!pool.contains(player)) {
//            throw new BusinessLogicException("Debes haber signeado al game antes para que puedas salirte del mismo.");
//        }
//        if (pool.get(0).equals(player)) {
//            throw new BusinessLogicException("Para salirte, debes CANCELAR el match por ser capitán (1°).");
//        }
//        if (pool.size() > 1 && pool.get(1).equals(player)) {
//            throw new BusinessLogicException("Para salirte, debes CANCELAR el match por ser capitán (2°).");
//        }
//
//        game.getPool().remove(player);
//        gameFac.edit(game);
//        player.getGamesSigned().remove(game);
//        playerFac.edit(player);
//
//    }
//    
//    /*
//     * Sólo el capitán de cualquiera de los 2 teams puede cancelar el game.
//     * El game debe estar en la fase de SIGNEO.
//     */
//    public void reject(Long idGame) throws BusinessLogicException {
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        PlayerIH player = user.getPlayerIH();
//        
//        GameIH game = gameFac.find(idGame);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe...");
//        }
//        if (!game.getStatus().equals(StatusIH.SIGN_PHASE)) {
//            throw new BusinessLogicException("El game debe estar en la fase de SIGNEO.");
//        }
//        List<PlayerIH> pool = game.getPool();
//        if (!pool.contains(player)) {
//            throw new BusinessLogicException("Debes haber signeado al game y ser capitán para poder cancelar el game.");
//        }
//        boolean puedeCancelar = false;
//        if (pool.size() > 0 && pool.get(0) != null && pool.get(0).equals(player)) {
//            puedeCancelar = true;
//        }
//        if (pool.size() > 1 && pool.get(1) != null && pool.get(1).equals(player)) {
//            puedeCancelar = true;
//        }
//        if (!puedeCancelar) {
//            throw new BusinessLogicException("Debes ser capitán para poder cancelar el game.");
//        }
//        
//        for (int i = 0; i < pool.size(); i++) {
//            PlayerIH player1 = pool.get(i);
//            player1.getGamesSigned().remove(game);
//            playerFac.edit(player1);
//        }
//        List<PlayerIH> playersTeamA = game.getPlayersTeamA();
//        for (int i = 0; i < playersTeamA.size(); i++) {
//            PlayerIH player1 = playersTeamA.get(i);
//            player1.getGamesTeamA().remove(game);
//            playerFac.edit(player1);
//        }
//        List<PlayerIH> playersTeamB = game.getPlayersTeamB();
//        for (int i = 0; i < playersTeamB.size(); i++) {
//            PlayerIH player1 = playersTeamB.get(i);
//            player1.getGamesTeamB().remove(game);
//            playerFac.edit(player1);
//        }
//        gameFac.remove(game);
//    }
//
//    /*
//     * Acusa a un player por salirse de la partida.
//     * Sólo se puede truantear cuando el game esta en la fase PLAYING 
//     * y tanto el truanteado como los truanteadores deben pertenecer a ese game. 
//     * El truanteado no puede seguir siendo truanteado si ya fue baneado.
//     * Si se alcanza a los 7 votos necesarios para un truant, 
//     * el player es baneado por 2 dias completos.
//     * 
//     * @returns El username del player truanteado exitosamente.
//     */
//    public String truant(Long idGame, Long idTruanteado) throws BusinessLogicException {
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        PlayerIH player = user.getPlayerIH();
//        
//        GameIH game = gameFac.find(idGame);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe...");
//        }
//        if (!game.getStatus().equals(StatusIH.PLAYING_PHASE)) {
//            throw new BusinessLogicException("El game debe estar en la fase de JUEGO.");
//        }
//        PlayerIH truanteado = playerFac.find(idTruanteado);
//        if (truanteado == null) {
//            throw new BusinessLogicException("Player a truantear no existe...");
//        }
//        if (!game.getPlayersTeamA().contains(player) && !game.getPlayersTeamB().contains(player)) {
//            throw new BusinessLogicException("Debes ser participante del game para poder truantear.");
//        }
//        if (!game.getPlayersTeamA().contains(truanteado) && !game.getPlayersTeamB().contains(truanteado)) {
//            throw new BusinessLogicException("El player debe ser participante del game para poder truantearlo.");
//        }
//        if (!game.getPool().contains(player) || !game.getPool().contains(truanteado)) {
//            throw new BusinessLogicException("Debes haber participado en el mismo game para poder truantear al player.");
//        }
//        List<BanIH> bansDelGame = banFac.findByGame(game);
//        for (int i = 0; i < bansDelGame.size(); i++) {
//            BanIH ban = bansDelGame.get(i);
//            if (ban.getBanned().equals(truanteado)) {
//                //el player ya fue truanteado en este game...
//                throw new BusinessLogicException("El player ya fue baneado por truant en este game.");
//            }
//        }
//        //truanteador:truanteado
//        Map<PlayerIH, PlayerIH> truants = game.getTruants();
//        truants.put(player, truanteado);
//        game.setTruants(truants);
//        gameFac.edit(game);
//
//        Collection<PlayerIH> playersTruanteados = truants.values();
//        Iterator<PlayerIH> it = playersTruanteados.iterator();
//        int numVotos = 0;
//        while (it.hasNext()) {
//            PlayerIH p = it.next();
//            if (p.equals(truanteado)) {
//                numVotos++;
//            }
//        }
//        if (numVotos >= 7) {
//            //player debe ser baneado...
//            BanIH ban = new BanIH();
//            ban.setAdmin(null);
//            ban.setBanDays(2);
//            ban.setBanned(truanteado);
//            ban.setGame(game);
//            ban.setReason("!!!TRUANT GAME #" + game.getId() + "!!!");
//            ban.setStartDate(Calendar.getInstance());
//            banFac.create(ban);
//            truanteado.getBans().add(ban);
//            playerFac.edit(truanteado);
//        }
//        return truanteado.getUsuario().getPerfil().getNickw3();
//    }
//
//    /*
//     * Reporta el resultado del game. 
//     * El reportador debe estar en ese game,
//     * y el game debe estar en la fase PLAYING. 
//     * Con 7 votos se reporta un game.
//     */
//    public void report(Long idGame, Integer reporte) throws BusinessLogicException {
//        if (reporte == null) {
//            throw new BusinessLogicException("Reporte inválido.");
//        }
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        PlayerIH player = user.getPlayerIH();
//        
//        GameIH game = gameFac.find(idGame);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe...");
//        }
//        if (!game.getStatus().equals(StatusIH.PLAYING_PHASE)) {
//            throw new BusinessLogicException("El game debe estar en la fase de JUEGO.");
//        }
//        if (!game.getPlayersTeamA().contains(player) && !game.getPlayersTeamB().contains(player)) {
//            throw new BusinessLogicException("Debes ser participante del game para poder reportar un resultado.");
//        }
//
//        //reportador:reporte
//        //REPORTE 0 = DRAW, REPORTE < 0 = WIN_TEAM_A, REPORTE > 0 WIN_TEAM_B
//        //A FUTURO PUEDE SERVIR PARA PONER UN SCORE... :D
//        Map<PlayerIH, Integer> reports = game.getReports();
//        reports.put(player, reporte);
//        game.setReports(reports);
//        gameFac.edit(game);
//
//        //checkeamos si ya hay 7 votos iguales...
//        int votosDraw = 0;
//        int votosWinA = 0;
//        int votosWinB = 0;
//        Collection<Integer> reportes = reports.values();
//        Iterator<Integer> it = reportes.iterator();
//        while (it.hasNext()) {
//            Integer r = it.next();
//            if (r == 0) {
//                votosDraw++;
//            } else if (r < 0) {
//                votosWinA++;
//            } else {
//                votosWinB++;
//            }
//        }
//        if (votosDraw == 7) {
//            game.setStatus(StatusIH.DRAW);
//            gameFac.edit(game);
//        } else if (votosWinA == 7) {
//            game.setStatus(StatusIH.WIN_TEAM_A);
//            gameFac.edit(game);
//        } else if (votosWinB == 7) {
//            game.setStatus(StatusIH.WIN_TEAM_B);
//            gameFac.edit(game);
//        }
//
//    }
//
//    /**
//     * Capitanes
//     */
//    /*
//     * Pickea un player del pool. 
//     * El que pickea debe ser un capitan, ya sea el que creo la partida 
//     * o el primero en signear. Solo puede pickear players 
//     * que no hayan sido pickeados, y solo puede pickear
//     * players que hayan signeado al game. El game debe estar en la fase PICKS.
//     *
//     * @returns El nombre del player pickeado. 
//     */
//     public String pick(Long idGame, Long idPlayer) throws BusinessLogicException {
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        PlayerIH capitan = user.getPlayerIH();
//        
//        PlayerIH player = playerFac.find(idPlayer);
//        if (player == null) {
//            throw new BusinessLogicException("Player no existe...");
//        }
//        GameIH game = gameFac.find(idGame);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe...");
//        }
//        if (!game.getStatus().equals(StatusIH.PICK_PHASE)) {
//            throw new BusinessLogicException("El game debe estar en la fase de PICKS.");
//        }
//        boolean capitanTeamA = false;
//        boolean capitanTeamB = false;
//        if (game.getPlayersTeamA().get(0).equals(capitan)) {
//            capitanTeamA = true;
//        } else if (game.getPlayersTeamB().get(0).equals(capitan)) {
//            capitanTeamB = true;
//        } else {
//            throw new BusinessLogicException("Debes ser el capitán de alguno de los teams para pickear players.");
//        }
//        if (!game.getPool().contains(player)) {
//            throw new BusinessLogicException("El player debe estar en el pool del game...");
//        }
//        if (game.getPlayersTeamA().contains(player)) {
//            throw new BusinessLogicException("El player ya pertenece al team A.");
//        }
//        if (game.getPlayersTeamB().contains(player)) {
//            throw new BusinessLogicException("El player ya pertenece al team B.");
//        }
//        if (pickeaTeamA(game)) {
//            if (capitanTeamB) {
//                throw new BusinessLogicException("Le toca al capitán del team A pickear.");
//            }
//            
//            game.getPlayersTeamA().add(player);
//            
//            if (game.getPlayersTeamA().size() == 5 && game.getPlayersTeamB().size() == 5) {
//                game.setStatus(StatusIH.PLAYING_PHASE);
//                game.setStartDate(Calendar.getInstance());
//            }
//            
//            gameFac.edit(game);
//            player.getGamesTeamA().add(game);
//            playerFac.edit(player);
//            return player.getUsuario().getPerfil().getNickw3();
//        } else if (pickeaTeamB(game)) {
//            if (capitanTeamA) {
//                throw new BusinessLogicException("Le toca al capitán del team B pickear.");
//            }
//            
//            game.getPlayersTeamB().add(player);
//            
//            if (game.getPlayersTeamA().size() == 5 && game.getPlayersTeamB().size() == 5) {
//                game.setStatus(StatusIH.PLAYING_PHASE);
//                game.setStartDate(Calendar.getInstance());
//            }
//            
//            gameFac.edit(game);
//            player.getGamesTeamB().add(game);
//            playerFac.edit(player);
//            return player.getUsuario().getPerfil().getNickw3();
//        } else {
//            throw new BusinessLogicException("ERROR SISTEMA. MÉTODO PICKS.");
//        }
//        
//    }
//
//    /*
//     * Los capitanes ponen ready. 
//     * Solo puede darse cuando hay al menos 10 players en el pool 
//     * incluyendo capitanes y el game debe estar en la fase SIGN. 
//     * Al estar ambos teams READY, ambos capitanes pasan al grupo
//     * de cada team (A y B) respectivamente.
//     */
//    public void ready(Long idGame) throws BusinessLogicException {
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        PlayerIH capitan = user.getPlayerIH();     
//        GameIH game = gameFac.find(idGame);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe...");
//        }
//        if (!game.getStatus().equals(StatusIH.SIGN_PHASE)) {
//            throw new BusinessLogicException("El game debe estar en la fase de PICKS.");
//        }
//        if (game.getPool().size() < 10) {
//            throw new BusinessLogicException("Deben haber al menos 10 players en el pool.");
//        }
//        boolean capitanTeamA = game.getPool().get(0).equals(capitan);
//        boolean capitanTeamB = game.getPool().get(1).equals(capitan);
//        if (capitanTeamA) {
//            if (game.getCapitanTeamAReady() == true) {
//                throw new BusinessLogicException("Ya estabas marcado como 'READY'.");
//            }
//            game.setCapitanTeamAReady(true);
//        }else if(capitanTeamB) {
//            if (game.getCapitanTeamBReady() == true) {
//                throw new BusinessLogicException("Ya estabas marcado como 'READY'.");
//            }
//            game.setCapitanTeamBReady(true);
//        }else{
//            throw new BusinessLogicException("Sólo los primeros 2 en el pool pueden poner 'READY' (capitanes).");
//        }
//        
//        if (game.getCapitanTeamAReady() == true && game.getCapitanTeamBReady() == true) {
//            //game ready.
//            game.getPlayersTeamA().add(game.getPool().get(0));
//            game.getPlayersTeamB().add(game.getPool().get(1));
//            game.setStatus(StatusIH.PICK_PHASE);
//            Random random = new Random();
//            game.setTeamAStartsPicking(random.nextBoolean());
//            gameFac.edit(game);
//            game.getPlayersTeamA().get(0).getGamesTeamA().add(game);
//            playerFac.edit(game.getPlayersTeamA().get(0));
//            game.getPlayersTeamB().get(0).getGamesTeamB().add(game);
//            playerFac.edit(game.getPlayersTeamB().get(0));
//        }
//        
//    }
//
//    /**
//     * Admins
//     */
//    
//    /*
//     * Se banea al player...
//     */
//    public void ban(Long idPlayer, Integer banDays, String reason) throws BusinessLogicException {
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        PlayerIH admin = user.getPlayerIH();
//        
//        PlayerIH player = playerFac.find(idPlayer);
//        if (player == null) {
//            throw new BusinessLogicException("Player no existe...");
//        }
//        if (player.getRoot()) {
//            throw new BusinessLogicException("¿A quién quieres banear? NOOB.");
//        }
//        if (!admin.getRoot() && !admin.getStaff()) {
//            throw new BusinessLogicException("Debes ser Staff para poder banear.");
//        }
//        
//        if (admin.equals(player)) {
//            throw new BusinessLogicException("No puedes banearte a ti mismo, noob.");
//        }
//        
//        BanIH ban = new BanIH();
//        ban.setAdmin(admin);
//        ban.setBanDays(banDays);
//        ban.setBanned(player);
//        ban.setGame(null);
//        ban.setReason(reason);
//        ban.setStartDate(Calendar.getInstance());
//        banFac.create(ban);
//        player.getBans().add(ban);
//        playerFac.edit(player);
//    }
//
//    /*
//     * Se desbanea al player.
//     */
//    public void removeBan(Long idPlayer, Long idBan) throws BusinessLogicException {
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        PlayerIH admin = user.getPlayerIH();
//        
//        PlayerIH player = playerFac.find(idPlayer);
//        if (player == null) {
//            throw new BusinessLogicException("Player no existe...");
//        }
//        
//        if (!admin.getRoot() && !admin.getStaff()) {
//            throw new BusinessLogicException("Debes ser Staff para poder desbanear.");
//        }
//        
//        if (admin.equals(player)) {
//            throw new BusinessLogicException("No puedes desbanearte a ti mismo, noob.");
//        }
//        List<BanIH> bans = player.getBans();
//        boolean banExiste = false;
//        for (int i = 0; i < bans.size(); i++) {
//            BanIH ban = bans.get(i);
//            if (ban.getId().equals(idBan)) {
//                banExiste = true;
//                player.getBans().remove(ban);
//                playerFac.edit(player);
//                banFac.remove(ban);
//                return ;
//            }
//        }
//        if (!banExiste) {
//            throw new BusinessLogicException("Ban no existe.");
//        }
//    }
//
//    /*
//     * Se reporta el game sin importar su reporte anterior. 
//     * Sólo se puede submit un game que está en PHASE_PLAYING
//     */
//    public void submit(Long idGame, StatusIH nuevoStatus) throws BusinessLogicException {
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        PlayerIH admin = user.getPlayerIH();
//        
//        GameIH game = gameFac.find(idGame);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe...");
//        }
//        
//        if (!nuevoStatus.equals(StatusIH.DRAW) 
//                && !nuevoStatus.equals(StatusIH.WIN_TEAM_A) 
//                && !nuevoStatus.equals(StatusIH.WIN_TEAM_B)) {
//            throw new BusinessLogicException("Solo se puede reportar un game como DRAW, WIN_TEAM_A ó WIN_TEAM_B.");
//        }
//        
//        StatusIH status = game.getStatus();
//        if (!status.equals(StatusIH.PLAYING_PHASE) 
//                && !status.equals(StatusIH.DRAW) 
//                && !status.equals(StatusIH.WIN_TEAM_A) 
//                && !status.equals(StatusIH.WIN_TEAM_B)) {
//            throw new BusinessLogicException("Solo se puede reportar un game cuando está en la fase de PLAYING o ya fue reportado como DRAW, WIN_TEAM_A ó WIN_TEAM_B.");
//        }
//        
//        if (!admin.getRoot() && !admin.getStaff()) {
//            throw new BusinessLogicException("Sólo un Staff puede reportar un game.");
//        }
//        
//        game.setStatus(nuevoStatus);
//        gameFac.edit(game);
//        
//    }
//
//    /*
//     * Métodos de utilidad.
//     */
//    private boolean pickeaTeamA(GameIH game) throws BusinessLogicException {
//        PickModeIH pickMode = game.getPickMode();
//        Boolean comienzaTeamA = game.getTeamAStartsPicking();
//        if (!game.getStatus().equals(StatusIH.PICK_PHASE)) {
//            throw new BusinessLogicException("El game no se encuentra en la fase de PICKS.");
//        }
//        if (comienzaTeamA == null) {
//            throw new BusinessLogicException("ERROR DE SISTEMA. Contactar un admin lo antes posible.");
//        }
//        if (game.getPlayersTeamA().size() == 5 && game.getPlayersTeamB().size() == 5) {
//            throw new BusinessLogicException("Ambos teams están full.");
//        }
//        int totalEnA = game.getPlayersTeamA().size();
//        int totalEnB = game.getPlayersTeamB().size();
//        if (pickMode.equals(PickModeIH.SINGLE_ALWAYS)) {
//            //1-1-1-1-1-1-1-1
//            if (comienzaTeamA) {
//                //comenzó el team A. por tanto, le toca al team A solo si hay
//                //1A 1B, 2A 2B, 3A 3B, 4A 4B
//                if (totalEnA == totalEnB && totalEnA > 0 && totalEnA < 5) {
//                    return true;
//                }else{
//                    return false;
//                }
//            }else{
//                //comenzó el team B. por tanto, le toca al team A solo si hay
//                //1A 2B, 2A 3B, 3A 4B, 4A 5B
//                if ((totalEnA + 1) == totalEnB && totalEnA > 0 && totalEnA < 5) {
//                    return true;
//                }else{
//                    return false;
//                }
//            }
//        } else if (pickMode.equals(PickModeIH.SINGLE_FIRST_AND_LAST)) {
//            //1-2-2-2-1
//            if (comienzaTeamA) {
//                //comenzó el team A. por tanto, le toca al team A solo si hay
//                //1A 1B, 2A 3B, 3A 3B, 4A 5B
//                if ((totalEnA == 1 && totalEnB == 1)
//                        || (totalEnA == 2 && totalEnB == 3)
//                        || (totalEnA == 3 && totalEnB == 3)
//                        || (totalEnA == 4 && totalEnB == 5)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            } else {
//                //comenzó el team B. por tanto, le toca al team A solo si hay
//                //1A 2B, 2A 2B, 3A 4B, 4A 4B
//                if ((totalEnA == 1 && totalEnB == 2)
//                        || (totalEnA == 2 && totalEnB == 2)
//                        || (totalEnA == 3 && totalEnB == 4)
//                        || (totalEnA == 4 && totalEnB == 4)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//
//        }else{
//            throw new BusinessLogicException("Modo de pickeo no implementado aún.");
//        }
//    }
//    
//    private boolean pickeaTeamB(GameIH game) throws BusinessLogicException {
//        PickModeIH pickMode = game.getPickMode();
//        Boolean comienzaTeamA = game.getTeamAStartsPicking();
//        if (!game.getStatus().equals(StatusIH.PICK_PHASE)) {
//            throw new BusinessLogicException("El game no se encuentra en la fase de PICKS.");
//        }
//        if (comienzaTeamA == null) {
//            throw new BusinessLogicException("ERROR DE SISTEMA. Contactar un admin lo antes posible.");
//        }
//        if (game.getPlayersTeamA().size() == 5 && game.getPlayersTeamB().size() == 5) {
//            throw new BusinessLogicException("Ambos teams están full.");
//        }
//        int totalEnA = game.getPlayersTeamA().size();
//        int totalEnB = game.getPlayersTeamB().size();
//        if (pickMode.equals(PickModeIH.SINGLE_ALWAYS)) {
//            //1-1-1-1-1-1-1-1
//            if (comienzaTeamA) {
//                //comenzó el team A. por tanto, le toca al team B solo si hay
//                //2A 1B, 3A 2B, 4A 3B, 5A 4B
//                if (totalEnA == (totalEnB + 1) && totalEnB > 0 && totalEnB < 5) {
//                    return true;
//                }else{
//                    return false;
//                }
//            }else{
//                //comenzó el team B. por tanto, le toca al team B solo si hay
//                //1A 1B, 2A 2B, 3A 3B, 4A 4B
//                if (totalEnA == totalEnB && totalEnB > 0 && totalEnB < 5) {
//                    return true;
//                }else{
//                    return false;
//                }
//            }
//        } else if (pickMode.equals(PickModeIH.SINGLE_FIRST_AND_LAST)) {
//            //1-2-2-2-1
//            if (comienzaTeamA) {
//                //comenzó el team A. por tanto, le toca al team B solo si hay
//                //2A 1B, 2A 2B, 4A 3B, 4A 4B
//                if ((totalEnA == 2 && totalEnB == 1)
//                        || (totalEnA == 2 && totalEnB == 2)
//                        || (totalEnA == 4 && totalEnB == 3)
//                        || (totalEnA == 4 && totalEnB == 4)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            } else {
//                //comenzó el team B. por tanto, le toca al team B solo si hay
//                //1A 1B, 3A 2B, 3A 3B, 5A 4B
//                if ((totalEnA == 1 && totalEnB == 1)
//                        || (totalEnA == 3 && totalEnB == 2)
//                        || (totalEnA == 3 && totalEnB == 3)
//                        || (totalEnA == 5 && totalEnB == 4)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//
//        }else{
//            throw new BusinessLogicException("Modo de pickeo no implementado aún.");
//        }
//    }
//
//    public void forceOut(Long idGame, Long idPlayer) throws BusinessLogicException {
//        PlayerIH player = this.playerFac.find(idPlayer);
//        if (player == null) {
//            throw new BusinessLogicException("Player no encontrado.");
//        }
//        GameIH game = gameFac.find(idGame);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe...");
//        }
//        if (!game.getStatus().equals(StatusIH.SIGN_PHASE)) {
//            throw new BusinessLogicException("El game debe estar en la fase de SIGNEO.");
//        }
//        List<PlayerIH> pool = game.getPool();
//        
//        if (!pool.contains(player)) {
//            throw new BusinessLogicException("Debes haber signeado al game antes para que puedas salirte del mismo.");
//        }
//        if (pool.get(0).equals(player)) {
//            throw new BusinessLogicException("Para salirte, debes CANCELAR el match por ser capitán (1°).");
//        }
//        if (pool.size() > 1 && pool.get(1).equals(player)) {
//            throw new BusinessLogicException("Para salirte, debes CANCELAR el match por ser capitán (2°).");
//        }
//
//        game.getPool().remove(player);
//        gameFac.edit(game);
//        player.getGamesSigned().remove(game);
//        playerFac.edit(player);
//    }
//    
//    public void sendMessage(Long gameId, String msg) throws BusinessLogicException {
//        Usuario user = Util.getUsuarioLogeado();
//        if (user == null) {
//            throw new BusinessLogicException("Debes estar logeado para usar esta característica.");
//        }
//        GameIH game = gameFac.find(gameId);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe.");
//        }
//        //quienes pueden usar el chat?
//        //Cualquier ADMIN-VOUCHER
//        //Si FASE PICKS: POOL
//        //Si FASE SIGN: POOL
//        //Si FASE PLAYING: TEAMS.
//        PlayerIH player = user.getPlayerIH();
//        if (player.getRoot() || player.getStaff() || player.getVoucher()) {
//            //todo ok. evitamos q entre a los otros else if.
//        }else if(game.getStatus().equals(StatusIH.PICK_PHASE) || game.getStatus().equals(StatusIH.SIGN_PHASE)) {
//            if (!game.getPool().contains(player)) {
//                throw new BusinessLogicException("Debes estar en el POOL para poder enviar un mensaje.");
//            }
//        }else if(game.getStatus().equals(StatusIH.PLAYING_PHASE)) {
//            if (!game.getPlayersTeamA().contains(player) && !game.getPlayersTeamB().contains(player)) {
//                throw new BusinessLogicException("Debes estar en algún TEAM para poder enviar un mensaje.");
//            }
//        }else{
//            throw new BusinessLogicException("El juego ya ha sido reportado, no se permite usar el chat.");
//        }
//        
//        ChatLineIH chatLine = new ChatLineIH();
//        chatLine.setFecha(Calendar.getInstance());
//        chatLine.setPlayer(player);
//        chatLine.setText(msg);
//        chatLine.setGameIH(game);
//        chatFac.create(chatLine);
//        game.getChat().add(chatLine);
//        gameFac.edit(game);
//        
//    }
//    
//    public void sendSystemMessage(Long gameId, String msg) throws BusinessLogicException {
//        GameIH game = gameFac.find(gameId);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe.");
//        }
//        ChatLineIH chatLine = new ChatLineIH();
//        chatLine.setFecha(Calendar.getInstance());
//        //chatLine.setPlayer(player);
//        chatLine.setText(msg);
//        chatLine.setGameIH(game);
//        chatFac.create(chatLine);
//        game.getChat().add(chatLine);
//        gameFac.edit(game);
//        
//    }
//
//    public void forceReject(Long idGame, Long idPlayer) throws BusinessLogicException {
//        PlayerIH player = this.playerFac.find(idPlayer);
//        if (player == null) {
//            throw new BusinessLogicException("Player no encontrado.");
//        }
//        GameIH game = gameFac.find(idGame);
//        if (game == null) {
//            throw new BusinessLogicException("Game no existe...");
//        }
//        if (!game.getStatus().equals(StatusIH.SIGN_PHASE)) {
//            throw new BusinessLogicException("El game debe estar en la fase de SIGNEO.");
//        }
//        List<PlayerIH> pool = game.getPool();
//        
//        if (!pool.contains(player)) {
//            throw new BusinessLogicException("Debes haber signeado al game antes para que puedas salirte del mismo.");
//        }
//        if (!pool.get(0).equals(player) && !(pool.size() > 1 && pool.get(1).equals(player))) {
//            throw new BusinessLogicException("Para rejectear, debes ser capitán.");
//        }
//
//        for (int i = 0; i < pool.size(); i++) {
//            PlayerIH player1 = pool.get(i);
//            player1.getGamesSigned().remove(game);
//            playerFac.edit(player1);
//        }
//        List<PlayerIH> playersTeamA = game.getPlayersTeamA();
//        for (int i = 0; i < playersTeamA.size(); i++) {
//            PlayerIH player1 = playersTeamA.get(i);
//            player1.getGamesTeamA().remove(game);
//            playerFac.edit(player1);
//        }
//        List<PlayerIH> playersTeamB = game.getPlayersTeamB();
//        for (int i = 0; i < playersTeamB.size(); i++) {
//            PlayerIH player1 = playersTeamB.get(i);
//            player1.getGamesTeamB().remove(game);
//            playerFac.edit(player1);
//        }
//        gameFac.remove(game);
//    }
//
//    public GameIH getCurrentGame(Long idPlayer) throws BusinessLogicException {
//        //POSIBLES CURRENT GAMES.
//        //FASE SIGNEO, FASE PICKEO O CURRENTLY PLAYING.
//        PlayerIH player = this.playerFac.find(idPlayer);
//        if (player == null) {
//            throw new BusinessLogicException("Player no encontrado.");
//        }
//        GameIH game = gameFac.getGamePendienteSignByPlayer(player);
//        if (game == null) {
//            game = gameFac.getGamePendientePickByPlayer(player);
//            if (game == null) {
//                game = gameFac.getGamePendientePlayingByPlayer(player);
//            }
//        }
//        return game;
//    }
//    
//}
