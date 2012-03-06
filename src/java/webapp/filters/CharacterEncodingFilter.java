/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webapp.filters;

import java.io.IOException;
import java.io.Serializable;
import javax.servlet.*;

/**
 *
 * @author rulyone
 */
public class CharacterEncodingFilter implements Filter, Serializable {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
    
}
