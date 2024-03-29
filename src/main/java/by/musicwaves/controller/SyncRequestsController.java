package by.musicwaves.controller;

import by.musicwaves.controller.command.action.ActionCommand;
import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.command.factory.ActionCommandFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet works with {@link ActionCommand} commands
 */
public class SyncRequestsController extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(SyncRequestsController.class);
    private static final ActionCommandFactory COMMAND_FACTORY = new ActionCommandFactory();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ActionCommand commandProcessor = COMMAND_FACTORY.defineCommand(request);

        try {
            commandProcessor.checkAccessAndExecute(request, response);
        } catch (CommandException ex) {
            LOGGER.error("Command failed to be executed", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
