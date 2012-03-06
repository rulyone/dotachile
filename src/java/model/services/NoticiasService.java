/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.services;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import model.entities.base.Usuario;
import model.entities.base.facades.UsuarioFacade;
import model.entities.noticias.Categoria;
import model.entities.noticias.Noticia;
import model.entities.noticias.facades.CategoriaFacade;
import model.entities.noticias.facades.NoticiaFacade;
import model.exceptions.BusinessLogicException;


/**
 *
 * @author Pablo
 */
@Stateless
@DeclareRoles({"ADMIN_ROOT", "ADMIN_DOTA", "ESCRITOR"})
@RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA", "ESCRITOR"})
public class NoticiasService {
    
    @Resource
    SessionContext ctx;

    @EJB NoticiaFacade noticiaFacade;
    @EJB UsuarioFacade userFac;
    @EJB CategoriaFacade catFac;
    
    @PermitAll
    public void agregarNoticiaSinEdicion(String titulo, String preview, String contenido) throws BusinessLogicException {
        if(titulo == null || preview == null || contenido == null)
            throw new BusinessLogicException("Valor requerido...");
        
        Principal principal = ctx.getCallerPrincipal();
        Usuario escritor = userFac.findByUsername(principal.getName());
        if(escritor == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica...");
        
        Categoria gral = catFac.findByCategoryname("General");
        
        Date fecha = new Date();

        Noticia noticia = new Noticia();
        noticia.setTitulo(titulo);
        noticia.setPreview(preview);
        noticia.setContenido(contenido);
        noticia.setFecha(fecha);
        noticia.setCategoria(gral);
        noticia.setEscritor(escritor);
        noticia.setLastUpdate(fecha);
        noticia.setAprobada(false);

        noticiaFacade.create(noticia);
        
    }
    
    public void aprobarNoticia(Long idNoticia) throws BusinessLogicException {
        if(idNoticia == null )
            throw new BusinessLogicException("Valor requerido...");

        Noticia noticia = noticiaFacade.find(idNoticia);
        if(noticia == null)
            throw new BusinessLogicException("Noticia no existe.");
        
        noticia.setAprobada(true);
        
        noticiaFacade.edit(noticia);
    }

    public void agregarNoticia(
            String titulo,
            String preview,
            String contenido,
            String categoryname) throws BusinessLogicException {


        if(titulo == null || preview == null || contenido == null || categoryname == null)
            throw new BusinessLogicException("Valor requerido...");

        Principal principal = ctx.getCallerPrincipal();
        Usuario escritor = userFac.findByUsername(principal.getName());
        if(escritor == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica...");

        Categoria categoria = catFac.findByCategoryname(categoryname);
        if(categoria == null)
            throw new BusinessLogicException("Categoria no existe.");

        Date fecha = new Date();

        Noticia noticia = new Noticia();
        noticia.setTitulo(titulo);
        noticia.setPreview(preview);
        noticia.setContenido(contenido);
        noticia.setFecha(fecha);
        noticia.setCategoria(categoria);
        noticia.setEscritor(escritor);
        noticia.setLastUpdate(fecha);
        noticia.setAprobada(true);

        noticiaFacade.create(noticia);

    }

    public void editarNoticia(
            Long idNoticia,
            String titulo,
            String preview,
            String contenido,
            String categoryname) throws BusinessLogicException {

        if(idNoticia == null || titulo == null || preview == null || contenido == null || categoryname == null)
            throw new BusinessLogicException("Valor requerido...");

        Noticia noticia = noticiaFacade.find(idNoticia);
        if(noticia == null)
            throw new BusinessLogicException("Noticia no existe.");

        Categoria cat = catFac.findByCategoryname(categoryname);
        if(cat == null)
            throw new BusinessLogicException("Categoria no existe.");

        noticia.setTitulo(titulo);
        noticia.setPreview(preview);
        noticia.setContenido(contenido);
        noticia.setCategoria(cat);
        noticia.setLastUpdate(new Date());

        noticiaFacade.edit(noticia);

    }

    @RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
    public void eliminarNoticia(Long idNoticia) throws BusinessLogicException {
        if(idNoticia == null)
            throw new BusinessLogicException("Valor requerido.");
        Noticia n = noticiaFacade.find(idNoticia);
        if(n == null)
            throw new BusinessLogicException("Noticia no encontrada.");

        noticiaFacade.remove(n);
    }
 
}
