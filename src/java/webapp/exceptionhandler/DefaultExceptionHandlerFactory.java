/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webapp.exceptionhandler;

import javax.faces.context.ExceptionHandler;

/**
 *
 * @author Pablo
 */
public class DefaultExceptionHandlerFactory extends javax.faces.context.ExceptionHandlerFactory {

    private javax.faces.context.ExceptionHandlerFactory parent;

    public DefaultExceptionHandlerFactory(
            javax.faces.context.ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler result = parent.getExceptionHandler();
        result = new CustomExceptionHandler(result);
        return result;
    }
}
