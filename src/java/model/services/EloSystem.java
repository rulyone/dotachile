/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.services;

import java.io.Serializable;

/**
 *
 * @author Pablo
 */
public class EloSystem implements Serializable {
    private EloSystem() {};

    private static double getExpectedScore(int score, int scoreOponente) {
        int dif = score-scoreOponente;
        boolean high = dif >= 0? true:false;
        dif = Math.abs(dif);
        if(dif >= 0 && dif <= 3) return high?0.50d:0.50d;
        else if(dif >= 4 && dif <= 10) return high?0.51d:0.49d;
        else if(dif >= 11 && dif <= 17) return high?0.52d:0.48d;
        else if(dif >= 18 && dif <= 25) return high?0.53d:0.47d;
        else if(dif >= 26 && dif <= 32) return high?0.54d:0.46d;
        else if(dif >= 33 && dif <= 39) return high?0.55d:0.45d;
        else if(dif >= 40 && dif <= 46) return high?0.56d:0.44d;
        else if(dif >= 47 && dif <= 53) return high?0.57d:0.43d;
        else if(dif >= 54 && dif <= 61) return high?0.58d:0.42d;
        else if(dif >= 62 && dif <= 68) return high?0.59d:0.41d;
        else if(dif >= 69 && dif <= 76) return high?0.60d:0.40d;
        else if(dif >= 77 && dif <= 83) return high?0.61d:0.39d;
        else if(dif >= 84 && dif <= 91) return high?0.62d:0.38d;
        else if(dif >= 92 && dif <= 98) return high?0.63d:0.37d;
        else if(dif >= 99 && dif <= 106) return high?0.64d:0.36d;
        else if(dif >= 107 && dif <= 113) return high?0.65d:0.35;
        else if(dif >= 114 && dif <= 121) return high?0.66d:0.34d;
        else if(dif >= 122 && dif <= 129) return high?0.67d:0.33d;
        else if(dif >= 130 && dif <= 137) return high?0.68d:0.32d;
        else if(dif >= 138 && dif <= 145) return high?0.69d:0.31d;
        else if(dif >= 146 && dif <= 153) return high?0.70d:0.30d;
        else if(dif >= 154 && dif <= 162) return high?0.71d:0.29d;
        else if(dif >= 163 && dif <= 170) return high?0.72d:0.28d;
        else if(dif >= 171 && dif <= 179) return high?0.73d:0.27d;
        else if(dif >= 180 && dif <= 188) return high?0.74d:0.26d;
        else if(dif >= 189 && dif <= 197) return high?0.75d:0.25d;
        else if(dif >= 198 && dif <= 206) return high?0.76d:0.24d;
        else if(dif >= 207 && dif <= 215) return high?0.77d:0.23d;
        else if(dif >= 216 && dif <= 225) return high?0.78d:0.22d;
        else if(dif >= 226 && dif <= 235) return high?0.79d:0.21d;
        else if(dif >= 236 && dif <= 245) return high?0.80d:0.20d;
        else if(dif >= 246 && dif <= 256) return high?0.81d:0.19d;
        else if(dif >= 257 && dif <= 267) return high?0.82d:0.18d;
        else if(dif >= 268 && dif <= 278) return high?0.83d:0.17d;
        else if(dif >= 279 && dif <= 290) return high?0.84d:0.16d;
        else if(dif >= 291 && dif <= 302) return high?0.85d:0.15d;
        else if(dif >= 303 && dif <= 315) return high?0.86d:0.14d;
        else if(dif >= 316 && dif <= 328) return high?0.87d:0.13d;
        else if(dif >= 329 && dif <= 344) return high?0.88d:0.12d;
        else if(dif >= 345 && dif <= 357) return high?0.89d:0.11d;
        else if(dif >= 358 && dif <= 374) return high?0.90d:0.10d;
        else if(dif >= 375 && dif <= 391) return high?0.91d:0.09d;
        else if(dif >= 392 && dif <= 411) return high?0.92d:0.08d;
        else if(dif >= 412 && dif <= 432) return high?0.93d:0.07d;
        else if(dif >= 433 && dif <= 456) return high?0.94d:0.06d;
        else if(dif >= 457 && dif <= 484) return high?0.95d:0.05d;
        else if(dif >= 485 && dif <= 517) return high?0.96d:0.04d;
        else if(dif >= 518 && dif <= 559) return high?0.97d:0.03d;
        else if(dif >= 560 && dif <= 619) return high?0.98d:0.02d;
        else if(dif >= 620 && dif <= 735) return high?0.99d:0.01d;
        else if(dif >= 736) return high?1.00d:0.00d;

        return 0.5d;

    }

    //metodo para calcular la variacion de puntaje, basado
    //en el rating ELO.
    public static int calculoVariacion(int score, int scoreOponente, boolean gano, int factorK) {
        //formula ELO
        //variacion =  K(Score - Ex)
        //K = constante
        //Score = 1 win, 0 lose
        //Ex = expected score clan A ó B.
        double ex = EloSystem.getExpectedScore(score, scoreOponente);
        if(gano) {
            return (int) (factorK * (1 - ex));
        }else
            return (int) (factorK * (0 - ex));
    }
}
