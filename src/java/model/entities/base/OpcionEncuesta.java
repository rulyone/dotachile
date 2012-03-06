/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base;

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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author rulyone
 */
@Entity
@Table(name = "opcionencuesta")
@NamedQueries({
    @NamedQuery(name = "OpcionEncuesta.findVotosUsuarioPorEncuesta", query = "SELECT oe FROM OpcionEncuesta oe WHERE :votador MEMBER OF oe.votadores AND oe.encuesta = :encuesta"),
    @NamedQuery(name = "OpcionEncuesta.countVotosByIdOpcionEncuesta", query = "SELECT COUNT(oe.votadores) FROM OpcionEncuesta oe WHERE oe.id = :id"),
    @NamedQuery(name = "OpcionEncuesta.countVotosUnicosByIdEncuesta", query = "SELECT COUNT(DISTINCT oe.votadores) FROM OpcionEncuesta oe WHERE oe.encuesta.id = :id")
})
public class OpcionEncuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String opcion;

    @OneToMany
    @JoinTable(name = "votaciones_encuesta")
    private List<Usuario> votadores;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(nullable = false)
    private Encuesta encuesta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Encuesta getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(Encuesta encuesta) {
        this.encuesta = encuesta;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public List<Usuario> getVotadores() {
        return votadores;
    }

    public void setVotadores(List<Usuario> votadores) {
        this.votadores = votadores;
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
        if (!(object instanceof OpcionEncuesta)) {
            return false;
        }
        OpcionEncuesta other = (OpcionEncuesta) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.opcion;
    }

}
