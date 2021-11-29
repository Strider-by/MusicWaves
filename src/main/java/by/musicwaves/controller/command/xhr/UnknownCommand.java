package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.servlet.XHRRequestsController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnknownCommand extends XHRCommand
{
    private final static Logger LOGGER = LogManager.getLogger(UnknownCommand.class);

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws IOException {

        LOGGER.error("UNKNOWN command reached, no data will be processed");
        // todo: log request parameters

        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    
}
