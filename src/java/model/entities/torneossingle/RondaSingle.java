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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import model.entities.base.Comentario;

/**
 *
 * @author rulyone
 */
@Entity
@Table(name = "rondasingle")
public class RondaSingle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @OneToMany(mappedBy = "ronda")
    private List<SingleMatch> matches;
    
    @ManyToOne
    private TorneoSingle torneo;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @OrderBy
    private Date fechaCreacion;
    
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

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<SingleMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<SingleMatch> matches) {
        this.matches = matches;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TorneoSingle getTorneo() {
        return torneo;
    }

    public void setTorneo(TorneoSingle torneo) {
        this.torneo = torneo;
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
        if (!(object instanceof RondaSingle)) {
            return false;
        }
        RondaSingle other = (RondaSingle) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.torneossingle.RondaSingle[ id=" + id + " ]";
    }
    
}
