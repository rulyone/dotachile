/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entities.base;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import model.entities.base.Clan;

/**
 *
 * @author rulyone
 */
@Entity
@Table(name="confirmacion")
@NamedQueries({
    @NamedQuery(
        name="Confirmacion.findByTag",
        query="SELECT c FROM Confirmacion c WHERE c.clan.tag = :tag")
})
public class Confirmacion implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @JoinColumn(unique = true, nullable = false)
    private Clan clan;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar fechaConfirmacion;

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

    public Calendar getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(Calendar fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
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
        if (!(object instanceof Confirmacion)) {
            return false;
        }
        Confirmacion other = (Confirmacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "controller.clanes.Confirmacion[ id=" + id + " ]";
    }
    
}
