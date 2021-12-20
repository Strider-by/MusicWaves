package by.musicwaves.controller.command.factory;

import javax.servlet.http.HttpServletRequest;
import by.musicwaves.controller.command.xhr.AbstractXHRCommand;
import by.musicwaves.controller.command.xhr.XHRCommand;

public class XHRCommandFactory {
    public static final String PARAM_NAME_COMMAND = "command";
    
    public XHRCommand defineCommand(HttpServletRequest request) {
        String commandParamValue = request.getParameter(PARAM_NAME_COMMAND);
        XHRCommand command = XHRCommandEnum.getCommandByAlias(commandParamValue);

        return command;
    }
}
