package by.musicwaves.controller.command;

import by.musicwaves.controller.command.action.ActionCommand;
import by.musicwaves.controller.util.ActionCommandEnum;

import javax.servlet.http.HttpServletRequest;

import static by.musicwaves.controller.util.Constant.PARAM_NAME_COMMAND;

public class ActionCommandFactory {

    public ActionCommand defineCommand(HttpServletRequest request) {
        String commandParamValue = request.getParameter(PARAM_NAME_COMMAND);
        ActionCommand command = ActionCommandEnum.getCommandByAlias(commandParamValue);

        return command;
    }
}
