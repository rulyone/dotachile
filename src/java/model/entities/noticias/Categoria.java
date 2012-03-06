/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.noticias;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "categoria")
@NamedQueries({
    @NamedQuery(name="Categoria.findByCategoryname", query="SELECT c FROM Categoria c WHERE c.categoryname = :categoryname")
})
public class Categoria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
    private String categoryname;
    @OneToOne(mappedBy = "categoria")
    private Noticia noticia;

    public Noticia getNoticia() {
        return noticia;
    }

    public void setNoticia(Noticia noticia) {
        this.noticia = noticia;
    }
    /**
     * Get the value of categoryname
     *
     * @return the value of categoryname
     */
    public String getCategoryname() {
        return categoryname;
    }

    /**
     * Set the value of categoryname
     *
     * @param categoryname new value of categoryname
     */
    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
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
        if (!(object instanceof Categoria)) {
            return false;
        }
        Categoria other = (Categoria) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.categoryname;
    }

}
