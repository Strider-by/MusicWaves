package by.musicwaves.controller.filter;

import by.musicwaves.controller.resource.ApplicationPage;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class PageAliasToRealJspFilter implements Filter {

    private final static Set<String> PAGE_ALIASES;

    static {
        PAGE_ALIASES = Arrays.stream(ApplicationPage.values())
                .map(ApplicationPage::getAlias)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String originalUri = ((HttpServletRequest) servletRequest).getRequestURI();
        String pageAlias = originalUri.substring(1);

        ApplicationPage page;
        if (PAGE_ALIASES.contains(pageAlias)) {
            // page found
            page = ApplicationPage.getPageByAlias(pageAlias);
            servletRequest.getRequestDispatcher(page.getPathToPage()).forward(servletRequest, servletResponse);
        } else {
            // Requested address was not found among application pages. Skipping.
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

}