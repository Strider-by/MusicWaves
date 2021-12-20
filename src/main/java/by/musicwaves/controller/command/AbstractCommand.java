package by.musicwaves.controller.command;

import by.musicwaves.controller.command.action.AbstractActionCommand;
import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.exception.ValidationException;
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

    private static final Logger LOGGER = LogManager.getLogger(AbstractActionCommand.class);
    private static final String SESSION_ATTRIBUTE_NAME_USER = "user";
    private final AccessLevel accessLevel;

    public AbstractCommand(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * Checks if user has rights to execute specific command and either execute it (#execute method is invoked)
     * or processes forbidden state reaction (#processAccessForbiddenState is invoked).
     * Both methods are abstract and must be implemented in child classes.
     *
     * @throws CommandException - passes further an exception thrown by #execute or #processAccessForbiddenState methods
     */
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

    /**
     * Actual command execution method.
     *
     * @throws CommandException    if on any stage of command execution something went wrong.
     * @throws ValidationException if request parameters fail to pass validation.
     */
    protected abstract void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException, ValidationException;

    /**
     * Is invoked when user that requested command execution has no rights to do it. Specific behaviour is determined
     * by implementing this method in child classes.
     *
     * @throws CommandException if invocation of implemented method fails in it's task.
     */
    protected abstract void processAccessForbiddenState(HttpServletRequest request, HttpServletResponse response) throws CommandException;

    /**
     * Checks if user has rights to execute called command. Is used when any command is called.
     *
     * @return true if user has rights to execute requested command and false if he hasn't.
     */
    private boolean checkAccess(HttpServletRequest request) {
        User user = getUser(request);
        return accessLevel.isAccessGranted(user);
    }

    /**
     * Sends Forbidden 403 error as a response to a command call.
     *
     * @throws CommandException wrapped around IOException if sending error is failed.
     */
    protected void sendForbiddenError(HttpServletResponse response) throws CommandException {
        try {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (IOException ex) {
            throw new CommandException(ex);
        }
    }

    /**
     * Sends Bad request 400 error as a response to a command call.
     *
     * @throws CommandException wrapped around IOException if sending error is failed.
     */
    protected void sendBadRequestError(HttpServletResponse response) throws CommandException {
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException ex) {
            throw new CommandException(ex);
        }
    }

    /**
     * Redirects user to the page set as default. Can be called when user tries to do something that he has no rights
     * to do to.
     */
    protected void sendToDefaultPage(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        ActionCommandEnum.GO_TO_DEFAULT_PAGE.getCommand().checkAccessAndExecute(request, response);
    }

    /**
     * Redirects user to the entrance page. Designed to be called when user tries to do something that he has no rights
     * to do to show him that he must log in account that actually allows to perform requested action.
     */
    protected void sendToEntrancePage(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        try {
            transfer(request, response, ApplicationPage.ENTRANCE, TransitType.REDIRECT);
        } catch (IOException | ServletException ex) {
            throw new CommandException(ex);
        }
    }

    /**
     * Used to get user set as an attribute in session. Be sure to check if returned value is an actual User object and
     * isn't null.
     *
     * @param request is used to get access to Session object and it's attributes.
     * @return User object if it is set in session or null if it isn't.
     */
    protected User getUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(SESSION_ATTRIBUTE_NAME_USER);
    }

    /**
     * Can be used to transfer request to some Application page either via redirect or via forward.
     *
     * @param targetPage  - page to be transferred to.
     * @param transitType - type of transition.
     * @throws ServletException in some cases when forwarding is failed.
     * @throws IOException      in some cases when forwarding or redirecting is failed.
     */
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
