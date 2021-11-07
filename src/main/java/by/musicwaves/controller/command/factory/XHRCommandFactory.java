package by.musicwaves.controller.command.factory;

import javax.servlet.http.HttpServletRequest;
import by.musicwaves.controller.command.xhr.XHRCommand;

public class XHRCommandFactory
{
    public final static String PARAM_NAME_COMMAND = "command";
    
    public XHRCommand defineCommand(HttpServletRequest request)
    {
        String commandParamValue = request.getParameter(PARAM_NAME_COMMAND);
        XHRCommand processor = XHRCommandEnum.getCommandByAlias(commandParamValue);

        return processor;
    }
}
