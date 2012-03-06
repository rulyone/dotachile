/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
//import model.entities.inhouse.PlayerIH;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "usuario")
@NamedQueries({
    @NamedQuery(name="Usuario.findByUsername", query="SELECT u FROM Usuario u WHERE u.username = :username"),
    @NamedQuery(name="Usuario.findByEmail", query="SELECT u FROM Usuario u WHERE u.email = :email"),
    @NamedQuery(name="Usuario.searchUsernames", query="SELECT u.username FROM Usuario u WHERE u.username LIKE :username"),
    @NamedQuery(name="Usuario.searchUsuariosByUsername", query="SELECT u FROM Usuario u WHERE u.username LIKE :username")
})
public class Usuario implements Serializable, HttpSessionBindingListener {
    
    
    

    public static Map<Usuario, HttpSession> logins = new HashMap<Usuario, HttpSession>();

    @Id
    @Column(nullable = false, unique = true, length = 20)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true, length = 64)
    private String email;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaRegistro;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date lastLogin;
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Clan clan;
    @ManyToMany(mappedBy = "invitaciones")
    private List<Clan> invitacionesDeClan;
    @ManyToMany
    @JoinTable(
        joinColumns = { @JoinColumn(name="USERNAME") },
        inverseJoinColumns = { @JoinColumn(name="GROUPNAME") }
    )
    private List<Grupo> grupos;
    @OneToOne(mappedBy = "usuario")
    private Perfil perfil;
    
    @OneToMany(mappedBy = "usuario")
    private List<Movimiento> movimientos;
//    
//    @OneToOne(mappedBy = "usuario")
//    private PlayerIH playerIH;
//
//    public PlayerIH getPlayerIH() {
//        return playerIH;
//    }
//
//    public void setPlayerIH(PlayerIH playerIH) {
//        this.playerIH = playerIH;
//    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }
    
    

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }
    
    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }
    

    public List<Clan> getInvitacionesDeClan() {
        return invitacionesDeClan;
    }

    public void setInvitacionesDeClan(List<Clan> invitacionesDeClan) {
        this.invitacionesDeClan = invitacionesDeClan;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    /**
     * Get the value of lastLogin
     *
     * @return the value of lastLogin
     */
    public Date getLastLogin() {
        return lastLogin;
    }

    /**
     * Set the value of lastLogin
     *
     * @param lastLogin new value of lastLogin
     */
    public void setLastLogin(Date lastLogin) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(lastLogin);
        cal.set(Calendar.MILLISECOND, 0);
        this.lastLogin = cal.getTime();
    }

    /**
     * Get the value of fechaRegistro
     *
     * @return the value of fechaRegistro
     */
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Set the value of fechaRegistro
     *
     * @param fechaRegistro new value of fechaRegistro
     */
    public void setFechaRegistro(Date fechaRegistro) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(fechaRegistro);
        cal.set(Calendar.MILLISECOND, 0);
        this.fechaRegistro = cal.getTime();
    }

    /**
     * Get the value of email
     *
     * @return the value of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the value of email
     *
     * @param email new value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    

    /**
     * Get the value of password
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the value of username
     *
     * @return the value of username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the value of username
     *
     * @param username new value of username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if (!this.username.equals(other.username)){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.Usuario[username=" + username + "]";
    }

    public boolean parteDeCualquierGrupo(String grupos) {
        String[] groupnames = grupos.split(",");
        for(String groupname : groupnames) {
            for(Grupo g : this.getGrupos()) {
                if(g.getGroupname().equals(groupname))
                    return true;
            }
        }
        return false;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        HttpSession session = logins.remove(this);
        if (session != null) {
            session.invalidate();
        }
        logins.put(this, event.getSession());
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        logins.remove(this);
    }

}
