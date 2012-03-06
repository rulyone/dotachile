/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entities.torneos;

/**
 *
 * @author Pablo
 */
public enum FactorK {
    DIEZ(10,"10"),
    VEINTE(20,"20"),
    TREINTA(30,"30"),
    CUARENTA(40,"40"),
    CINCUENTA(50,"50"),
    SESENTA(60,"60"),
    SETENTA(70,"70"),
    OCHENTA(80,"80"),
    NOVENTA(90,"90"),
    CIEN(100,"100");

    private final int numero;
    private final String string;

    FactorK(final int numero, final String string) {
        this.numero = numero;
        this.string = string;
    }

    public int getNumero() {
        return this.numero;
    }
    @Override
    public String toString() {
        return string;
    }

}
