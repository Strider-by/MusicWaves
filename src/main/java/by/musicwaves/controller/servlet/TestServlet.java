package by.musicwaves.controller.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@WebServlet(urlPatterns = {"/test/*"})
public class TestServlet extends HttpServlet
{
    private static final Logger LOGGER = LogManager.getLogger(TestServlet.class);

    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
   
        Enumeration<String> parameterNames = request.getParameterNames();

        StringBuilder sb = new StringBuilder();
        while (parameterNames.hasMoreElements())
        {
            String paramName = parameterNames.nextElement();
            sb.append(paramName);

            String[] paramValues = request.getParameterValues(paramName);
            for (String paramValue : paramValues)
            {
                sb.append("\t").append(paramValue);
            }
        }
        
        LOGGER.debug("request parameters: " + sb.toString());
        LOGGER.debug("request url: " + request.getRequestURI());
        LOGGER.debug("request path info: " + request.getPathInfo());
      
        //request.getServletContext().getRequestDispatcher("/static/css/entrance.css").forward(request, response);
        request.getServletContext().getRequestDispatcher("/static/css/entrance.css").include(request, response);
        
    }
}
