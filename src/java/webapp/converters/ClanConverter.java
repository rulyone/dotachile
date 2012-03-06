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
import model.entities.base.Clan;
import model.entities.base.facades.ClanFacade;

/**
 *
 * @author rulyone
 */
@FacesConverter("clanConverter")
public class ClanConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            Context ctx = (Context) new InitialContext();
            ClanFacade clanFac = (ClanFacade) ctx.lookup("java:global/DotaCL/ClanFacade");
            return clanFac.findByTag(value);
        } catch (NamingException ex) {
            throw new ConverterException(ex);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null)
            throw new ConverterException("NULL");
        if(!(value instanceof Clan)) {
            throw new ConverterException("No es un clan...");
        }
        Clan clan = (Clan) value;
        return clan.getTag();
    }

}
