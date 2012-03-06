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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "perfil")
@NamedQueries({
    @NamedQuery(name="Perfil.findByUsername", query="SELECT p FROM Perfil p WHERE p.usuario.username = :username"),
    @NamedQuery(name="Perfil.findByNickW3", query="SELECT p FROM Perfil p WHERE p.nickw3 = :nickw3"),
    @NamedQuery(name="Perfil.findByBotW3", query="SELECT p FROM Perfil p WHERE p.botw3 = :botw3")
})
public class Perfil implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @JoinColumn(nullable = false)
    @OneToOne(optional = false)
    private Usuario usuario;
    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<Comentario> comentarios;
    @Column(length = 30, unique = true)
    private String nickw3;  // acct\\username value en la tabla BNET (con un backslash...)
    @Column(unique = true)
    private Integer uid; //uid en la table BNET...
    @Column(unique = true)
    private String botw3; // acct\\username value en la tablA bnet (CON UN BACKSLASH...)
    @Column(unique = true)
    private Integer uidBot; //uid en la table bnet

    public Integer getUidBot() {
        return uidBot;
    }

    public void setUidBot(Integer uidBot) {
        this.uidBot = uidBot;
    }

    public String getBotw3() {
        return botw3;
    }

    public void setBotw3(String botw3) {
        this.botw3 = botw3;
    }
    
    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
    
    public String getNickw3() {
        return nickw3;
    }

    public void setNickw3(String nickw3) {
        this.nickw3 = nickw3;
    }
    
    /**
     * Get the value of comentarios
     *
     * @return the value of comentarios
     */
    public List<Comentario> getComentarios() {
        return comentarios;
    }

    /**
     * Set the value of comentarios
     *
     * @param comentarios new value of comentarios
     */
    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    /**
     * Get the value of usuario
     *
     * @return the value of usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Set the value of usuario
     *
     * @param usuario new value of usuario
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
        if (!(object instanceof Perfil)) {
            return false;
        }
        Perfil other = (Perfil) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.Perfil[id=" + id + "]";
    }

}
