package by.musicwaves.controller.filter;

import javax.servlet.*;
import java.io.IOException;


/**
 * This filter sets encoding for all incoming ServletResponse and ServletRequest objects
 * if their encoding is different from what it should be.
 */
public class EncodingSetFilter implements Filter {

    private String encoding;

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        encoding = fConfig.getInitParameter("encoding");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        String codeRequest = request.getCharacterEncoding();
        if (encoding != null && !encoding.equalsIgnoreCase(codeRequest)) {
            request.setCharacterEncoding(encoding);
            response.setCharacterEncoding(encoding);
        }

        chain.doFilter(request, response);
    }
}

