package by.musicwaves.controller;

import by.musicwaves.controller.command.XHRCommandFactory;
import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.command.xhr.XHRCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet works with {@link XHRCommand} commands
 */
public class XHRRequestsController extends HttpServlet {

    private final static Logger LOGGER = LogManager.getLogger(XHRRequestsController.class);
    private final static XHRCommandFactory COMMAND_FACTORY = new XHRCommandFactory();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        try {
            XHRCommand command = COMMAND_FACTORY.defineCommand(request);
            command.checkAccessAndExecute(request, response);
        } catch (CommandException ex) {
            LOGGER.error("Failed to execute command", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
