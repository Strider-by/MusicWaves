package by.musicwaves.controller.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


public class AntiInjectionFilter implements Filter {

    private static final String DOES_NOT_CONTAIN = "^((?!<|>|script).)*$";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        StringBuilder sb = new StringBuilder();
        Map<String, String[]> params = request.getParameterMap();
        for (String[] v : params.values()) {
            sb.append(v[0]);
        }
        if (sb.toString().trim().matches(DOES_NOT_CONTAIN)) {
            chain.doFilter(request, response);
        } else {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}