/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.services;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import model.entities.torneos.FaseTorneo;
import model.entities.torneossingle.*;
import model.exceptions.BusinessLogicException;

/**
 *
 * @author rulyone
 */
@Stateless
@LocalBean
public class SingleTorneoService {

    public void crearTorneo(
            String nombre,
            String informacion,
            Integer maxCantidadPlayers,
            Integer minCantidadPlayers) throws BusinessLogicException {
        
        if (nombre == null || informacion == null || maxCantidadPlayers == null || minCantidadPlayers == null) {
            throw new BusinessLogicException("Valor requerido.");
        }
        
        if (maxCantidadPlayers < 2) {
            throw new BusinessLogicException("Max cantidad de players debe ser mayor o igual a 2.");
        }
        if (minCantidadPlayers < 2) {
            throw new BusinessLogicException("Min cantidad de players debe ser mayor o igual a 2.");
        }
        if (maxCantidadPlayers < minCantidadPlayers) {
            throw new BusinessLogicException("Max cantidad de players debe ser mayor o igual a Min cantidad de players.");
        }   
        
        TorneoSingle torneo = new TorneoSingle();
        //torneo.setComentarios(null);
        torneo.setEncargado(null);
        torneo.setFaseTorneo(FaseTorneo.REGISTRATION);
        torneo.setInformacion(null);
        torneo.setMaxCantidadPlayers(Integer.MIN_VALUE);
        torneo.setMinCantidadPlayers(Integer.MIN_VALUE);
        torneo.setNombre(null);
        //torneo.setPlayerCampeon(null);
        //torneo.setPlayersInscritos(null);
        //torneo.setRondas(null);
        

        
    }
    
}
