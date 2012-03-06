/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package webapp.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import model.entities.base.OpcionEncuesta;
import model.entities.base.facades.OpcionEncuestaFacade;

/**
 *
 * @author rulyone
 */
@FacesConverter("opcionEncuestaConverter")
public class OpcionEncuestaConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            Context ctx = (Context) new InitialContext();
            OpcionEncuestaFacade opcionFac = (OpcionEncuestaFacade) ctx.lookup("java:global/DotaCL/OpcionEncuestaFacade");
            return opcionFac.find(Long.parseLong(value));
        } catch (NamingException ex) {
            throw new ConverterException(ex);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null)
            throw new ConverterException("NULL");
        if(!(value instanceof OpcionEncuesta)) {
            throw new ConverterException("No es una opcion de encuesta...");
        }
        OpcionEncuesta opcion = (OpcionEncuesta) value;
        return opcion.getId().toString();
    }

}