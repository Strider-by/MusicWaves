package by.musicwaves.controller.filter;

import by.musicwaves.controller.resources.ApplicationPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JspToPlainPageNameFilter implements Filter {


    private final static Logger LOGGER = LogManager.getLogger(JspToPlainPageNameFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.debug("JspToPlainPageNameFilter class #init method reached");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOGGER.debug("JspToPlainPageNameFilter #doFilter");
        StringBuffer originalUrl = ((HttpServletRequest) servletRequest).getRequestURL();
        String originalUri = ((HttpServletRequest) servletRequest).getRequestURI();
        String baseName = originalUrl.substring(0, originalUrl.length() - originalUri.length());
        LOGGER.debug("Original url: " + originalUrl);
        LOGGER.debug("Original uri: " + originalUri);
        LOGGER.debug("BaseName: " + baseName);
        ApplicationPage pageToBeRedirectedTo = ApplicationPage.getPageByUri(originalUri);

        // We don't know this page alias, just skip it
        if (pageToBeRedirectedTo == ApplicationPage.UNKNOWN_PAGE) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            LOGGER.debug("Target Application page is: " + pageToBeRedirectedTo);
            String redirectTo = baseName + "/" + pageToBeRedirectedTo.getAlias();
            LOGGER.debug("Redirect will be sent to: "+ redirectTo);

            ((HttpServletResponse) servletResponse).sendRedirect(redirectTo);
        }
    }

    @Override
    public void destroy() {
        //Filter.super.destroy();
    }
}
