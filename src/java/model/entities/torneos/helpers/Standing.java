/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos.helpers;

/**
 *
 * @author rulyone
 */
public class Standing {
    private String tagClan;
    private String nombreTorneo;
    private Integer partidosJugados;
    private Integer partidosGanados;
    private Integer partidosPerdidos;
    private Integer gamesGanados;
    private Integer gamesPerdidos;

    public Standing(String tagClan, String nombreTorneo, Integer partidosJugados, Integer partidosGanados, Integer partidosPerdidos, Integer gamesGanados, Integer gamesPerdidos) {
        this.tagClan = tagClan;
        this.nombreTorneo = nombreTorneo;
        this.partidosJugados = partidosJugados;
        this.partidosGanados = partidosGanados;
        this.partidosPerdidos = partidosPerdidos;
        this.gamesGanados = gamesGanados;
        this.gamesPerdidos = gamesPerdidos;
    }

    /**
     * @return the tagClan
     */
    public String getTagClan() {
        return tagClan;
    }

    /**
     * @return the nombreTorneo
     */
    public String getNombreTorneo() {
        return nombreTorneo;
    }

    /**
     * @return the partidosJugados
     */
    public Integer getPartidosJugados() {
        return partidosJugados;
    }

    /**
     * @return the partidosGanados
     */
    public Integer getPartidosGanados() {
        return partidosGanados;
    }

    /**
     * @return the partidosPerdidos
     */
    public Integer getPartidosPerdidos() {
        return partidosPerdidos;
    }

    public Integer getGamesGanados() {
        return gamesGanados;
    }

    public void setGamesGanados(Integer gamesGanados) {
        this.gamesGanados = gamesGanados;
    }

    public Integer getGamesPerdidos() {
        return gamesPerdidos;
    }

    public void setGamesPerdidos(Integer gamesPerdidos) {
        this.gamesPerdidos = gamesPerdidos;
    }
    
}
