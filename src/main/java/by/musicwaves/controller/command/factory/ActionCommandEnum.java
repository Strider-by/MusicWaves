package by.musicwaves.controller.command.factory;

import by.musicwaves.controller.command.action.*;
import by.musicwaves.controller.resource.AccessLevel;

import java.util.Arrays;

public enum ActionCommandEnum {

    REGISTER("register", new RegisterUserCommand(AccessLevel.ALL)),
    LOGIN("login", new LoginCommand(AccessLevel.ALL)),
    LOGOUT("logout", new LogoutCommand(AccessLevel.ALL)),
    GO_TO_DEFAULT_PAGE("go_to_default_page", new GoToDefaultPageCommand(AccessLevel.ALL)),
    CHANGE_LANGUAGE("change_language", new ChangeLanguageCommand(AccessLevel.USER_PLUS)),
    CHANGE_PASSWORD("change_password", new ChangePasswordCommand(AccessLevel.USER_PLUS)),
    CHANGE_LOGIN("change_login", new ChangeLoginCommand(AccessLevel.USER_PLUS)),
    DELETE_ACCOUNT_BY_USER("delete_account", new DeleteAccountByUserCommand(AccessLevel.USER_PLUS));

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
