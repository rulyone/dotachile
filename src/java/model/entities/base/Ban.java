/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
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
import utils.Util;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "ban")
@NamedQueries({
    @NamedQuery(name="Ban.findByBaneadoUsername", query="SELECT b FROM Ban b WHERE b.baneado.username = :username"),
    @NamedQuery(name="Ban.findByBaneadorUsername", query="SELECT b FROM Ban b WHERE b.baneador.username = :username")
})
public class Ban implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(nullable = false)
    private Usuario baneador;
    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    private Usuario baneado;
    @Column(nullable = false)
    private String razonBan;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(nullable = false)
    private Date fechaBan;
    @Column(nullable = false)
    private Integer diasBan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getBaneado() {
        return baneado;
    }

    public void setBaneado(Usuario baneado) {
        this.baneado = baneado;
    }

    public Usuario getBaneador() {
        return baneador;
    }

    public void setBaneador(Usuario baneador) {
        this.baneador = baneador;
    }

    public Integer getDiasBan() {
        return diasBan;
    }

    public void setDiasBan(Integer diasBan) {
        this.diasBan = diasBan;
    }

    public Date getFechaBan() {
        return fechaBan;
    }

    public void setFechaBan(Date fechaBan) {
        this.fechaBan = Util.dateSinTime(fechaBan);
    }

    public String getRazonBan() {
        return razonBan;
    }

    public void setRazonBan(String razonBan) {
        this.razonBan = razonBan;
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
        if (!(object instanceof Ban)) {
            return false;
        }
        Ban other = (Ban) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.base.Ban[id=" + id + "]";
    }

}
