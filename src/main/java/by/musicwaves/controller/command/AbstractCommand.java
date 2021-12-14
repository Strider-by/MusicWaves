package by.musicwaves.controller.command;

import by.musicwaves.controller.command.action.AbstractActionCommand;
import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.command.exception.ValidationException;
import by.musicwaves.controller.command.factory.ActionCommandEnum;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.controller.resource.ApplicationPage;
import by.musicwaves.controller.resource.TransitType;
import by.musicwaves.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractCommand {

    private final static Logger LOGGER = LogManager.getLogger(AbstractActionCommand.class);
    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";
    private final AccessLevel accessLevel;

    public AbstractCommand(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public void checkAccessAndExecute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        boolean accessGranted = checkAccess(request);

        if (accessGranted) {
            try {
                execute(request, response);
            } catch (ValidationException ex) {
                LOGGER.error("Bad request parameter", ex);
                sendBadRequestError(response);
            }
        } else {
            processAccessForbiddenState(request, response);
        }
    }

    protected abstract void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException;
    protected abstract void processAccessForbiddenState(HttpServletRequest request, HttpServletResponse response) throws CommandException;

    protected boolean checkAccess(HttpServletRequest request) {
        User user = getUser(request);
        return accessLevel.isAccessGranted(user);
    }

    protected void sendForbiddenError(HttpServletResponse response) throws CommandException {
        try {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (IOException ex) {
            throw new CommandException(ex);
        }
    }

    protected void sendBadRequestError(HttpServletResponse response) throws CommandException {
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException ex) {
            throw new CommandException(ex);
        }
    }

    protected void sendToDefaultPage(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        ActionCommandEnum.GO_TO_DEFAULT_PAGE.getCommand().checkAccessAndExecute(request, response);
    }

    protected User getUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(SESSION_ATTRIBUTE_NAME_USER);
    }

    protected void transfer(HttpServletRequest request, HttpServletResponse response,
                         ApplicationPage targetPage, TransitType transitType) throws ServletException, IOException {

        switch (transitType) {
            case FORWARD:
                RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(targetPage.getPathToPage());
                dispatcher.forward(request, response);
                break;

            case REDIRECT:
                response.sendRedirect(targetPage.getAlias());
                break;
        }
    }
}
