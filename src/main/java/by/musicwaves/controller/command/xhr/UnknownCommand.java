package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.resource.AccessLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UnknownCommand extends AbstractXHRCommand {

    private final static Logger LOGGER = LogManager.getLogger(UnknownCommand.class);

    public UnknownCommand(AccessLevel accessLevel) {
        super(accessLevel);
    }

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {

        LOGGER.error("UNKNOWN command reached, no data will be processed");
        // todo: log request parameters
        sendBadRequestError(response);
    }
}
