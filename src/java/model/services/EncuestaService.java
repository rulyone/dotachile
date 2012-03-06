/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import model.entities.base.Encuesta;
import model.entities.base.OpcionEncuesta;
import model.entities.base.Usuario;
import model.entities.base.facades.EncuestaFacade;
import model.entities.base.facades.OpcionEncuestaFacade;
import model.entities.base.facades.UsuarioFacade;
import model.exceptions.BusinessLogicException;

/**
 *
 * @author rulyone
 */
@Stateless
@DeclareRoles({"ADMIN_ROOT", "ADMIN_DOTA"})
@RolesAllowed({"ADMIN_ROOT", "ADMIN_DOTA"})
public class EncuestaService {

    @Resource SessionContext ctx;
    
    @EJB private EncuestaFacade encFac;
    @EJB private OpcionEncuestaFacade opcionFac;
    @EJB private UsuarioFacade userFac;

    public void crearEncuesta(
            String pregunta,
            List<String> opciones,
            boolean multiple) throws BusinessLogicException {

        if(pregunta == null || opciones == null)
            throw new BusinessLogicException("Valor requerido.");

        Encuesta encuesta = encFac.findByPregunta(pregunta);
        if(encuesta != null)
            throw new BusinessLogicException("Pregunta ya fue utilizada en otra encuesta, elige otra.");
        
        if(opciones.size() < 2)
            throw new BusinessLogicException("Para crear una encuesta deben existir al menos 2 opciones.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario creador = userFac.findByUsername(principal.getName());
        if(creador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        encuesta = new Encuesta();
        encuesta.setCreador(creador);
        encuesta.setFechaCreacion(new Date());
        encuesta.setMultiple(multiple);
        encuesta.setPregunta(pregunta);

        List<OpcionEncuesta> opcionesEncuesta = new ArrayList<OpcionEncuesta>(opciones.size());
        for(int i = 0; i < opciones.size(); i++) {
            OpcionEncuesta opcion = new OpcionEncuesta();
            opcion.setOpcion(opciones.get(i));
            opcion.setEncuesta(encuesta);
            opcionesEncuesta.add(opcion);
        }

        encuesta.setOpciones(opcionesEncuesta);
        encFac.create(encuesta);
    }

    public void cerrarEncuesta(Long idEncuesta) throws BusinessLogicException {
        if(idEncuesta == null)
            throw new BusinessLogicException("Valor requerido.");

        Encuesta encuesta = encFac.find(idEncuesta);
        if(encuesta == null)
            throw new BusinessLogicException("Encuesta no encontrada.");

        if(encuesta.getFechaCierre() != null)
            throw new BusinessLogicException("Encuesta ya estaba cerrada (" + encuesta.getFechaCierre() + ").");

        encuesta.setFechaCierre(new Date());
        encFac.edit(encuesta);
    }

    public void reabrirEncuesta(Long idEncuesta) throws BusinessLogicException {
        if(idEncuesta == null)
            throw new BusinessLogicException("Valor requerido.");

        Encuesta encuesta = encFac.find(idEncuesta);
        if(encuesta == null)
            throw new BusinessLogicException("Encuesta no encontrada.");

        if(encuesta.getFechaCierre() == null)
            throw new BusinessLogicException("Encuesta ya estaba abierta.");

        encuesta.setFechaCierre(null);
        encFac.edit(encuesta);
    }

    @RolesAllowed({"ADMIN_ROOT"})
    public void eliminarEncuesta(Long idEncuesta) throws BusinessLogicException {
        if(idEncuesta == null)
            throw new BusinessLogicException("Valor requerido.");
        Encuesta encuesta = encFac.find(idEncuesta);
        if(encuesta == null)
            throw new BusinessLogicException("Encuesta no encontrada.");

        List<OpcionEncuesta> opciones = encuesta.getOpciones();
        for(int i = 0; i < opciones.size(); i++) {
            OpcionEncuesta oe = opciones.get(i);
            opcionFac.remove(oe);
        }

        encFac.remove(encuesta);

    }

    @PermitAll
    public void votar(
            Long idEncuesta,
            List<Long> idsOpciones) throws BusinessLogicException {

        if(idEncuesta == null || idsOpciones == null)
            throw new BusinessLogicException("Valor requerido.");

        Encuesta encuesta = encFac.find(idEncuesta);
        if(encuesta == null)
            throw new BusinessLogicException("Encuesta no encontrada.");

        if(idsOpciones.isEmpty())
            throw new BusinessLogicException("Debes escojer al menos 1 opción.");
        if(!encuesta.isMultiple() && idsOpciones.size() != 1)
            throw new BusinessLogicException("No se admiten multiples selecciones.");

        List<OpcionEncuesta> opciones = new ArrayList<OpcionEncuesta>(idsOpciones.size());
        for(int i = 0; i < idsOpciones.size(); i++) {
            OpcionEncuesta opcion = opcionFac.find(idsOpciones.get(i));
            if(opcion == null)
                throw new BusinessLogicException("Opción no encontrada.");
            opciones.add(opcion);
        }
        //No debiesemos entrar nunca a este if... sanity check.
        if(!encuesta.getOpciones().containsAll(opciones))
            throw new BusinessLogicException("La opción no es parte de la encuesta.");

        Principal principal = ctx.getCallerPrincipal();
        Usuario votador = userFac.findByUsername(principal.getName());
        if(votador == null)
            throw new BusinessLogicException("Debes estar logeado para usar esta caracteristica.");

        List<OpcionEncuesta> votos = opcionFac.findVotosUsuarioPorEncuesta(votador, encuesta);

        if(votos != null && votos.size() > 0)
            throw new BusinessLogicException("Ya haz votado en esta encuesta.");

        //todo ok?

        for(int i = 0; i < opciones.size(); i++) {
            OpcionEncuesta oe = opciones.get(i);
            oe.getVotadores().add(votador);
            opcionFac.edit(oe);
        }

    }
 
}
