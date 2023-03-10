/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import model.entities.base.Clan;
import model.entities.base.Comentario;
import model.entities.base.Usuario;
import model.entities.torneos.helpers.Standing;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "torneo")
@NamedQueries({
    @NamedQuery(
        name="Torneo.findByNombre",
        query="SELECT t FROM Torneo t WHERE t.nombre = :nombre"),
    @NamedQuery(
        name="Torneo.cantidadClanesInscritos",
        query="SELECT COUNT(t.clanesInscritos) FROM Torneo t WHERE t.id = :id"),
    @NamedQuery(
        name="Torneo.findAllReverse",
        query="SELECT t FROM Torneo t ORDER BY t.id DESC")
    //TODO: CREAR NAMED QUERY CMO ESTA (TESTEAR CON LA SUNDAY CUP...)
    /*
     * mysql>   select clan.tag, count(clancampeon_id) as torneos_ganados
                from torneo, clan where torneo.clancampeon_id = clan.id group by clancampeon_id
                order by torneos_ganados desc; 
                                                    
     */
})
public class Torneo implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nombre;
    @Column(nullable = false, length = 100000)
    private String informacion;
    @OneToOne
    @JoinColumn(nullable = false)
    private Usuario encargado;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Clan> clanesInscritos;
    @Column(nullable = false)
    private TipoTorneo tipoTorneo;
    @Column(nullable = false)
    private FaseTorneo faseTorneo;
    @Column(nullable = false)
    private FactorK factorK;
    @Column(nullable = false)
    private Integer maxCantidadClanes;
    @Column(nullable = false)
    private Integer minCantidadClanes;
    @OneToMany(mappedBy = "torneo")
    private List<Ronda> rondas;
    @OneToOne
    @JoinColumn(nullable = true)
    private Clan clanCampeon;
    @OneToMany
    private List<Comentario> comentarios;

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMinCantidadClanes() {
        return minCantidadClanes;
    }

    public void setMinCantidadClanes(Integer minCantidadClanes) {
        this.minCantidadClanes = minCantidadClanes;
    }

    public Clan getClanCampeon() {
        return clanCampeon;
    }

    public void setClanCampeon(Clan clanCampeon) {
        this.clanCampeon = clanCampeon;
    }

    public List<Clan> getClanesInscritos() {
        return clanesInscritos;
    }

    public void setClanesInscritos(List<Clan> clanesInscritos) {
        this.clanesInscritos = clanesInscritos;
    }

    public Usuario getEncargado() {
        return encargado;
    }

    public void setEncargado(Usuario encargado) {
        this.encargado = encargado;
    }

    public FactorK getFactorK() {
        return factorK;
    }

    public void setFactorK(FactorK factorK) {
        this.factorK = factorK;
    }

    public FaseTorneo getFaseTorneo() {
        return faseTorneo;
    }

    public void setFaseTorneo(FaseTorneo faseTorneo) {
        this.faseTorneo = faseTorneo;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public Integer getMaxCantidadClanes() {
        return maxCantidadClanes;
    }

    public void setMaxCantidadClanes(Integer maxCantidadClanes) {
        this.maxCantidadClanes = maxCantidadClanes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Ronda> getRondas() {
        return rondas;
    }

    public void setRondas(List<Ronda> rondas) {
        this.rondas = rondas;
    }

    public TipoTorneo getTipoTorneo() {
        return tipoTorneo;
    }

    public void setTipoTorneo(TipoTorneo tipoTorneo) {
        this.tipoTorneo = tipoTorneo;
    }

    public List<Standing> getStandings() {
        List<Standing> standings = new ArrayList<Standing>();

        for(int i = 0; i < this.clanesInscritos.size(); i++) {
            Clan clan = this.clanesInscritos.get(i);
            int matchesJugados = 0;
            int matchesGanados = 0;
            int matchesPerdidos = 0;
            int gamesGanados = 0;
            int gamesPerdidos = 0;
            for(int j = 0; j < this.rondas.size(); j++) {
                Ronda ronda = this.rondas.get(j);
                for(int k = 0; k < ronda.getMatches().size(); k++) {
                    GameMatch match = ronda.getMatches().get(k);
                    if(match.isResultadoConfirmado() && (match.getClan1().equals(clan) || match.getClan2().equals(clan))) {
                        
                        int gamesToWin = (int) Math.ceil((double) match.getBestOf() / 2);
                        int gamesWonPerMatch = 0;
                        int gamesLostPerMatch = 0;
                        int dobleWosPerMatch = 0;
                        for(int l = 0; l < match.getGames().size(); l++) {
                            Game game = match.getGames().get(l);
                            if(game.getSentinel().equals(clan)) {
                                //clan es sentinel
                                if(game.getResultado().equals(Resultado.WIN_SENTINEL)
                                        || game.getResultado().equals(Resultado.WIN_SENTINEL_WO)) {
                                    //Y GANO
                                    gamesWonPerMatch++;
                                    gamesGanados++;
                                }else if(game.getResultado().equals(Resultado.WIN_SCOURGE)
                                        || game.getResultado().equals(Resultado.WIN_SCOURGE_WO)) {
                                    //Y PERDIO
                                    gamesLostPerMatch++;
                                    gamesPerdidos++;
                                }else{
                                    dobleWosPerMatch++;
                                }
                            }else{
                                //clan es scourge
                                if(game.getResultado().equals(Resultado.WIN_SCOURGE)
                                        || game.getResultado().equals(Resultado.WIN_SCOURGE_WO)) {
                                    //Y GANO
                                    gamesWonPerMatch++;
                                    gamesGanados++;
                                }else if(game.getResultado().equals(Resultado.WIN_SENTINEL)
                                        || game.getResultado().equals(Resultado.WIN_SENTINEL_WO)) {
                                    //y perdio
                                    gamesLostPerMatch++;
                                    gamesPerdidos++;
                                }else{
                                    dobleWosPerMatch++;
                                }
                            }
                        }
                        if(gamesWonPerMatch == gamesLostPerMatch) {
                            //fue DOBLE_WO el resultado final
                            matchesJugados++;
                            matchesPerdidos++;
                        }else if(gamesWonPerMatch >= gamesToWin) {
                            //gano
                            matchesJugados++;
                            matchesGanados++;
                        }else if(gamesLostPerMatch >= gamesToWin) {
                            //perdio
                            matchesJugados++;
                            matchesPerdidos++;
                        }
                    }
                }
            }
            Standing standing = new Standing(clan.getTag(), this.getNombre(), matchesJugados, matchesGanados, matchesPerdidos, gamesGanados, gamesPerdidos) ;
            standings.add(standing);
        }

        final Comparator<Standing> STANDING_ORDER = new Comparator<Standing>() {
            @Override
            public int compare(Standing s1, Standing s2) {
                //ganados, jugados, perdidos (reverse), tag
                if(s1.getPartidosGanados() < s2.getPartidosGanados())
                    return 1;
                else if(s1.getPartidosGanados() == s2.getPartidosGanados()) {
                    //
                    if(s1.getPartidosJugados() < s2.getPartidosJugados())
                        return 1;
                    else if(s1.getPartidosJugados() == s2.getPartidosJugados()) {
                        //
                        if(s1.getPartidosPerdidos() < s2.getPartidosPerdidos())
                            return -1;
                        else if(s1.getPartidosPerdidos() == s2.getPartidosPerdidos()) {
                            //
                            if(s1.getGamesGanados() < s2.getGamesGanados()) {
                                return 1;
                            } else if (s1.getGamesGanados() == s2.getGamesGanados()) {
                                //
                                if(s1.getGamesPerdidos() < s2.getGamesPerdidos()) {
                                    return -1;
                                } else if(s1.getGamesPerdidos() == s2.getGamesPerdidos()) {
                                    return s1.getTagClan().compareTo(s2.getTagClan());
                                }else
                                    return 1;
                            }else
                                return -1;                            
                        }else
                            return 1;
                    }else{
                        return -1;
                    }
                }else{
                    return -1;
                }
            }
        };

        Collections.sort(standings, STANDING_ORDER);

        return standings;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Torneo)) {
            return false;
        }
        Torneo other = (Torneo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.torneos2.Torneo[id=" + id + "]";
    }

}
