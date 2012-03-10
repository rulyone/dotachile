/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import model.entities.base.Clan;
import utils.Util;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "desafio")
@NamedQueries({
    @NamedQuery(
        name="Desafio.findDesafiosNoAceptados",
        query="SELECT d FROM Desafio d WHERE d.desafioAceptado = FALSE"),
    @NamedQuery(
        name="Desafio.findDesafiosPendientes",
        query="SELECT d FROM Desafio d WHERE d.desafioAceptado = FALSE OR d.resultadoConfirmado = FALSE"),
    @NamedQuery(
        name="Desafio.countDesafiosPendientes",
        query="SELECT COUNT(d) FROM Desafio d WHERE d.desafioAceptado = FALSE OR d.resultadoConfirmado = FALSE"),
    @NamedQuery(
        name="Desafio.findDesafiosPendientesByTag", 
        query="SELECT d FROM Desafio d WHERE (d.desafiador.tag = :tag OR d.rival.tag = :tag) AND (d.desafioAceptado = FALSE OR d.resultadoConfirmado = FALSE)"),
    @NamedQuery(
        name="Desafio.findGamesByTag", 
        query="SELECT d.game FROM Desafio d WHERE (d.desafiador.tag = :tag OR d.rival.tag = :tag) AND d.resultadoConfirmado = TRUE ORDER BY d.fechaDesafio DESC"),
    @NamedQuery(
        name="Desafio.countByTag", 
        query="SELECT COUNT(d.game) FROM Desafio d WHERE (d.desafiador.tag = :tag OR d.rival.tag = :tag) AND d.resultadoConfirmado = TRUE"),
    @NamedQuery(
        name="Desafio.findDesafiosConfirmadosByTag",
        query="SELECT d FROM Desafio d WHERE (d.desafiador.tag = :tag OR d.rival.tag = :tag) AND d.resultadoConfirmado = TRUE ORDER BY d.fechaDesafio DESC"),
    @NamedQuery(
        name="Desafio.countConfirmadosByTag",
        query="SELECT COUNT(d) FROM Desafio d WHERE (d.desafiador.tag = :tag OR d.rival.tag = :tag) AND d.resultadoConfirmado = TRUE")
})
public class Desafio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Clan desafiador;
    @ManyToOne
    private Clan rival;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fechaDesafio;
    @OneToOne
    private Game game;
    @ManyToOne
    private Ladder ladder;
    private boolean desafioAceptado;
    private boolean resultadoConfirmado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Clan getDesafiador() {
        return desafiador;
    }

    public void setDesafiador(Clan desafiador) {
        this.desafiador = desafiador;
    }

    public boolean isDesafioAceptado() {
        return desafioAceptado;
    }

    public void setDesafioAceptado(boolean desafioAceptado) {
        this.desafioAceptado = desafioAceptado;
    }

    public Date getFechaDesafio() {
        return fechaDesafio;
    }

    public void setFechaDesafio(Date fechaDesafio) {
        this.fechaDesafio = Util.dateSinMillis(fechaDesafio);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Ladder getLadder() {
        return ladder;
    }

    public void setLadder(Ladder ladder) {
        this.ladder = ladder;
    }

    public boolean isResultadoConfirmado() {
        return resultadoConfirmado;
    }

    public void setResultadoConfirmado(boolean resultadoConfirmado) {
        this.resultadoConfirmado = resultadoConfirmado;
    }

    public Clan getRival() {
        return rival;
    }

    public void setRival(Clan rival) {
        this.rival = rival;
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
        if (!(object instanceof Desafio)) {
            return false;
        }
        Desafio other = (Desafio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.torneos.Desafio[id=" + id + "]";
    }

}
