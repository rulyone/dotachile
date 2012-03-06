/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "comentario")
public class Comentario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, length = 255)
    private String comentario;
    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(nullable = false)
    private Usuario comentador;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaComentario;
    @Column(nullable = false)
    private Boolean denegado;

    /**
     * Get the value of denegado
     *
     * @return the value of denegado
     */
    public Boolean getDenegado() {
        return denegado;
    }

    /**
     * Set the value of denegado
     *
     * @param denegado new value of denegado
     */
    public void setDenegado(Boolean denegado) {
        this.denegado = denegado;
    }

    /**
     * Get the value of fechaComentario
     *
     * @return the value of fechaComentario
     */
    public Date getFechaComentario() {
        return fechaComentario;
    }

    /**
     * Set the value of fechaComentario
     *
     * @param fechaComentario new value of fechaComentario
     */
    public void setFechaComentario(Date fechaComentario) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(fechaComentario);
        cal.set(Calendar.MILLISECOND, 0);
        this.fechaComentario = cal.getTime();
    }


    /**
     * Get the value of comentador
     *
     * @return the value of comentador
     */
    public Usuario getComentador() {
        return comentador;
    }

    /**
     * Set the value of comentador
     *
     * @param comentador new value of comentador
     */
    public void setComentador(Usuario comentador) {
        this.comentador = comentador;
    }

    /**
     * Get the value of comentario
     *
     * @return the value of comentario
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * Set the value of comentario
     *
     * @param comentario new value of comentario
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Comentario)) {
            return false;
        }
        Comentario other = (Comentario) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.Comentario[id=" + id + "]";
    }

}
