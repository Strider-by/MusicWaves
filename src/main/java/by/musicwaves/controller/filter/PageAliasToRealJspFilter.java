package by.musicwaves.controller.filter;

import by.musicwaves.controller.resources.ApplicationPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


public class PageAliasToRealJspFilter implements Filter {


    private final static Logger LOGGER = LogManager.getLogger(PageAliasToRealJspFilter.class);
    private final static Set<String> PAGE_ALIASES;

    static {
        PAGE_ALIASES = Arrays.stream(ApplicationPage.values())
                .map(ApplicationPage::getAlias)
                .filter(s -> s != null)
                .collect(Collectors.toSet());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.debug("PageAliasToRealJspFilter class #init method reached");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        LOGGER.debug("PageAliasToRealJspFilter #doFilter");
        StringBuffer originalUrl = ((HttpServletRequest) servletRequest).getRequestURL();
        String originalUri = ((HttpServletRequest) servletRequest).getRequestURI();
        String baseName = originalUrl.substring(0, originalUrl.length() - originalUri.length());
        String pageAlias = originalUri.substring(1);
        LOGGER.debug("Original url: " + originalUrl);
        LOGGER.debug("Original uri: " + originalUri);
        LOGGER.debug("BaseName: " + baseName);
        LOGGER.debug("Page alias (possibly): " + pageAlias);
        LOGGER.debug("known page aliases: " + PAGE_ALIASES);

        ApplicationPage page;
        if (PAGE_ALIASES.contains(pageAlias)) {
            page = ApplicationPage.getPageByAlias(pageAlias);
            LOGGER.debug("Requested page found: " + pageAlias +  " -> " + page);
            servletRequest.getRequestDispatcher(page.getPathToPage()).forward(servletRequest, servletResponse);
        } else {
            LOGGER.debug("Requested address was not found among application pages. Skipping...");
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        //Filter.super.destroy();
    }

}