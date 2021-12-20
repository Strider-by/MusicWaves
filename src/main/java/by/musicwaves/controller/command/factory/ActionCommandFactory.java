package by.musicwaves.controller.command.factory;

import by.musicwaves.controller.command.action.ActionCommand;

import javax.servlet.http.HttpServletRequest;

public class ActionCommandFactory {

    private static final String PARAM_NAME_COMMAND = "command";

    public ActionCommand defineCommand(HttpServletRequest request) {
        String commandParamValue = request.getParameter(PARAM_NAME_COMMAND);
        ActionCommand command = ActionCommandEnum.getCommandByAlias(commandParamValue);

        return command;
    }
}
