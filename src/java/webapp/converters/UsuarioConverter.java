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
import model.entities.base.Usuario;
import model.entities.base.facades.UsuarioFacade;

/**
 *
 * @author rulyone
 */
@FacesConverter("usuarioConverter")
public class UsuarioConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            Context ctx = (Context) new InitialContext();
            UsuarioFacade userFac = (UsuarioFacade) ctx.lookup("java:global/DotaCL/UsuarioFacade");
            return userFac.findByUsername(value);
        } catch (NamingException ex) {
            throw new ConverterException(ex);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null)
            throw new ConverterException("NULL");
        if(!(value instanceof Usuario)) {
            throw new ConverterException("No es un usuario...");
        }
        Usuario user = (Usuario) value;
        return user.getUsername();
    }

}
