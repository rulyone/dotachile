/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webapp.exceptionhandler;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 *
 * @author Pablo
 */
public class DefaultExceptionHandlerFactory extends javax.faces.context.ExceptionHandlerFactory {

    private ExceptionHandlerFactory parent;

    public DefaultExceptionHandlerFactory(
            ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler result = parent.getExceptionHandler();
        result = new CustomExceptionHandler(result);
        return result;
    }
}
