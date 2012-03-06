/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.Random;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.validation.constraints.NotNull;
import utils.Util;

/**
 *
 * @author rulyone
 */
@ManagedBean
@RequestScoped
public class PsuMB {

    @NotNull(message="El rut es obligatorio.")
    private Integer rut;
    @NotNull(message="El digito verificador es obligatorio.")
    private char digitoVerificador;
    
    private Boolean mostrarPuntajes = false;
    
    private int puntajeLenguaje;
    private int puntajeMatematica;
    private int puntajeCiencias;
    private int puntajeHistoria;
    
    /**
     * Creates a new instance of PsuMB
     */
    public PsuMB() {
    }
    
    @PostConstruct
    private void init() {
        Random random = new Random();
        puntajeLenguaje = 400 + random.nextInt(200);
        puntajeMatematica = 400 + random.nextInt(200);
        puntajeCiencias = 400 + random.nextInt(200);
        puntajeHistoria = 400 + random.nextInt(200);
    }
    
    public String submit() {
        boolean verificado = PsuMB.verificarRut(rut, digitoVerificador);
        if (!verificado) {
            Util.addErrorMessage("El rut no es válido.", null);
        }else{
            this.mostrarPuntajes = true;
        }
        return null;
    }
    
    private static boolean verificarRut(int rut, char dv) {
        if (dv == 'k') {
            dv = 'K';
        }
        int m = 0, s = 1;
        for (; rut != 0; rut /= 10) {
        s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return dv == (char) (s != 0 ? s + 47 : 75);
    }

    public char getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(char digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
    }

    public Boolean getMostrarPuntajes() {
        return mostrarPuntajes;
    }

    public void setMostrarPuntajes(Boolean mostrarPuntajes) {
        this.mostrarPuntajes = mostrarPuntajes;
    }

    public int getPuntajeCiencias() {
        return puntajeCiencias;
    }

    public void setPuntajeCiencias(int puntajeCiencias) {
        this.puntajeCiencias = puntajeCiencias;
    }

    public int getPuntajeHistoria() {
        return puntajeHistoria;
    }

    public void setPuntajeHistoria(int puntajeHistoria) {
        this.puntajeHistoria = puntajeHistoria;
    }

    public int getPuntajeLenguaje() {
        return puntajeLenguaje;
    }

    public void setPuntajeLenguaje(int puntajeLenguaje) {
        this.puntajeLenguaje = puntajeLenguaje;
    }

    public int getPuntajeMatematica() {
        return puntajeMatematica;
    }

    public void setPuntajeMatematica(int puntajeMatematica) {
        this.puntajeMatematica = puntajeMatematica;
    }

    public Integer getRut() {
        return rut;
    }

    public void setRut(Integer rut) {
        this.rut = rut;
    }
    
}
