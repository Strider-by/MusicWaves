package by.musicwaves.controller.util;

import by.musicwaves.controller.command.action.*;

import java.util.Arrays;

public enum ActionCommandEnum {

    REGISTER("register", new RegisterUserCommand(AccessLevelEnum.ALL)),
    LOGIN("login", new LoginCommand(AccessLevelEnum.ALL)),
    LOGOUT("logout", new LogoutCommand(AccessLevelEnum.ALL)),
    GO_TO_DEFAULT_PAGE("go_to_default_page", new GoToDefaultPageCommand(AccessLevelEnum.ALL)),
    CHANGE_LANGUAGE("change_language", new ChangeLanguageCommand(AccessLevelEnum.USER_PLUS)),
    CHANGE_PASSWORD("change_password", new ChangePasswordCommand(AccessLevelEnum.USER_PLUS)),
    CHANGE_LOGIN("change_login", new ChangeLoginCommand(AccessLevelEnum.USER_PLUS)),
    DELETE_ACCOUNT_BY_USER("delete_account", new DeleteAccountByUserCommand(AccessLevelEnum.USER_PLUS));

    private final String alias;
    private final AbstractActionCommand command;

    ActionCommandEnum(String alias, AbstractActionCommand command) {
        this.alias = alias;
        this.command = command;
    }

    public static AbstractActionCommand getCommandByAlias(String alias) {
        return Arrays.stream(values())
                .filter(command -> command.alias.equalsIgnoreCase(alias))
                .findAny()
                .map(ActionCommandEnum::getCommand)
                .orElse(GO_TO_DEFAULT_PAGE.command);
    }

    public String getAlias() {
        return alias;
    }

    public AbstractActionCommand getCommand() {
        return command;
    }
}
