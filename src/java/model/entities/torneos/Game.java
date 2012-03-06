/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import model.entities.base.Clan;
import model.entities.base.Comentario;
import model.entities.base.Replay;
import model.entities.base.Usuario;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "game")
@NamedQueries({
    @NamedQuery(name="Game.countByClanTag", query="SELECT COUNT(g) FROM Game g WHERE g.sentinel.tag = :tag OR g.scourge.tag = :tag"),
    @NamedQuery(name="Game.findByClanes", query="SELECT g FROM Game g WHERE g.sentinel = :clan OR g.scourge = :clan")
    
})
public class Game implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(nullable = false)
    private Clan sentinel;
    @OneToOne
    @JoinColumn(nullable = false)
    private Clan scourge;
    @OneToMany
    @JoinTable(name="players_sentinel")
    private List<Usuario> playersSentinel;
    @OneToMany
    @JoinTable(name="players_scourge")
    private List<Usuario> playersScourge;
    @Column(nullable = false)
    private Resultado resultado;
    @OneToMany
    private List<Comentario> comentarios;
    @OneToOne
    private Replay replay;

    public Replay getReplay() {
        return replay;
    }

    public void setReplay(Replay replay) {
        this.replay = replay;
    }

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

    public List<Usuario> getPlayersScourge() {
        return playersScourge;
    }

    public void setPlayersScourge(List<Usuario> playersScourge) {
        this.playersScourge = playersScourge;
    }

    public List<Usuario> getPlayersSentinel() {
        return playersSentinel;
    }

    public void setPlayersSentinel(List<Usuario> playersSentinel) {
        this.playersSentinel = playersSentinel;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public Clan getScourge() {
        return scourge;
    }

    public void setScourge(Clan scourge) {
        this.scourge = scourge;
    }

    public Clan getSentinel() {
        return sentinel;
    }

    public void setSentinel(Clan sentinel) {
        this.sentinel = sentinel;
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
        if (!(object instanceof Game)) {
            return false;
        }
        Game other = (Game) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.torneos2.Game_[id=" + id + "]";
    }

}
