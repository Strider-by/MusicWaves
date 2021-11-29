package by.musicwaves.controller.servlet;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.factory.XHRCommandFactory;
import by.musicwaves.controller.command.xhr.XHRCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class XHRRequestsController extends HttpServlet {

    private final static Logger LOGGER = LogManager.getLogger(XHRRequestsController.class);
    private final static XHRCommandFactory COMMAND_FACTORY = new XHRCommandFactory();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        LOGGER.debug("XHRRequestsController#service reached");
        Enumeration<String> parameterNames = request.getParameterNames();

        StringBuilder sb = new StringBuilder();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            sb.append(paramName);

            String[] paramValues = request.getParameterValues(paramName);
            for (String paramValue : paramValues) {
                sb.append("\t").append(paramValue);
            }

            sb.append("\n");
        }

        LOGGER.debug("request parameters: \n" + sb.toString());
        LOGGER.debug("request url: " + request.getRequestURI());
        LOGGER.debug("request path info: " + request.getPathInfo());

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        try {
            XHRCommand command = COMMAND_FACTORY.defineCommand(request);
            command.execute(request, response);
        } catch(CommandException | IOException ex) {
            LOGGER.error("Failed to execute command", ex);
            throw new ServletException(ex);
        }
    }
}
