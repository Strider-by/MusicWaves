package by.musicwaves.controller.command.factory;

import by.musicwaves.controller.command.xhr.*;

import java.util.Arrays;

public enum XHRCommandEnum
{
    UNKNOWN_COMMAND(null, new UnknownCommand()),
    CHANGE_USER_ROLE_COMMAND("change_user_role", new ChangeUserRoleCommand()),
    FIND_USERS("find_users", new FindUsersCommand()),
    DELETE_USER_BY_ADMINISTRATION("delete_user_by_admin", new DeleteeUserByAdministrationCommand());
        
    private final String alias;
    private final XHRCommand command;

    XHRCommandEnum(String alias, XHRCommand command) {
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
                .filter(command -> command.alias != null)
                .filter(command -> command.alias.equalsIgnoreCase(alias))
                .findAny()
                .map(XHRCommandEnum::getCommand)
                .orElse(UNKNOWN_COMMAND.command);
    }
}
