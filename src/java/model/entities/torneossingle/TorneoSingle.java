/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entities.torneossingle;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import model.entities.base.Comentario;
import model.entities.base.Usuario;
import model.entities.torneos.FaseTorneo;

/**
 *
 * @author rulyone
 */
@Entity
@Table(name="torneosingle")
public class TorneoSingle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    @Column(nullable = false, length = 100000)
    private String informacion;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Usuario encargado;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="players_por_torneo")
    private List<Usuario> playersInscritos;
    
    @Column(nullable = false)
    private FaseTorneo faseTorneo;
    
    @Column(nullable = false)
    private Integer maxCantidadPlayers;
    
    @Column(nullable = false)
    private Integer minCantidadPlayers;
    
    @OneToMany(mappedBy = "torneo")
    private List<RondaSingle> rondas;
    
    @ManyToOne
    @JoinColumn(nullable = true)
    private Usuario playerCampeon;
    
    @OneToMany
    private List<Comentario> comentarios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public Usuario getEncargado() {
        return encargado;
    }

    public void setEncargado(Usuario encargado) {
        this.encargado = encargado;
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

    public Integer getMaxCantidadPlayers() {
        return maxCantidadPlayers;
    }

    public void setMaxCantidadPlayers(Integer maxCantidadPlayers) {
        this.maxCantidadPlayers = maxCantidadPlayers;
    }

    public Integer getMinCantidadPlayers() {
        return minCantidadPlayers;
    }

    public void setMinCantidadPlayers(Integer minCantidadPlayers) {
        this.minCantidadPlayers = minCantidadPlayers;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Usuario getPlayerCampeon() {
        return playerCampeon;
    }

    public void setPlayerCampeon(Usuario playerCampeon) {
        this.playerCampeon = playerCampeon;
    }

    public List<Usuario> getPlayersInscritos() {
        return playersInscritos;
    }

    public void setPlayersInscritos(List<Usuario> playersInscritos) {
        this.playersInscritos = playersInscritos;
    }

    public List<RondaSingle> getRondas() {
        return rondas;
    }

    public void setRondas(List<RondaSingle> rondas) {
        this.rondas = rondas;
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
        if (!(object instanceof TorneoSingle)) {
            return false;
        }
        TorneoSingle other = (TorneoSingle) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.torneos.TorneoSingle[ id=" + id + " ]";
    }
    
}
