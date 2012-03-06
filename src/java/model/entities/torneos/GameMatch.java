/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import model.entities.base.Clan;
import model.entities.base.Comentario;
import model.entities.base.Usuario;
import utils.Util;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "gamematch")
@NamedQueries({
    @NamedQuery(
        name="GameMatch.listMatchesPendientes",
        query="SELECT m FROM GameMatch m WHERE m.resultadoConfirmado = false ORDER BY m.fechaMatch"),
    @NamedQuery(
        name="GameMatch.findPendientesByTag",
        query="SELECT m FROM GameMatch m WHERE m.resultadoConfirmado = false AND (m.clan1.tag = :tag OR m.clan2.tag = :tag) ORDER BY m.fechaMatch DESC"),
    @NamedQuery(
        name="GameMatch.findByGame",
        query="SELECT m FROM GameMatch m WHERE :game MEMBER OF m.games"),
    @NamedQuery(
        name="GameMatch.findGamesByTag", 
        query="SELECT m.games FROM GameMatch m WHERE m.resultadoConfirmado = TRUE AND (m.clan1.tag = :tag OR m.clan2.tag = :tag) ORDER BY m.fechaMatch DESC"),
    @NamedQuery(
        name="GameMatch.countByTag",  
        query="SELECT COUNT(m.games) FROM GameMatch m WHERE m.resultadoConfirmado = TRUE AND (m.clan1.tag = :tag OR m.clan2.tag = :tag)"),
    @NamedQuery(
        name="GameMatch.findMatchesConfirmadosByTag",
        query="SELECT m FROM GameMatch m WHERE m.resultadoConfirmado = TRUE AND (m.clan1.tag = :tag OR m.clan2.tag = :tag) ORDER BY m.fechaMatch DESC"),
    @NamedQuery(
        name="GameMatch.countMatchesConfirmadosByTag",
        query="SELECT COUNT(m) FROM GameMatch m WHERE m.resultadoConfirmado = TRUE AND (m.clan1.tag = :tag OR m.clan2.tag = :tag)")
})
public class GameMatch implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "match_games")
    private List<Game> games;
    @OneToOne
    @JoinColumn(nullable = false)
    private Clan clan1;
    @OneToOne
    @JoinColumn(nullable = false)
    private Clan clan2;
    @ManyToOne
    private Ronda ronda;
    @Column(nullable = false)
    private int bestOf;
    @Column(nullable = false)
    private boolean resultadoConfirmado;
    @OneToOne
    @JoinColumn(nullable = true)
    private Usuario arbitro;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaMatch;
    @OneToMany
    private List<Comentario> comentarios;

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public Date getFechaMatch() {
        return fechaMatch;
    }

    public void setFechaMatch(Date fechaMatch) {
        this.fechaMatch = Util.dateSinMillis(fechaMatch);
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ronda getRonda() {
        return ronda;
    }

    public void setRonda(Ronda ronda) {
        this.ronda = ronda;
    }

    public Usuario getArbitro() {
        return arbitro;
    }

    public void setArbitro(Usuario arbitro) {
        this.arbitro = arbitro;
    }

    public int getBestOf() {
        return bestOf;
    }

    public void setBestOf(int bestOf) {
        this.bestOf = bestOf;
    }

    public Clan getClan1() {
        return clan1;
    }

    public void setClan1(Clan clan1) {
        this.clan1 = clan1;
    }

    public Clan getClan2() {
        return clan2;
    }

    public void setClan2(Clan clan2) {
        this.clan2 = clan2;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public boolean isResultadoConfirmado() {
        return resultadoConfirmado;
    }

    public void setResultadoConfirmado(boolean resultadoConfirmado) {
        this.resultadoConfirmado = resultadoConfirmado;
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
        if (!(object instanceof GameMatch)) {
            return false;
        }
        GameMatch other = (GameMatch) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.torneos2.Match[id=" + id + "]";
    }


}
