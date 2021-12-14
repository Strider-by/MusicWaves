package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.AbstractCommand;
import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.dto.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class AbstractActionCommand extends AbstractCommand implements ActionCommand {

    private final static Logger LOGGER = LogManager.getLogger(AbstractActionCommand.class);
    private final static String SESSION_SERVICE_RESPONSE_ATTRIBUTE = "serviceResponse";

    public AbstractActionCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    protected void processAccessForbiddenState(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        sendToDefaultPage(request, response);
    }

    protected static void attachServiceResponse(HttpServletRequest request, ServiceResponse serviceResponse) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_SERVICE_RESPONSE_ATTRIBUTE, serviceResponse);
    }


}
