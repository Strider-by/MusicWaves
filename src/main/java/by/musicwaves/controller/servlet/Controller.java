package by.musicwaves.controller.servlet;

import by.musicwaves.controller.command.action.ActionCommand;
import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.factory.ActionCommandFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@WebServlet(urlPatterns = {"/action/*"}) // todo: move to xml
public class Controller extends HttpServlet {

    private final static Logger LOGGER = LogManager.getLogger(Controller.class);
    private final static ActionCommandFactory COMMAND_FACTORY = new ActionCommandFactory();

    @Override
    protected void service(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

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

        ActionCommand commandProcessor = COMMAND_FACTORY.defineCommand(request);

        try {
            commandProcessor.execute(request, response);
        } catch (CommandException ex) {
            LOGGER.error("Command failed to be executed", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
