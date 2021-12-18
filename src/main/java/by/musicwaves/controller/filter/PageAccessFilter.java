package by.musicwaves.controller.filter;

import by.musicwaves.controller.resource.ApplicationPage;
import by.musicwaves.entity.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Exists to check that user gets access only to the pages it should get access to.
 * If user tries to get somewhere he is not being waited for, he shall be redirected to the entrance page.
 * Additionally this filter forwards to the actual jsp pages, parsing request URI and converting them to pages aliases.
 */
public class PageAccessFilter implements Filter {

    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";
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
        // 1 since URI is something like "/some-alias" and the very first part with index [0] is actually empty
        String pageAlias = originalUri.substring(1);

        ApplicationPage page;
        if (PAGE_ALIASES.contains(pageAlias)) {
            // page found, filtering
            page = ApplicationPage.getPageByAlias(pageAlias);
            User user = getUser(servletRequest);
            boolean accessGranted = isAccessGranted(page, user);

            if (!accessGranted) {
                // redirecting to entrance page
                page = ApplicationPage.ENTRANCE;
                ((HttpServletResponse) servletResponse).sendRedirect(page.getAlias());
            } else {
                // forwarding to requested page
                servletRequest.getRequestDispatcher(page.getPathToPage()).forward(servletRequest, servletResponse);
            }

        } else {
            // Requested address was not found among application pages. Skipping.
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private User getUser(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SESSION_ATTRIBUTE_NAME_USER);
        return user;
    }

    private boolean isAccessGranted(ApplicationPage page, User user) {
        return page.getAccessLevel().isAccessGranted(user);
    }

}