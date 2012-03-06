/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model.exceptions;

import javax.ejb.ApplicationException;

/**
 *
 * @author Pablo
 */
@ApplicationException(rollback = true, inherited = true)
public class BusinessLogicException extends Exception {

    /**
     * Creates a new instance of <code>BusinessLogicException</code> without detail message.
     */
    public BusinessLogicException() {
    }


    /**
     * Constructs an instance of <code>BusinessLogicException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public BusinessLogicException(String msg) {
        super(msg);
    }
}
