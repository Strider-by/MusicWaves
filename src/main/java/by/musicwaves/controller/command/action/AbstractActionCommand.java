package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.AbstractCommand;
import by.musicwaves.controller.exception.CommandException;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AbstractActionCommand extends AbstractCommand implements ActionCommand {

    private static final Logger LOGGER = LogManager.getLogger(AbstractActionCommand.class);
    private static final String SESSION_SERVICE_RESPONSE_ATTRIBUTE = "serviceResponse";

    public AbstractActionCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    /**
     * Attaches ServiceResponse parameter object as a session attribute so it can be accessed from jsp to build page.
     * If session doesn't exist (invalidated), it shall be created.
     *
     * @param request         - is used to get access to session
     * @param serviceResponse - unchanged is being attached to session
     */
    protected static void attachServiceResponse(HttpServletRequest request, ServiceResponse serviceResponse) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_SERVICE_RESPONSE_ATTRIBUTE, serviceResponse);
    }

    @Override
    protected void processAccessForbiddenState(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        sendToEntrancePage(request, response);
    }

}
