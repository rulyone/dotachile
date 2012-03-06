/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import utils.Util;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "temporadamodificacion")
@NamedQueries({
    @NamedQuery(name="TemporadaModificacion.findByDate",query="SELECT t FROM TemporadaModificacion t WHERE :date BETWEEN t.startDate AND t.endDate"),
    @NamedQuery(name="TemporadaMOdificacion.findAllReverse",query="SELECT t FROM TemporadaModificacion t ORDER BY t.startDate DESC")
})
public class TemporadaModificacion implements Serializable {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(nullable = false)
    private Date endDate;
    @OneToMany(mappedBy = "temporada", cascade = CascadeType.ALL)
    private List<Modificacion> modificaciones;
    @Column(nullable = false)
    private int maxAgregaciones;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMaxAgregaciones() {
        return maxAgregaciones;
    }

    public void setMaxAgregaciones(int maxAgregaciones) {
        this.maxAgregaciones = maxAgregaciones;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = Util.dateSinTime(endDate);
    }

    public List<Modificacion> getModificaciones() {
        return modificaciones;
    }

    public void setModificaciones(List<Modificacion> modificaciones) {
        this.modificaciones = modificaciones;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = Util.dateSinTime(startDate);
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
        if (!(object instanceof TemporadaModificacion)) {
            return false;
        }
        TemporadaModificacion other = (TemporadaModificacion) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.torneos2.TemporadaModificacion[id=" + id + "]";
    }

    //metodos por un bug de <p:calendar mindate maxdate.
    public String getStartDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy");
        return format.format(startDate);
    }
    public String getEndDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy");
        return format.format(endDate);
    }

}
