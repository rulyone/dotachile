/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dotachile.auth.converter;



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.dotachile.auth.entity.Grupo;
import com.dotachile.auth.facade.GrupoFacade;

/**
 *
 * @author Pablo
 */
@FacesConverter("grupoConverter")
public class GrupoConverter implements Converter {


    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            Context ctx = (Context) new InitialContext();
            GrupoFacade grupoFac = (GrupoFacade) ctx.lookup("java:global/DotaCL/GrupoFacade");
            return grupoFac.find(value);
        } catch (NamingException ex) {
            throw new ConverterException(ex);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null)
            throw new ConverterException("NULL");
        if(!(value instanceof Grupo)) {
            throw new ConverterException("No es un grupo...");
        }
        Grupo grupo = (Grupo) value;
        return grupo.getGroupname();
    }

}
