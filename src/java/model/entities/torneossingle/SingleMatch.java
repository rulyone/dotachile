/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entities.torneossingle;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import model.entities.base.Comentario;
import model.entities.base.Replay;
import model.entities.base.Usuario;

/**
 *
 * @author rulyone
 */
@Entity
@Table(name = "singlematch")
public class SingleMatch implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Usuario player1;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Usuario player2;
    
    @ManyToOne
    private RondaSingle ronda;
    
    @JoinColumn(nullable = true)
    @ManyToOne
    private Usuario playerGanador;
    
    @ManyToOne
    @JoinColumn(nullable = true)
    private Usuario arbitro;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaMatch;
    
    @OneToMany
    private List<Comentario> comentarios;
    
    @OneToMany
    @JoinTable(name = "replays_singlematch")
    private List<Replay> replays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getArbitro() {
        return arbitro;
    }

    public void setArbitro(Usuario arbitro) {
        this.arbitro = arbitro;
    }

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
        this.fechaMatch = fechaMatch;
    }

    public Usuario getPlayer1() {
        return player1;
    }

    public void setPlayer1(Usuario player1) {
        this.player1 = player1;
    }

    public Usuario getPlayer2() {
        return player2;
    }

    public void setPlayer2(Usuario player2) {
        this.player2 = player2;
    }

    public Usuario getPlayerGanador() {
        return playerGanador;
    }

    public void setPlayerGanador(Usuario playerGanador) {
        this.playerGanador = playerGanador;
    }

    public List<Replay> getReplays() {
        return replays;
    }

    public void setReplays(List<Replay> replays) {
        this.replays = replays;
    }

    public RondaSingle getRonda() {
        return ronda;
    }

    public void setRonda(RondaSingle ronda) {
        this.ronda = ronda;
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
        if (!(object instanceof SingleMatch)) {
            return false;
        }
        SingleMatch other = (SingleMatch) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.torneossingle.SingleMatch[ id=" + id + " ]";
    }
    
}
