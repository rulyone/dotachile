/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import model.entities.base.Comentario;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "ladder")
public class Ladder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nombre;
    @Column(nullable = false, length = 10000)
    private String informacion;
    @Column(nullable = false)
    private FaseLadder faseLadder;
    @Column(nullable = false)
    private FactorK factorK;
    @OneToMany(mappedBy = "ladder")
    private List<Desafio> desafios;
    @OneToMany
    private List<Comentario> comentarios;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Desafio> getDesafios() {
        return desafios;
    }

    public void setDesafios(List<Desafio> desafios) {
        this.desafios = desafios;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public FactorK getFactorK() {
        return factorK;
    }

    public void setFactorK(FactorK factorK) {
        this.factorK = factorK;
    }

    public FaseLadder getFaseLadder() {
        return faseLadder;
    }

    public void setFaseLadder(FaseLadder faseLadder) {
        this.faseLadder = faseLadder;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        if (!(object instanceof Ladder)) {
            return false;
        }
        Ladder other = (Ladder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.torneos.Ladder[id=" + id + "]";
    }

}
