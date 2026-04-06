///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package model.services.inhouse;
//
//
//import java.util.List;
//import javax.ejb.EJB;
//import javax.ejb.Stateless;
//import javax.ejb.LocalBean;
//import model.entities.base.Usuario;
//import model.entities.base.facades.UsuarioFacade;
//import model.entities.inhouse.GameIH;
//import model.entities.inhouse.PlayerIH;
//import model.entities.inhouse.facades.GameIHFacade;
//import model.entities.inhouse.facades.PlayerIHFacade;
//
///**
// *
// * @author rulyone
// */
//@Stateless
//@LocalBean
//public class SearchService {
//
//    private @EJB PlayerIHFacade playerFac;
//    private @EJB GameIHFacade gameFac;
//    private @EJB UsuarioFacade userFac;
//    
////    public PlayerIH searchByUsername(String username) {
////        return playerFac.findByUsername(username);
////    }
//    
//    public List<GameIH> getGamesPendientes() {
//        return gameFac.gamesPendientes();
//    }
//    
//    public List<GameIH> getGamesFaseSigneo() {
//        return gameFac.gamesFaseSigneo();
//    }
//    
//    public List<GameIH> getGamesFasePickeo() {
//        return gameFac.gamesFasePickeo();
//    }
//    
//    public List<GameIH> getGamesFaseJuego() {
//        return gameFac.gamesFaseJuego();
//    }
//    
//    public List<GameIH> getGamesFaseSigneoByPlayer(PlayerIH player) {
//        return gameFac.gamesFaseSigneoByPlayer(player);
//    }
//    
//    public List<GameIH> getGamesFasePickByPlayer(PlayerIH player) {
//        return gameFac.gamesFasePickByPlayer(player);
//    }
//    
//    public List<GameIH> getGamesFasePlayingByPlayer(PlayerIH player) {
//        return gameFac.gamesFasePlayingByPlayer(player);
//    }
//    
//    public void soloUnaVez() {
//        List<Usuario> all = userFac.findAll();
//        int size = all.size();
//        for (int i = 0; i < size; i++) {
//            Usuario usuario = all.get(i);
//            PlayerIH player = new PlayerIH();
//            player.setRoot(false);
//            player.setStaff(false);
//            player.setVoucher(false);
//            player.setUsuario(usuario);
//            playerFac.create(player);
//            usuario.setPlayerIH(player);
//            userFac.edit(usuario);
//        }
//    }
//}
