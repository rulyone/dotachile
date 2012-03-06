/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import model.entities.torneos.FaseTorneo;
import model.entities.torneos.Torneo;

/**
 *
 * @author Pablo
 */
@Entity
@Table(name = "clan")
@NamedQueries({
    @NamedQuery(name="Clan.listClanesOrderByFechaCreacion", query="SELECT c FROM Clan c ORDER BY c.fechaCreacion DESC"),
    @NamedQuery(name="Clan.findByNombre", query="SELECT c FROM Clan c WHERE c.nombre = :nombre"),
    @NamedQuery(name="Clan.findByTag", query="SELECT c FROM Clan c WHERE c.tag = :tag"),
    @NamedQuery(name="Clan.findClanOrderByElo", query="SELECT c FROM Clan c ORDER BY c.elo DESC"),
    @NamedQuery(name="Clan.rankClanes", query="SELECT c FROM Clan c WHERE (SELECT COUNT(g) FROM Game g WHERE g.sentinel = c OR g.scourge = c) > 0 AND c.integrantes IS NOT EMPTY ORDER BY c.elo DESC "),
    @NamedQuery(name="Clan.rankClanesCount", query="SELECT COUNT(c) FROM Clan c WHERE (SELECT COUNT(g) FROM Game g WHERE g.sentinel = c OR g.scourge = c) > 0 AND c.integrantes IS NOT EMPTY"),
    @NamedQuery(name="Clan.searchClanesByTag", query="SELECT c FROM Clan c WHERE c.tag LIKE :tag"),
    @NamedQuery(name="Clan.searchClanesByChieftain", query="SELECT c FROM Clan c WHERE c.chieftain = :chieftain")
})
public class Clan implements Serializable {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
    @Column(nullable = false, unique = true, length = 5)
    private String tag;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fechaCreacion;
    @OneToMany(mappedBy = "clan", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<Usuario> integrantes;
    @ManyToMany
    @JoinTable(name="invitaciones_clan")
    private List<Usuario> invitaciones;
    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(nullable = false)
    private Usuario chieftain;
    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name="shamanes")
    private List<Usuario> shamanes;
    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name="grunts")
    private List<Usuario> grunts;
    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name="peones")
    private List<Usuario> peones;
    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<Comentario> comentarios;
    @Column(nullable = false)
    private int elo;
    @ManyToMany(mappedBy = "clanesInscritos", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<Torneo> torneos;
    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(unique = true)
    private Imagen avatar;
    
    @OneToMany(mappedBy = "clan")
    private List<Movimiento> movimientos;

    private transient int cantidadTorneosGanados = -1;
    private transient List<Torneo> torneosGanados = null;

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }
    
    
    public Imagen getAvatar() {
        return avatar;
    }

    public void setAvatar(Imagen avatar) {
        this.avatar = avatar;
    }

    public List<Torneo> getTorneos() {
        return torneos;
    }

    public void setTorneos(List<Torneo> torneos) {
        this.torneos = torneos;
    }

    /**
     * Get the value of invitaciones
     *
     * @return the value of invitaciones
     */
    public List<Usuario> getInvitaciones() {
        return invitaciones;
    }

    /**
     * Set the value of invitaciones
     *
     * @param invitaciones new value of invitaciones
     */
    public void setInvitaciones(List<Usuario> invitaciones) {
        this.invitaciones = invitaciones;
    }

    /**
     * Get the value of integrantes
     *
     * @return the value of integrantes
     */
    public List<Usuario> getIntegrantes() {
        return integrantes;
    }

    /**
     * Set the value of integrantes
     *
     * @param integrantes new value of integrantes
     */
    public void setIntegrantes(List<Usuario> integrantes) {
        this.integrantes = integrantes;
    }

    /**
     * Get the value of elo
     *
     * @return the value of elo
     */
    public int getElo() {
        return elo;
    }

    /**
     * Set the value of elo
     *
     * @param elo new value of elo
     */
    public void setElo(int elo) {
        this.elo = elo;
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
     * Get the value of peones
     *
     * @return the value of peones
     */
    public List<Usuario> getPeones() {
        return peones;
    }

    /**
     * Set the value of peones
     *
     * @param peones new value of peones
     */
    public void setPeones(List<Usuario> peones) {
        this.peones = peones;
    }

    /**
     * Get the value of grunts
     *
     * @return the value of grunts
     */
    public List<Usuario> getGrunts() {
        return grunts;
    }

    /**
     * Set the value of grunts
     *
     * @param grunts new value of grunts
     */
    public void setGrunts(List<Usuario> grunts) {
        this.grunts = grunts;
    }

    /**
     * Get the value of shamanes
     *
     * @return the value of shamanes
     */
    public List<Usuario> getShamanes() {
        return shamanes;
    }

    /**
     * Set the value of shamanes
     *
     * @param shamanes new value of shamanes
     */
    public void setShamanes(List<Usuario> shamanes) {
        this.shamanes = shamanes;
    }

    /**
     * Get the value of chieftain
     *
     * @return the value of chieftain
     */
    public Usuario getChieftain() {
        return chieftain;
    }

    /**
     * Set the value of chieftain
     *
     * @param chieftain new value of chieftain
     */
    public void setChieftain(Usuario chieftain) {
        this.chieftain = chieftain;
    }

    /**
     * Get the value of fechaCreacion
     *
     * @return the value of fechaCreacion
     */
    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Set the value of fechaCreacion
     *
     * @param fechaCreacion new value of fechaCreacion
     */
    public void setFechaCreacion(Date fechaCreacion) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(fechaCreacion);
        cal.set(Calendar.MILLISECOND, 0);
        this.fechaCreacion = cal.getTime();
    }

    /**
     * Get the value of tag
     *
     * @return the value of tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Set the value of tag
     *
     * @param tag new value of tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Get the value of nombre
     *
     * @return the value of nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Set the value of nombre
     *
     * @param nombre new value of nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
        if (!(object instanceof Clan)) {
            return false;
        }
        Clan other = (Clan) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entities.base.Clan[id=" + id + "]";
    }

    //TODO: hacer un query...
    public List<Torneo> getTorneosActivos() {
        List<Torneo> torneosActivos = new ArrayList<Torneo>();
        if(this.getTorneos() == null)
            return torneosActivos;
        for(Torneo t : this.getTorneos()) {
            if(t.getFaseTorneo().equals(FaseTorneo.STARTED) || t.getFaseTorneo().equals(FaseTorneo.REGISTRATION)) {
                torneosActivos.add(t);
            }
        }
        return torneosActivos;
    }

    public int getScoreTorneosGanados() {
        if(this.cantidadTorneosGanados != -1)
            return this.cantidadTorneosGanados;
        this.cantidadTorneosGanados = 0;
        if(this.getTorneos() == null)
            return this.cantidadTorneosGanados;
        for(Torneo t : this.getTorneos()) {
            if(t.getClanCampeon().equals(this))
                this.cantidadTorneosGanados++;
        }
        return this.cantidadTorneosGanados;
    }

    public List<Torneo> getTOrneosGanados() {
        if(this.torneosGanados != null)
            return this.torneosGanados;
        this.torneosGanados = new ArrayList<Torneo>();
        if(this.getTorneos() == null)
            return this.torneosGanados;
        for(Torneo t : this.getTorneos()) {
            if(t.getClanCampeon().equals(this))
                this.torneosGanados.add(t);
        }
        return this.torneosGanados;
    }

}
