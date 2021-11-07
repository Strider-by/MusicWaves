package by.musicwaves.controller.command.factory;

import javax.servlet.http.HttpServletRequest;
import by.musicwaves.controller.command.action.ActionCommand;

public class ActionCommandFactory
{
    private final static String PARAM_NAME_COMMAND = "command";
    
    public ActionCommand defineCommand(HttpServletRequest request) {
        String command = request.getParameter(PARAM_NAME_COMMAND);
        ActionCommand processor = ActionCommandEnum.getCommandByAlias(command);

        return processor;
    }
}
