package by.musicwaves.controller.servlet;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.action.ActionCommand;
import by.musicwaves.controller.command.factory.ActionCommandFactory;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@WebServlet(urlPatterns = {"/action/*"})
public class Controller extends HttpServlet
{
    private static final Logger LOGGER = LogManager.getLogger(Controller.class);

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

            sb.append("\n");
        }
        
        LOGGER.debug("request parameters: \n" + sb.toString());
        LOGGER.debug("request url: " + request.getRequestURI());
        LOGGER.debug("request path info: " + request.getPathInfo());
      
        ActionCommandFactory factory = new ActionCommandFactory();
        ActionCommand commandProcessor = factory.defineCommand(request);
        
        try
        {
            commandProcessor.execute(request, response);
        }
        catch(CommandException ex)
        {
            LOGGER.error("Command failed to be executed", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
    }
}
