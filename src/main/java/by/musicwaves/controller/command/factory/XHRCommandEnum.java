package by.musicwaves.controller.command.factory;

import by.musicwaves.controller.command.xhr.UnknownCommand;
import by.musicwaves.controller.command.xhr.XHRCommand;

import java.util.Arrays;

public enum XHRCommandEnum
{
    UNKNOWN_COMMAND(null, new UnknownCommand());
        
    private final String alias;
    private final by.musicwaves.controller.command.xhr.XHRCommand command;

    XHRCommandEnum(String alias, by.musicwaves.controller.command.xhr.XHRCommand command) {
        this.alias = alias;
        this.command = command;
    }

    public String getAlias() {
        return alias;
    }

    public XHRCommand getCommand() {
        return command;
    }

    public static XHRCommand getCommandByAlias(String alias) {
        return Arrays.stream(values())
                .filter(command -> command.alias.equalsIgnoreCase(alias))
                .findAny()
                .map(XHRCommandEnum::getCommand)
                .orElse(UNKNOWN_COMMAND.command);
    }
}
