/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.noticias;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import model.entities.base.Comentario;
import model.entities.base.Usuario;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "noticia")
@NamedQueries({
    @NamedQuery(name="Noticia.findAll", query="SELECT n FROM Noticia n WHERE n.aprobada = TRUE ORDER BY n.fecha DESC"),
    @NamedQuery(name="Noticia.countComentariosByIdNoticia", query="SELECT COUNT(n.comentarios) FROM Noticia n WHERE n.id = :idNoticia"),
    @NamedQuery(name="Noticia.findUltimasNoticiasByCategoryname", query="SELECT n FROM Noticia n WHERE n.categoria.categoryname = :categoryname AND n.aprobada = TRUE ORDER BY n.fecha DESC"),
    @NamedQuery(name="Noticia.findUltimasNoticias", query="SELECT n FROM Noticia n WHERE n.aprobada = TRUE ORDER BY n.fecha DESC"),
    @NamedQuery(name="Noticia.findUltimasNoticiasNoAprobadas", query="SELECT n FROM Noticia n WHERE n.aprobada = FALSE ORDER BY n.fecha DESC"),
    @NamedQuery(name="Noticia.countNoAprobadas", query="SELECT COUNT(n) FROM Noticia n WHERE n.aprobada = FALSE")
})
public class Noticia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String titulo;
    @Column(nullable = false, length = 5000)
    private String preview;
    @Column(nullable = false, length = 100000)
    private String contenido;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fecha;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date lastUpdate;
    @OneToOne
    @JoinColumn(nullable = false)
    private Usuario escritor;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(nullable = false)
    private Categoria categoria;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Comentario> comentarios;
    
    @Column(nullable = false)
    private Boolean aprobada;

    public Boolean getAprobada() {
        return aprobada;
    }

    public void setAprobada(Boolean aprobada) {
        this.aprobada = aprobada;
    }    

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    /**
     * Get the value of categoria
     *
     * @return the value of categoria
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Set the value of categoria
     *
     * @param categoria new value of categoria
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }


    /**
     * Get the value of escritor
     *
     * @return the value of escritor
     */
    public Usuario getEscritor() {
        return escritor;
    }

    /**
     * Set the value of escritor
     *
     * @param escritor new value of escritor
     */
    public void setEscritor(Usuario escritor) {
        this.escritor = escritor;
    }


    /**
     * Get the value of lastUpdate
     *
     * @return the value of lastUpdate
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Set the value of lastUpdate
     *
     * @param lastUpdate new value of lastUpdate
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Get the value of fecha
     *
     * @return the value of fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Set the value of fecha
     *
     * @param fecha new value of fecha
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    /**
     * Get the value of contenido
     *
     * @return the value of contenido
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Set the value of contenido
     *
     * @param contenido new value of contenido
     */
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    /**
     * Get the value of preview
     *
     * @return the value of preview
     */
    public String getPreview() {
        return preview;
    }

    /**
     * Set the value of preview
     *
     * @param preview new value of preview
     */
    public void setPreview(String preview) {
        this.preview = preview;
    }

    /**
     * Get the value of titulo
     *
     * @return the value of titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Set the value of titulo
     *
     * @param titulo new value of titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
        if (!(object instanceof Noticia)) {
            return false;
        }
        Noticia other = (Noticia) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.noticias.Noticia[id=" + id + "]";
    }

}
