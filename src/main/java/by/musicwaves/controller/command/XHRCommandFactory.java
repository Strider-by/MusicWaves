package by.musicwaves.controller.command;

import by.musicwaves.controller.command.xhr.XHRCommand;
import by.musicwaves.controller.util.XHRCommandEnum;

import javax.servlet.http.HttpServletRequest;

import static by.musicwaves.controller.util.Constant.PARAM_NAME_COMMAND;

public class XHRCommandFactory {
    
    public XHRCommand defineCommand(HttpServletRequest request) {
        String commandParamValue = request.getParameter(PARAM_NAME_COMMAND);
        XHRCommand command = XHRCommandEnum.getCommandByAlias(commandParamValue);

        return command;
    }
}
