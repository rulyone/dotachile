/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import model.services.EloSystem;

/**
 *
 * @author rulyone
 */
@ManagedBean
@RequestScoped
public class CalculadorEloMB {

    private int score = 1000;
    private int scoreOponente = 1000;
    private boolean gano = true;
    private int factorK;

    private Integer variacion = null;

    /** Creates a new instance of CalculadorEloMB */
    public CalculadorEloMB() {
    }

    public int getFactorK() {
        return factorK;
    }

    public void setFactorK(int factorK) {
        this.factorK = factorK;
    }

    public boolean isGano() {
        return gano;
    }

    public void setGano(boolean gano) {
        this.gano = gano;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScoreOponente() {
        return scoreOponente;
    }

    public void setScoreOponente(int scoreOponente) {
        this.scoreOponente = scoreOponente;
    }

    public Integer getVariacion() {
        return variacion;
    }

    public void setVariacion(Integer variacion) {
        this.variacion = variacion;
    }

    public void calcular(ActionEvent e) {
        this.variacion = EloSystem.calculoVariacion(score, scoreOponente, gano, factorK);
    }

}
