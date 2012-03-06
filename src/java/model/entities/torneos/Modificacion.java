/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import model.entities.base.Clan;
import model.entities.base.Usuario;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "modificacion")
@NamedQueries({
    @NamedQuery(name="Modificacion.findByTemporadaAndClan",query="SELECT m FROM Modificacion m WHERE m.temporada = :temporada AND m.clan = :clan")
})
public class Modificacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private TemporadaModificacion temporada;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaModificacion;
    @OneToOne
    @JoinColumn(nullable = false)
    private Clan clan;
    @OneToOne
    @JoinColumn(nullable = false)
    private Usuario usuario;
    @Column(nullable = false)
    private TipoModificacion tipoModificacion;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaModificacion);
        cal.set(Calendar.MILLISECOND, 0);
        this.fechaModificacion = cal.getTime();
    }

    public TemporadaModificacion getTemporada() {
        return temporada;
    }

    public void setTemporada(TemporadaModificacion temporada) {
        this.temporada = temporada;
    }

    public TipoModificacion getTipoModificacion() {
        return tipoModificacion;
    }

    public void setTipoModificacion(TipoModificacion tipo) {
        this.tipoModificacion = tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        if (!(object instanceof Modificacion)) {
            return false;
        }
        Modificacion other = (Modificacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.torneos2.Modificacion_[id=" + id + "]";
    }

}
