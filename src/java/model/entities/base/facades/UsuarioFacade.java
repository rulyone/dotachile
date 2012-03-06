/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.base.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.entities.AbstractFacade;
import model.entities.base.Usuario;

/**
 *
 * @author Pablo
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {
    @PersistenceContext(unitName = "DotaCLPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    public Usuario findByUsername(String username) {
        List<Usuario> list = em.createNamedQuery("Usuario.findByUsername", Usuario.class)
                .setParameter("username", username)
                .getResultList();
        return list.size()==1? list.get(0):null;
    }

    public Usuario findByEmail(String email) {
        List<Usuario> list = em.createNamedQuery("Usuario.findByEmail", Usuario.class)
                .setParameter("email", email)
                .getResultList();
        return list.size()==1? list.get(0):null;
    }

    public List<String> searchUsernames(String username, int maxResults) {
        return em.createNamedQuery("Usuario.searchUsernames", String.class)
                .setParameter("username", "%" + username + "%")
                .setMaxResults(maxResults)
                .getResultList();
    }

    public List<Usuario> searchUsuariosByUsername(String username, int maxResults) {
        return em.createNamedQuery("Usuario.searchUsuariosByUsername", Usuario.class)
                .setParameter("username", "%" + username + "%")
                .setMaxResults(maxResults)
                .getResultList();
    }
}
