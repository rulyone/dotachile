/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webapp.filters;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rulyone
 */
public class CacheControlFilter implements Filter, Serializable {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        long cacheAge = 60 * 60 * 24 * 7; //1 semana en segundos
        long expiry = new Date().getTime() + cacheAge * 1000; //1 semana de cache en milisegundos

        HttpServletResponse httpResponse = (HttpServletResponse)response;
        httpResponse.setDateHeader("Expires", expiry);
        httpResponse.setHeader("Cache-Control", "max-age="+ cacheAge);

        chain.doFilter(request, response);
        
    }

    @Override
    public void destroy() {
    }
    
}
