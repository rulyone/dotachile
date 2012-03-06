/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.entities.base;

import java.io.Serializable;
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

/**
 *
 * @author rulyone
 */
@Entity
@Table(name = "clanban")
@NamedQueries({
    @NamedQuery(name = "ClanBan.findByTag", query = "SELECT cb FROM ClanBan cb WHERE cb.clanBaneado.tag = :tag")
})
public class ClanBan implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    private Clan clanBaneado;
    @Column(nullable = false)
    private String razon;    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Clan getClanBaneado() {
        return clanBaneado;
    }

    public void setClanBaneado(Clan clanBaneado) {
        this.clanBaneado = clanBaneado;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
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
        if (!(object instanceof ClanBan)) {
            return false;
        }
        ClanBan other = (ClanBan) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.base.ClanBan[ id=" + id + " ]";
    }
    
}
